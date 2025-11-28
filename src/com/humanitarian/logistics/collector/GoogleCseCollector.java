package com.humanitarian.logistics.collector;

import com.humanitarian.logistics.config.GoogleCseConfig;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;
import com.humanitarian.logistics.util.CustomRateLimiter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleCseCollector extends Collector<SearchCriteria, OkHttpClient, List<SocialPost>> {
    private static final Logger logger = LoggerFactory.getLogger(GoogleCseCollector.class);

    private final GoogleCseConfig config;
    private final CustomRateLimiter rateLimiter;
    private int totalRequests;
    private int remainingQuota;
    private OkHttpClient apiClient;
    private boolean initialized;

    // Date patterns for parsing
    private static final Pattern DATE_PATTERN = Pattern.compile(
            "(\\d{1,2})\\s+(tháng|thg)\\s+(\\d{1,2})[,\\s]+(\\d{4})");

    public GoogleCseCollector(GoogleCseConfig config) {
        super("google.cse");
        this.config = config;
        this.totalRequests = 0;
        this.remainingQuota = config.getRateLimit();

        if (config.isValid()) {
            initializeClient();
            this.rateLimiter = new CustomRateLimiter(
                    config.getRateLimit(),
                    config.getRateWindow());
        } else {
            logger.error("Invalid Google CSE configuration!");
            this.rateLimiter = null;
        }
    }

    @Override
    public void initializeClient() {
        try {
            logger.info("Initializing Google CSE HTTP client...");

            this.apiClient = new OkHttpClient.Builder()
                    .connectTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
                    .build();

            this.initialized = true;
            logger.info("Google CSE HTTP client initialized successfully");

        } catch (Exception e) {
            logger.error("Failed to initialize Google CSE client", e);
            this.initialized = false;
        }
    }

    @Override
    public boolean testConnection() {
        try {
            String url = buildSearchUrl("test", 1, 1);
            Request request = buildRequest(url);

            try (Response response = apiClient.newCall(request).execute()) {
                int code = response.code();

                if (code == 200) {
                    logger.info("Connection test passed");

                    // Check remaining quota from response
                    if (response.body() != null) {
                        String body = response.body().string();
                        checkQuotaFromResponse(body);
                    }

                    return true;
                } else if (code == 400) {
                    logger.error("Connection test failed: Bad request - Check API key and CX");
                    return false;
                } else if (code == 403) {
                    logger.error("Connection test failed: API not enabled or quota exceeded");
                    return false;
                } else {
                    logger.error("Connection test failed: HTTP {}", code);
                    return false;
                }
            }

        } catch (Exception e) {
            logger.error("Connection test failed", e);
            return false;
        }
    }

    @Override
    protected void beforeCollect(SearchCriteria criteria) {

        logger.info("Google CSE Configuration:");
        logger.info("  Search Engine ID: {}", maskId(config.getSearchEngineId()));
        logger.info("  Language: {}", config.getDefaultLanguage());
        logger.info("  Country: {}", config.getDefaultCountry());
        logger.info("  Search Type: {}", config.getSearchType());
        logger.info("  Remaining quota: {}/{}", remainingQuota, config.getRateLimit());
    }

    @Override
    protected void afterCollect(List<SocialPost> result) {

        logger.info("Total articles collected: {}", result.size());
        logger.info("Total API requests made: {}", totalRequests);
        logger.info("Remaining quota: {}", remainingQuota);

        if (!result.isEmpty()) {
            // Show source distribution
            Map<String, Long> sources = new HashMap<>();
            result.forEach(post -> {
                String source = post.getAuthor();
                sources.put(source, sources.getOrDefault(source, 0L) + 1);
            });

            logger.info("Articles from {} unique sources", sources.size());
        }
    }

    @Override
    public List<SocialPost> getEmptyResult() {
        return new ArrayList<>();
    }

    // ==========================================
    // GOOGLE CSE-SPECIFIC METHODS
    // ==========================================

    /**
     * Build search query from criteria
     */
    public String buildQuery(SearchCriteria criteria) {
        StringBuilder query = new StringBuilder();

        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            query.append(criteria.getKeyword());
        }

        // Add hashtags
        if (criteria.getHashtags() != null && !criteria.getHashtags().isEmpty()) {
            for (String hashtag : criteria.getHashtags()) {
                query.append(" ");
                query.append(hashtag.replace("#", ""));
            }
        }

        // Add date range operators (if supported)
        if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
            // Google search date range format
            String startDate = criteria.getStartDate().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endDate = criteria.getEndDate().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // This is a hint, not guaranteed to work
            query.append(" after:").append(startDate);
            query.append(" before:").append(endDate);
        }

        return query.toString().trim();
    }

    /**
     * Build Google CSE API URL
     */
    public String buildSearchUrl(String query, int num, int start) throws Exception {
        StringBuilder url = new StringBuilder(config.getBaseUrl());
        url.append("?key=").append(config.getApiKey());
        url.append("&cx=").append(config.getSearchEngineId());
        // Ensure this is using UTF-8
        url.append("&q=").append(URLEncoder.encode(query, StandardCharsets.UTF_8));
        url.append("&num=").append(num);
        url.append("&start=").append(start);

        // // Language restriction
        // if (config.getDefaultLanguage() != null) {
        // url.append("&lr=").append(config.getDefaultLanguage());
        // }

        // // Country restriction
        // if (config.getDefaultCountry() != null) {
        // url.append("&cr=").append(config.getDefaultCountry());
        // }

        // // Safe search
        // url.append("&safe=").append(config.getSafeSearch());

        // // Sort by date (if news search)
        // if ("news".equals(config.getSearchType())) {
        // url.append("&sort=date");
        // }
        System.out.println(url.toString());
        return url.toString();
    }

    /**
     * Build HTTP request
     */
    private Request buildRequest(String url) {
        return new Request.Builder()
                .url(url)
                .get()
                // This string makes Google think you are a real user on Windows 10
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .build();
    }

    /**
     * Execute search request
     */
    public List<SocialPost> executeSearchRequest(String url, SearchCriteria criteria)
            throws IOException {

        List<SocialPost> posts = new ArrayList<>();

        Request request = buildRequest(url);
        totalRequests++;
        remainingQuota--;

        try (Response response = apiClient.newCall(request).execute()) {
            int code = response.code();

            if (code != 200) {
                handleErrorResponse(code, response);
                return posts;
            }

            if (response.body() == null) {
                logger.error("Empty response body");
                return posts;
            }

            String body = response.body().string();

            // Parse JSON
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            // Check for errors
            if (json.has("error")) {
                JsonObject error = json.getAsJsonObject("error");
                String message = error.get("message").getAsString();
                int errorCode = error.get("code").getAsInt();
                logger.error("API error {}: {}", errorCode, message);
                return posts;
            }

            // Check quota
            checkQuotaFromResponse(body);

            // Parse search results
            if (json.has("items")) {
                JsonArray items = json.getAsJsonArray("items");

                for (JsonElement element : items) {
                    JsonObject item = element.getAsJsonObject();
                    SocialPost post = parseSearchResult(item, criteria);

                    if (post != null) {
                        posts.add(post);
                    }
                }

                logger.debug("Parsed {} results from response", posts.size());
            } else {
                logger.warn("No 'items' in response");
            }

            // Log search information
            if (json.has("searchInformation")) {
                JsonObject searchInfo = json.getAsJsonObject("searchInformation");
                if (searchInfo.has("totalResults")) {
                    String total = searchInfo.get("totalResults").getAsString();
                    logger.debug("Total results available: {}", total);
                }
            }

        } catch (IOException e) {
            logger.error("Request failed", e);
            throw e;
        }

        return posts;
    }

    /**
     * Parse search result to SocialPost
     */
    private SocialPost parseSearchResult(JsonObject item, SearchCriteria criteria) {
        SocialPost post = new SocialPost();

        // URL
        String link = getJsonString(item, "link");
        if (link == null) {
            logger.warn("Search result has no link, skipping");
            return null;
        }

        post.setId(String.valueOf(link.hashCode()));

        // Title
        String title = getJsonString(item, "title");

        // Snippet (description)
        String snippet = getJsonString(item, "snippet");

        // Full content
        StringBuilder content = new StringBuilder();
        if (title != null) {
            content.append(title).append("\n\n");
        }
        if (snippet != null) {
            content.append(snippet);
        }

        post.setContent(content.toString().trim());

        // Try to extract date from snippet or metadata
        LocalDateTime timestamp = extractDateFromResult(item, snippet);
        post.setTimestamp(timestamp);

        // Source
        post.setSource("google-cse");

        // Author (domain name)
        String displayLink = getJsonString(item, "displayLink");
        post.setAuthor(displayLink != null ? displayLink : extractDomain(link));

        // No engagement metrics
        post.setLikes(0);
        post.setShares(0);
        post.setComments(0);

        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("url", link);
        metadata.put("title", title);
        metadata.put("snippet", snippet);
        metadata.put("display_link", displayLink);

        // Image if available
        if (item.has("pagemap")) {
            JsonObject pagemap = item.getAsJsonObject("pagemap");
            if (pagemap.has("cse_image")) {
                JsonArray images = pagemap.getAsJsonArray("cse_image");
                if (images.size() > 0) {
                    JsonObject image = images.get(0).getAsJsonObject();
                    String imageSrc = getJsonString(image, "src");
                    metadata.put("image_url", imageSrc);
                }
            }
        }

        post.setMetadata(metadata);

        // Filter by date if within criteria
        if (criteria.getStartDate() != null && timestamp.isBefore(criteria.getStartDate())) {
            return null;
        }
        if (criteria.getEndDate() != null && timestamp.isAfter(criteria.getEndDate())) {
            return null;
        }

        return post;
    }

    /**
     * Extract date from search result
     */
    private LocalDateTime extractDateFromResult(JsonObject item, String snippet) {
        // Try to get from metadata first
        if (item.has("pagemap")) {
            JsonObject pagemap = item.getAsJsonObject("pagemap");

            // Check metatags
            if (pagemap.has("metatags")) {
                JsonArray metatags = pagemap.getAsJsonArray("metatags");
                if (metatags.size() > 0) {
                    JsonObject meta = metatags.get(0).getAsJsonObject();

                    // Try article:published_time
                    String publishedTime = getJsonString(meta, "article:published_time");
                    if (publishedTime != null) {
                        try {
                            return LocalDateTime.parse(
                                    publishedTime.replace("Z", ""),
                                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        } catch (Exception e) {
                            logger.debug("Failed to parse date: {}", publishedTime);
                        }
                    }
                }
            }
        }

        // Try to extract from snippet (Vietnamese date format)
        if (snippet != null) {
            Matcher matcher = DATE_PATTERN.matcher(snippet);
            if (matcher.find()) {
                try {
                    int day = Integer.parseInt(matcher.group(1));
                    int month = Integer.parseInt(matcher.group(3));
                    int year = Integer.parseInt(matcher.group(4));

                    return LocalDateTime.of(year, month, day, 0, 0);
                } catch (Exception e) {
                    logger.debug("Failed to parse Vietnamese date from snippet");
                }
            }
        }

        // Default to now
        return LocalDateTime.now();
    }

    /**
     * Extract domain from URL
     */
    private String extractDomain(String url) {
        try {
            URI uri = URI.create(url);
            return uri.getHost();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * Check remaining quota from response
     */
    private void checkQuotaFromResponse(String responseBody) {
        // Google CSE doesn't provide quota in response
        // We track it manually

        if (remainingQuota < 10) {
            logger.warn("Low API quota remaining: {} requests", remainingQuota);
        }

        if (remainingQuota <= 0) {
            logger.error("API quota exhausted!");
        }
    }

    /**
     * Handle error responses
     */
    private void handleErrorResponse(int code, Response response) throws IOException {
        switch (code) {
            case 400:
                logger.error("Bad request: Check query parameters or API key");
                break;
            case 403:
                logger.error("Forbidden: API not enabled, quota exceeded, or invalid cx");
                break;
            case 429:
                logger.error("Rate limit exceeded: Too many requests");
                break;
            case 500:
                logger.error("Server error: Google API service issue");
                break;
            default:
                String errorBody = response.body() != null ? response.body().string() : "No details";
                logger.error("HTTP Error {}: {}", code, errorBody);
        }
    }

    /**
     * Helper: Get string from JSON safely
     */
    private String getJsonString(JsonObject json, String key) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsString();
        }
        return null;
    }

    /**
     * Mask sensitive ID for logging
     */
    private String maskId(String id) {
        if (id == null || id.length() < 10) {
            return "***";
        }
        return id.substring(0, 5) + "..." + id.substring(id.length() - 3);
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public int getTotalRequests() {
        return totalRequests;
    }

    public int getRemainingQuota() {
        return remainingQuota;
    }

    public GoogleCseConfig getConfig() {
        return config;
    }

	public CustomRateLimiter getRateLimiter() {
		// TODO Auto-generated method stub
		return rateLimiter;
	}
}
package com.humanitarian.logistics.collector;

import com.humanitarian.logistics.config.NewsApiConfig;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class NewsCollector extends Collector<SearchCriteria, OkHttpClient, List<SocialPost>> {
    private NewsApiConfig config;
    private CustomRateLimiter rateLimiter;
    private int totalRequest;
    private int remaningRequest;
    private boolean initialized;
    private OkHttpClient apiClient;
    private static final Logger logger = LoggerFactory.getLogger(NewsCollector.class);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // API Endpoints
    private static final String ENDPOINT_EVERYTHING = "/everything";
    private static final String ENDPOINT_TOP_HEADLINES = "/top-headlines";

    public NewsCollector(NewsApiConfig config) {
        super("newsapi");
        this.config = config;
        this.totalRequest = 0;
        this.remaningRequest = config.getRateLimit();
        if (config.isValid()) {
            initializeClient();
            this.rateLimiter = new CustomRateLimiter(config.getRateLimit(), config.getRateWindow());
        } else {
            logger.error("Invalid NewsApi configuration");
        }
    }

    @Override
    public void initializeClient() {
        try {
            logger.info("Initializing NewsAPI HTTP client...");

            this.apiClient = new OkHttpClient.Builder()
                    .connectTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
                    .build();

            this.initialized = true;
            logger.info("NewsAPI HTTP client initialized successfully");

        } catch (Exception e) {
            logger.error("Failed to initialize NewsAPI client", e);
            this.initialized = false;
        }

    }

    public String getSourceName() {
        return "news";
    }

    @Override
    public boolean testConnection() {
        if (!initialized) {
            logger.warn("Cannot test connection, client not initialized");
            return false;
        }
        String url = buildEverythingUrl("test", 1, null, null); // build a null api link for testing
        Request request = new Request.Builder().url(url).get().build();

        try (Response response = apiClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                logger.info("Successfully connected to NewsApi (status {})", response.code());
                logger.info(url, response);
                return true;
            } else {
                logger.error("Falied to connect. HTTP status {}", response.code());
                if (response.body() != null) {
                    logger.error("Response body: {}", response.body().string());
                }
                return false;
            }
        } catch (IOException e) {
            logger.error("Error during NewsApi connection test", e);
            return false;
        }

    }

    public String buildEverythingUrl(String query, int page, String from, String to) {
        StringBuilder sb = new StringBuilder("https://newsapi.org/v2/everything?");
        sb.append("apikey=").append(config.getApiKey());
        sb.append("&q=").append(URLEncoder.encode(query, StandardCharsets.UTF_8));
        sb.append("&language=").append("vi");
        sb.append("&page=").append(page);
        if (from != null)
            sb.append("&from=").append(from);
        if (to != null)
            sb.append("&to=").append(to);
        System.out.println(sb.toString());
        return sb.toString();
    }

    /**
     * Build URL for /top-headlines endpoint (alternative)
     */
    private String buildTopHeadlinesUrl(String query, int pageSize) {
        StringBuilder url = new StringBuilder(config.getBaseUrl());
        url.append(ENDPOINT_TOP_HEADLINES);
        url.append("?apiKey=").append(config.getApiKey());
        url.append("&pageSize=").append(pageSize);

        if (query != null && !query.isEmpty()) {
            url.append("&q=").append(URLEncoder.encode(query, StandardCharsets.UTF_8));
        }

        // Country
        if (config.getDefaultCountry() != null) {
            url.append("&country=").append(config.getDefaultCountry());
        }

        return url.toString();
    }

    @Override
    protected void beforeCollect(SearchCriteria criteria) {
        super.beforeCollect(criteria);
        logger.info("NewsAPI Configuration:");
        logger.info("  Base URL: {}", config.getBaseUrl());
        logger.info("  Language: {}", config.getDefaultLanguage());
        logger.info("  Country: {}", config.getDefaultCountry());
        logger.info("  Max results: {}", config.getMaxResults());
        logger.info("  Remaining requests: {}/{}", remaningRequest, config.getRateLimit());
    }

    @Override
    protected void afterCollect(List<SocialPost> result) {
        super.afterCollect(result);
        logger.info("Total articles collected: {}", result.size());
        logger.info("Total API requests made: {}", totalRequest);
        logger.info("Remaining requests: {}", remaningRequest);

        if (!result.isEmpty()) {
            // Show source distribution
            Map<String, Long> sources = new HashMap<>();
            result.forEach(post -> {
                String source = post.getAuthor();
                sources.put(source, sources.getOrDefault(source, 0L) + 1);
            });

            logger.info("Articles from {} unique sources", sources.size());
            logger.debug("Top sources: {}", sources.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .limit(5)
                    .toList());
        }
    }

    @Override
    protected List<SocialPost> getEmptyResult() {
        return new ArrayList<>();
    }

    public String buildQuery(SearchCriteria criteria) {
        StringBuilder sb = new StringBuilder();
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            sb.append(criteria.getKeyword());
        }
        if (criteria.getHashtags() != null && !criteria.getHashtags().isEmpty()) {
            for (String hagstag : criteria.getHashtags()) {
                sb.append(" ");
                if (!hagstag.startsWith("#")) {
                    sb.append("#");
                }
                sb.append(hagstag);
            }
        }
        return sb.toString().trim();
    }

    private Request buildRequest(String url) {
        return new Request.Builder().url(url).get().addHeader("User-Agent", "ProjectOOP/1.0").build();
    }

    public List<SocialPost> executeRequest(String url) throws IOException {
        List<SocialPost> posts = new ArrayList<>();

        Request request = buildRequest(url);
        totalRequest++;

        try (Response response = apiClient.newCall(request).execute()) {
            int code = response.code();

            if (code != 200) {
                handleErrorResponse(code, response);
                return posts;
            }
            if (response.body() == null) {
                logger.error("Empty reponse body");
                return posts;
            }

            String body = response.body().string();

            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            updateRateLimitFromReponse(body);

            String status = json.get("status").getAsString();

            if (!"ok".equals(status)) {
                String message = json.has("message") ? json.get("message").getAsString() : "Unknown error";
                logger.error("Api return error message: {}", message);
                return posts;
            }
            if (json.has("articles")) {
                JsonArray articles = json.getAsJsonArray("articles");

                for (JsonElement element : articles) {
                    JsonObject article = element.getAsJsonObject();
                    SocialPost post = parseArticle(article);
                    posts.add(post);
                }
                logger.debug("Parsed {} articles from response", posts.size());
            }
        } catch (IOException e) {
            super.handleError(e);
        }
        return posts;
    }

    private void handleErrorResponse(int code, Response response) {
        switch (code) {
            case 400:
                logger.error("Bad request: Check query parameters");
                break;
            case 401:
                logger.error("Unauthorized: Invalid API key");
                break;
            case 429:
                logger.error("Rate limit exceeded: {} requests per day limit reached",
                        config.getRateLimit());
                break;
            case 500:
                logger.error("Server error: NewsAPI service issue");
                break;
            default:
                String errorBody;
                try {
                    errorBody = response.body().string();
                } catch (IOException e) {
                    errorBody = "NoDetail";
                    super.handleError(e);
                }
                logger.error("HTTP Error {}: {}", code, errorBody);
        }
    }

    private void updateRateLimitFromReponse(String body) {
        remaningRequest = config.getRateLimit() - totalRequest;
        if (remaningRequest < 10) {
            logger.warn("Low API quota remaining: {} requests", remaningRequest);
        }
    }

    private SocialPost parseArticle(JsonObject article) {
        SocialPost post = new SocialPost();

        String url = getJsonString(article, "url");
        post.setId(String.valueOf(url.hashCode()));

        String title = getJsonString(article, "title");
        String description = getJsonString(article, "description");
        String content = getJsonString(article, "content");

        StringBuilder fullContent = new StringBuilder();
        if (title != null)
            fullContent.append(title).append("\n\n");
        if (description != null)
            fullContent.append(description).append("\n\n");
        if (content != null)
            fullContent.append(content);

        post.setContent(fullContent.toString().trim());

        // Timestamp
        String publishedAt = getJsonString(article, "publishedAt");
        if (publishedAt != null) {
            try {
                post.setTimestamp(LocalDateTime.parse(
                        publishedAt.replace("Z", ""),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (Exception e) {
                post.setTimestamp(LocalDateTime.now());
            }
        } else {
            post.setTimestamp(LocalDateTime.now());
        }
        // Source
        post.setSource("newsapi");

        // Author (news source name)
        if (article.has("source") && article.get("source").isJsonObject()) {
            JsonObject source = article.getAsJsonObject("source");
            String sourceName = getJsonString(source, "name");
            post.setAuthor(sourceName != null ? sourceName : "Unknown");
        } else {
            String author = getJsonString(article, "author");
            post.setAuthor(author != null ? author : "Unknown");
        }

        // No engagement metrics for news
        post.setLikes(0);
        post.setShares(0);
        post.setComments(0);

        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("url", url);
        metadata.put("title", title);
        metadata.put("description", description);
        metadata.put("url_to_image", getJsonString(article, "urlToImage"));

        if (article.has("source") && article.get("source").isJsonObject()) {
            JsonObject source = article.getAsJsonObject("source");
            metadata.put("source_id", getJsonString(source, "id"));
            metadata.put("source_name", getJsonString(source, "name"));
        }

        post.setMetadata(metadata);

        return post;

    }

    private String getJsonString(JsonObject json, String key) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsString();
        }
        return null;
    }
    // ==========================================
    // GETTERS
    // ==========================================

    public int getTotalRequests() {
        return totalRequest;
    }

    public int getRemainingRequests() {
        return remaningRequest;
    }

    public NewsApiConfig getConfig() {
        return config;
    }
    
    public CustomRateLimiter getRateLimiter() {
    	return rateLimiter;
    }
}
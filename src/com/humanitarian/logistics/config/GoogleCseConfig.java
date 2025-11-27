package com.humanitarian.logistics.config;

public class GoogleCseConfig extends ApiConfig {
    private String searchEngineId;
    private String defaultLanguage;
    private String defaultCountry;
    private String searchType;
    private String safeSearch;
    private int resultsPerPage;
    private int maxResults;
    private int timeout;

    public GoogleCseConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
        loadKeys();
    }

    public boolean isValid() {
        return enabled &&
                apiKey != null && !apiKey.isEmpty() &&
                !apiKey.equals("YOUR_GOOGLE_API_KEY") &&
                searchEngineId != null && !searchEngineId.isEmpty() &&
                !searchEngineId.equals("YOUR_SEARCH_ENGINE_ID");
    }

    @Override
    public void loadKeys() {
        this.enabled = appConfig.getBoolean("google.cse.enabled");
        this.apiKey = appConfig.get("google.cse.api.key");
        this.searchEngineId = appConfig.get("google.cse.engine.id");
        this.baseUrl = appConfig.get("google.cse.base.url",
                "https://www.googleapis.com/customsearch/v1");

        this.defaultLanguage = appConfig.get("google.cse.default.language", "lang_vi");
        this.defaultCountry = appConfig.get("google.cse.default.country", "countryVN");
        this.searchType = appConfig.get("google.cse.search.type", "news");
        this.safeSearch = appConfig.get("google.cse.safe.search", "off");
        this.resultsPerPage = appConfig.getInt("google.cse.results.per.page", 10);
        this.maxResults = appConfig.getInt("google.cse.max.results", 100);
        this.timeout = appConfig.getInt("google.cse.timeout", 30000);

        this.rateLimit = appConfig.getInt("google.cse.rate.limit", 100);
        this.rateWindow = appConfig.getInt("google.cse.rate.window", 1440);
    }

    // Getters
    public boolean isEnabled() {
        return enabled;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSearchEngineId() {
        return searchEngineId;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public String getDefaultCountry() {
        return defaultCountry;
    }

    public String getSearchType() {
        return searchType;
    }

    public String getSafeSearch() {
        return safeSearch;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public int getTimeout() {
        return timeout;
    }

    @Override
    public int getRateLimit() {
        return rateLimit;
    }

    @Override
    public int getRateWindow() {
        return rateWindow;
    }
}
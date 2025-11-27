package com.humanitarian.logistics.config;

public class GNewsConfig extends ApiConfig {
    private String defaultLanguage;
    private String defaultCountry;
    private int defaultPageSize;
    private int maxResults;
    private int timeout;

    public GNewsConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
        loadKeys();
    }

    public boolean isValid() {
        return apiKey != null && !apiKey.isEmpty();
    }

    @Override
    public void loadKeys() {
        this.apiKey = appConfig.get("gnews.api.key");
        this.baseUrl = appConfig.get("gnews.api.base.url");
        this.defaultLanguage = appConfig.get("gnews.default.language", "vi");
        this.defaultCountry = appConfig.get("gnews.default.country", "vn");
        this.maxResults = appConfig.getInt("gnews.max.results", 500);
        this.timeout = appConfig.getInt("gnews.timeout", 30000);

        this.rateLimit = appConfig.getInt("gnews.rate.limit", 100);
        this.rateWindow = appConfig.getInt("gnews.rate.window", 1440);
    }

    // Getters
    public String getApiKey() {
        return apiKey;
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

    public int getDefaultPageSize() {
        return defaultPageSize;
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

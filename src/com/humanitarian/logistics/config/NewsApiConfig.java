package com.humanitarian.logistics.config;

public class NewsApiConfig {
    private final AppConfig appConfig;

    private final String apiKey;
    private final String baseUrl;
    
    private final String defaultLanguage;
    private final String defaultCountry;
    private final int defaultPageSize;
    private final int maxResults;
    private final int timeout;
    
    private final int rateLimit;
    private final int rateWindow;
    
    public NewsApiConfig(AppConfig appConfig) {
        this.appConfig = appConfig;

        this.apiKey = appConfig.get("newsapi.api.key");
        this.baseUrl = appConfig.get("newsapi.base.url", "https://newsapi.org/v2");
        
        this.defaultLanguage = appConfig.get("newsapi.default.language", "vi");
        this.defaultCountry = appConfig.get("newsapi.default.country", "vn");
        this.defaultPageSize = appConfig.getInt("newsapi.default.page.size", 100);
        this.maxResults = appConfig.getInt("newsapi.max.results", 500);
        this.timeout = appConfig.getInt("newsapi.timeout", 30000);
        
        this.rateLimit = appConfig.getInt("newsapi.rate.limit", 100);
        this.rateWindow = appConfig.getInt("newsapi.rate.window", 1440); // minutes in a day
    }
    
    public boolean isValid() {
        return  apiKey != null && 
                !apiKey.isEmpty();
    }
    
    // Getters
    public String getApiKey() { return apiKey; }
    public String getBaseUrl() { return baseUrl; }
    public String getDefaultLanguage() { return defaultLanguage; }
    public String getDefaultCountry() { return defaultCountry; }
    public int getDefaultPageSize() { return defaultPageSize; }
    public int getMaxResults() { return maxResults; }
    public int getTimeout() { return timeout; }
    public int getRateLimit() { return rateLimit; }
    public int getRateWindow() { return rateWindow; }
}
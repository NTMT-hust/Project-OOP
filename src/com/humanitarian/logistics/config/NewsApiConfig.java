package com.humanitarian.logistics.config;

public class NewsApiConfig extends ApiConfig{
    private AppConfig appConfig;

    private String apiKey;
    private String baseUrl;
    
    private String defaultLanguage;
    private String defaultCountry;
    private int defaultPageSize;
    private int maxResults;
    private int timeout;
    
    private int rateLimit;
    private int rateWindow;
    
    public NewsApiConfig(AppConfig appConfig){
        this.appConfig = appConfig;
        loadKeys();
    }
    
    public boolean isValid() {
        return  apiKey != null && 
                !apiKey.isEmpty();
    }
    @Override
    public void loadKeys(){
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
    
    // Getters
    public String getApiKey() { return apiKey; }
    public String getBaseUrl() { return baseUrl; }
    public String getDefaultLanguage() { return defaultLanguage; }
    public String getDefaultCountry() { return defaultCountry; }
    public int getDefaultPageSize() { return defaultPageSize; }
    public int getMaxResults() { return maxResults; }
    public int getTimeout() { return timeout; }
    @Override
    public int getRateLimit() { return rateLimit; }
    @Override
    public int getRateWindow() { return rateWindow; }
}
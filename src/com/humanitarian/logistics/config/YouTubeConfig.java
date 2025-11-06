package com.humanitarian.logistics.config;

public class YouTubeConfig {
    private AppConfig appConfig;
    private String apiKey;
    private int rateLimit;
    private int rateWindow;
    
    public YouTubeConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.apiKey = appConfig.get("youtube.api.key");
        this.rateLimit = appConfig.getInt("youtube.rate.limit", 100);
        this.rateWindow = appConfig.getInt("youtube.rate.window", 60);
    }
    
    public boolean isValid() {
        return  apiKey != null && 
                !apiKey.isEmpty();
    }
    
    public String getApiKey() { 
        return apiKey; 
    }
    
    public int getRateLimit() { 
        return rateLimit; 
    }
    
    public int getRateWindow() { 
        return rateWindow; 
    }
}
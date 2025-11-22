package com.humanitarian.logistics.config;

public class YouTubeConfig extends ApiConfig {
    private AppConfig appConfig;
    private String apiKey;
    private int rateLimit;
    private int rateWindow;
    
    public YouTubeConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
        loadKeys();
    }
    
    public boolean isValid() {
        return  apiKey != null && 
                !apiKey.isEmpty();
    }
    @Override
    public void loadKeys(){
        this.apiKey = appConfig.get("youtube.api.key");
        this.rateLimit = appConfig.getInt("youtube.rate.limit", 100);
        this.rateWindow = appConfig.getInt("youtube.rate.window", 60);

    }
    public String getApiKey() { 
        return apiKey; 
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
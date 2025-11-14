package com.humanitarian.logistics.config;

public class TwitterConfig extends ApiConfig{
    private AppConfig config;
    private String apiKey;
    private String apiSecret;
    private String accessToken;
    private String accessSecret;
    private int rateLimit;
    private int rateWindow;
    
    public TwitterConfig(AppConfig config){
        super();
        this.config = config;
        loadKeys();
    }
    
    public boolean isValid() {
        return  apiKey != null && !apiKey.isEmpty() &&
                apiSecret != null && !apiSecret.isEmpty() &&
                accessToken != null && !accessToken.isEmpty() &&
                accessSecret != null && !accessSecret.isEmpty();
    }
    @Override 
    public void loadKeys(){
        this.apiKey = config.get("twitter.api.key");
        this.apiSecret = config.get("twitter.api.secret");
        this.accessToken = config.get("twitter.access.token");
        this.accessSecret = config.get("twitter.access.secret");
        this.rateLimit = getRateLimit();
        this.rateWindow = getRateWindow();
    }
    @Override
    public int getRateLimit(){
        return config.getInt(apiKey + ".rate.limit", 180);
    }
    @Override
    public int getRateWindow(){
        return config.getInt(apiKey + ".rate.window", 15);
    }
    public String getApiKey() { return apiKey; }
    public String getApiSecret() { return apiSecret; }
    public String getAccessToken() { return accessToken; }
    public String getAccessSecret() { return accessSecret; }
}
package com.humanitarian.logistics.config;

public class TwitterConfig {
    private AppConfig config;
    private String apiKey;
    private String apiSecret;
    private String accessToken;
    private String accessSecret;
    
    public TwitterConfig(AppConfig config) {
        this.config = config;
        this.apiKey = config.get("twitter.api.key");
        this.apiSecret = config.get("twitter.api.secret");
        this.accessToken = config.get("twitter.access.token");
        this.accessSecret = config.get("twitter.access.secret");
    }
    
    public boolean isValid() {
        return  apiKey != null && !apiKey.isEmpty() &&
                !apiKey.equals("YOUR_API_KEY") &&
                apiSecret != null && !apiSecret.isEmpty() &&
                accessToken != null && !accessToken.isEmpty() &&
                accessSecret != null && !accessSecret.isEmpty();
    }
    
    public String getApiKey() { return apiKey; }
    public String getApiSecret() { return apiSecret; }
    public String getAccessToken() { return accessToken; }
    public String getAccessSecret() { return accessSecret; }
}
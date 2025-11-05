package com.humanitarian.logistics.config;

public class YouTubeConfig extends ApiConfig {
    private String apiKey;

    public  YouTubeConfig(AppConfig config){
        super(config);
    }

    @Override
    protected void loadKeys(){
        this.apiKey = config.get(apiKey);
    }

    @Override
    public boolean isValid() {
        return  apiKey != null && !apiKey.isEmpty() &&
                !apiKey.equals("AIzaSyAcQFFgiE5EoK_yeV3kzJQ_FgF2YqyvRUc");
    }

    public String getApiKey() {
        return apiKey;
    }
}

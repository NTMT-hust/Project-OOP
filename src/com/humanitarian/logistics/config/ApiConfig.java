package com.humanitarian.logistics.config;

public abstract class ApiConfig {
    protected AppConfig appConfig;
    protected boolean enabled;
    protected String apiKey;
    protected String baseUrl;
    protected int rateLimit;
    protected int rateWindow;

    protected abstract void loadKeys();

    public abstract boolean isValid();

    public abstract int getRateLimit();

    public abstract int getRateWindow();
}

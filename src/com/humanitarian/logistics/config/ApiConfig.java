package com.humanitarian.logistics.config;

public abstract class ApiConfig {
    protected AppConfig config;

    public ApiConfig(AppConfig config){
        this.config = config;
        loadKeys();
    }

    protected abstract void loadKeys();
    public abstract boolean isValid();
}

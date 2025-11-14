package com.humanitarian.logistics.config;

public abstract class ApiConfig {
    protected abstract void loadKeys();
    public abstract boolean isValid();
    public abstract int getRateLimit();
    public abstract int getRateWindow();
}

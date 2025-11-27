package com.humanitarian.logistics.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private Properties properties;
    private String apiName;

    public AppConfig(String apiName) {
        properties = new Properties();
        this.apiName = apiName;
        loadConfig();
    }

    private void loadConfig() {
        try {
            // Try loading from resources folder
            InputStream input = new FileInputStream(".\\resources\\application.properties");
            properties.load(input);
            System.out.println("✓ Loaded config from resources/application.properties");
            System.out.println(String.format("DEBUG: %s.api.key = " + get(apiName + ".api.key"), apiName));
        } catch (IOException e) {
            System.err.println("Error loading config: " + e.getMessage());
            loadDefaults();
        }
    }

    private void loadDefaults() {
        System.out.println("Using default configuration");
        properties.setProperty("search.max.results", "100");
        properties.setProperty("search.language", "vi");
        properties.setProperty("data.dir", "./data");
        properties.setProperty("output.dir", "./output");
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public boolean getBoolean(String key) {
        String value = properties.getProperty(key);
        if (value == null)
            return false; // default
        return Boolean.parseBoolean(value.trim());
    }
}
package com.humanitarian.logistics.test;

import com.humanitarian.logistics.collector.NewsCollector;
import com.humanitarian.logistics.config.AppConfig;
import com.humanitarian.logistics.config.NewsApiConfig;

public class Test {
    public static void main(String[] args) {
        AppConfig config = new AppConfig("newsapi");
        NewsApiConfig newsApiConfig = new NewsApiConfig(config);
        NewsCollector collector = new NewsCollector(newsApiConfig);
        System.out.println("Testing connection...");
        System.out.println("Connected: " + collector.testConnection());
    }
}

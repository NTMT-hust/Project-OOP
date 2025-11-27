package com.humanitarian.logistics.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.humanitarian.logistics.collector.NewsCollector;
import com.humanitarian.logistics.config.AppConfig;
import com.humanitarian.logistics.config.NewsApiConfig;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;
import com.humanitarian.logistics.util.LocalDateTimeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Test NewsAPI.org Collector
 */
public class TestNewsApiCollector {

    private static final Logger logger = LoggerFactory.getLogger(TestNewsApiCollector.class);

    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("NewsAPI.org Collector Test");
        logger.info("========================================");

        // Load config
        AppConfig appConfig = new AppConfig("newsapi");
        NewsApiConfig config = new NewsApiConfig(appConfig);

        logger.info("Configuration:");
        logger.info("  Base URL: {}", config.getBaseUrl());
        logger.info("  Language: {}", config.getDefaultLanguage());
        logger.info("  Country: {}", config.getDefaultCountry());
        logger.info("  Valid: {}", config.isValid());

        if (!config.isValid()) {
            logger.error("NewsAPI configuration invalid!");
            logger.error("Get your API key from: https://newsapi.org/");
            return;
        }

        // Create collector
        NewsCollector collector = new NewsCollector(config);

        // Test connection
        logger.info("--- Testing Connection ---");
        if (!collector.testConnection()) {
            logger.error("Connection test failed!");
            return;
        }

        // Build criteria
        logger.info("--- Building Search Criteria ---");
        SearchCriteria criteria = new SearchCriteria.Builder()
                .keyword("bão")
                .dateRange(
                        LocalDateTime.now().minusDays(30), // NewsAPI free: max 1 month back
                        LocalDateTime.now())
                .maxResults(50)
                .build();

        logger.info("Keyword: {}", criteria.getKeyword());
        logger.info("Max results: {}", criteria.getMaxResults());
        logger.info("Date range: {} to {}", criteria.getStartDate(), criteria.getEndDate());

        // Collect
        logger.info("--- Starting Collection ---");
        List<SocialPost> posts = collector.collect(criteria);

        // Display results
        logger.info("========================================");
        logger.info("Collection Results");
        logger.info("========================================");
        logger.info("Total articles: {}", posts.size());
        logger.info("API requests used: {}", collector.getTotalRequests());
        logger.info("Remaining requests: {}", collector.getRemainingRequests());

        if (posts.isEmpty()) {
            logger.warn("No articles found");
            logger.warn("Try different keywords or check date range (max 1 month for free tier)");
            return;
        }

        // Show first 5 articles
        logger.info("--- Sample Articles ---");
        for (int i = 0; i < Math.min(5, posts.size()); i++) {
            SocialPost post = posts.get(i);

            logger.info("\n📰 Article #{}", i + 1);
            logger.info("  Source: {}", post.getAuthor());
            logger.info("  Date: {}", post.getTimestamp());
            logger.info("  URL: {}", post.getMetadata().get("url"));
            logger.info("  Title: {}", post.getMetadata().get("title"));

            String content = post.getContent();
            if (content.length() > 150) {
                content = content.substring(0, 150) + "...";
            }
            logger.info("  Content: {}", content);
        }

        // Statistics
        logger.info("--- Statistics ---");

        // Group by source
        Map<String, Long> sourceCount = posts.stream()
                .collect(Collectors.groupingBy(
                        SocialPost::getAuthor,
                        Collectors.counting()));

        logger.info("Articles from {} unique sources", sourceCount.size());
        logger.info("Top sources:");
        sourceCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .forEach(entry -> logger.info("  {}: {} articles", entry.getKey(), entry.getValue()));
        try {
            // 5. Write to JSON file
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .setPrettyPrinting()
                    .create();
            try (FileWriter writer = new FileWriter(".\\data\\news_api_results.json")) {
                gson.toJson(posts, writer);
            }

            System.out.println("Saved " + posts.size() + " results to news_api_results.json");

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Date distribution
        LocalDateTime earliest = posts.stream()
                .map(SocialPost::getTimestamp)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime latest = posts.stream()
                .map(SocialPost::getTimestamp)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        if (earliest != null && latest != null) {
            logger.info("Date range: {} to {}", earliest, latest);
        }

        logger.info("========================================");
        logger.info("✓ Test completed successfully!");
        logger.info("========================================");
    }
}
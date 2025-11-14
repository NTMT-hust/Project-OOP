package com.humanitarian.logistics.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.humanitarian.logistics.collector.GoogleCseCollector;
import com.humanitarian.logistics.config.AppConfig;
import com.humanitarian.logistics.config.GoogleCseConfig;
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
 * Test Google Custom Search Engine Collector
 */
public class TestGoogleCseCollector {
    
    private static final Logger logger = LoggerFactory.getLogger(TestGoogleCseCollector.class);
    
    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("Google Custom Search Engine Test");
        logger.info("========================================");
        
        // Load config
        AppConfig appConfig = new AppConfig("google.cse");
        GoogleCseConfig config = new GoogleCseConfig(appConfig);
        
        logger.info("Configuration:");
        logger.info("  Enabled: {}", config.isEnabled());
        logger.info("  Base URL: {}", config.getBaseUrl());
        logger.info("  Language: {}", config.getDefaultLanguage());
        logger.info("  Country: {}", config.getDefaultCountry());
        logger.info("  Valid: {}", config.isValid());
        
        if (!config.isValid()) {
            logger.error("Google CSE configuration invalid!");
            logger.error("Setup guide:");
            logger.error("1. Go to: https://programmablesearchengine.google.com/");
            logger.error("2. Create a custom search engine");
            logger.error("3. Get your Search Engine ID (cx)");
            logger.error("4. Enable Custom Search API and get API key");
            return;
        }
        
        // Create collector
        GoogleCseCollector collector = new GoogleCseCollector(config);
        
        // Test connection
        logger.info("--- Testing Connection ---");
        if (!collector.testConnection()) {
            logger.error("Connection test failed!");
            return;
        }
        
        // Build criteria
        logger.info("--- Building Search Criteria ---");
        SearchCriteria criteria = new SearchCriteria.Builder()
            .keyword("bách khoa")
            .dateRange(
                LocalDateTime.now().minusDays(60),
                LocalDateTime.now()
            )
            .maxResults(30)
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
        logger.info("Remaining quota: {}", collector.getRemainingQuota());
        
        if (posts.isEmpty()) {
            logger.warn("No articles found");
            logger.warn("Try different keywords or check search engine configuration");
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
                Collectors.counting()
            ));
        
        logger.info("Articles from {} unique sources", sourceCount.size());
        logger.info("Top sources:");
        sourceCount.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(5)
            .forEach(entry -> 
                logger.info("  {}: {} articles", entry.getKey(), entry.getValue())
            );
        
        // Date distribution
        LocalDateTime earliest = posts.stream()
            .map(SocialPost::getTimestamp)
            .min(LocalDateTime::compareTo)
            .orElse(null);
        
        LocalDateTime latest = posts.stream()
            .map(SocialPost::getTimestamp)
            .max(LocalDateTime::compareTo)
            .orElse(null);
        try {
            // 5. Write to JSON file
            Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .setPrettyPrinting()
                        .create();
            try (FileWriter writer = new FileWriter("google_cse_results.json")) {
                gson.toJson(posts, writer);
            }

            System.out.println("Saved " + posts.size() + " results to google_cse_results.json");

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (earliest != null && latest != null) {
            logger.info("Date range: {} to {}", earliest, latest);
        }
        logger.info("========================================");
        logger.info("✓ Test completed successfully!");
        logger.info("========================================");
    }
}
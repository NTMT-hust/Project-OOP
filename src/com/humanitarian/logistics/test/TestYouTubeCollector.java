package com.humanitarian.logistics.test;

import com.humanitarian.logistics.collector.YouTubeCollector;
import com.humanitarian.logistics.config.AppConfig;
import com.humanitarian.logistics.config.YouTubeConfig;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class TestYouTubeCollector {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("YouTube Collector Test");
        System.out.println("========================================\n");

        // Load config
        AppConfig appConfig = new AppConfig("youtube");
        YouTubeConfig config = new YouTubeConfig(appConfig);

        if (!config.isValid()) {
            System.err.println("✗ YouTube API key not configured!");
            System.err.println("Edit resources/application.properties");
            System.err.println("Add: youtube.api.key=YOUR_API_KEY");
            return;
        }

        System.out.println("✓ Configuration loaded");

        // Create collector
        YouTubeCollector collector;
        try {
            collector = new YouTubeCollector(config);
        } catch (Exception e) {
            System.err.println("✗ Failed to create collector: " + e.getMessage());
            return;
        }

        // Test connection
        System.out.println("\n--- Testing Connection ---");
        if (!collector.testConnection()) {
            return;
        }

        // Build criteria
        System.out.println("\n--- Building Search Criteria ---");
        SearchCriteria criteria1 = new SearchCriteria.Builder()
                .keyword("Bão")
                // .hashtags("", "")
                .dateRange(
                        LocalDateTime.of(2025, 9, 6, 0, 0),
                        LocalDateTime.of(2025, 12, 15, 23, 59))
                .language("vi")
                .maxResults(5000000)
                .build();

        // Build criteria for searching short video
        System.out.println("\n--- Building Search Criteria ---");
        SearchCriteria criteria2 = new SearchCriteria.Builder()
                .keyword("Bão #shorts")
                // .hashtags("", "")
                .dateRange(
                        LocalDateTime.of(2025, 9, 6, 0, 0),
                        LocalDateTime.of(2025, 12, 15, 23, 59))
                .language("vi")
                .maxResults(5)
                .build();

        System.out.println("Keyword: " + criteria1.getKeyword());
        System.out.println("Max results: " + criteria1.getMaxResults());

        // Collect
        System.out.println("\n--- Starting Collection ---");
        List<SocialPost> posts = collector.collect(criteria1);
        List<SocialPost> posts2 = collector.collect(criteria2);
        posts.addAll(posts2);

        // Display results
        System.out.println("\n========================================");
        System.out.println("Collection Results");
        System.out.println("========================================");
        System.out.println("Total posts: " + posts.size());

        if (posts.isEmpty()) {
            System.out.println("\n⚠ No posts collected");
            System.out.println("Try different keywords or date range");
            return;
        }

        // Show first 5 posts
        System.out.println("\n--- Sample Posts ---");
        for (int i = 0; i < Math.min(5, posts.size()); i++) {
            SocialPost post = posts.get(i);
            System.out.println("\n💬 Comment #" + (i + 1));
            System.out.println("  Author: " + post.getAuthor());
            System.out.println("  Date: " + post.getTimestamp());
            System.out.println("  Likes: " + post.getLikes());
            System.out.println("  Video: " + post.getMetadata().get("video_title"));
            System.out.println("  Channel: " + post.getMetadata().get("channel"));

            String content = post.getContent();
            if (content.length() > 100) {
                content = content.substring(0, 100) + "...";
            }
            System.out.println("  Content: " + content);
        }

        // Statistics
        System.out.println("\n--- Statistics ---");
        int totalLikes = posts.stream()
                .mapToInt(SocialPost::getLikes)
                .sum();

        double avgLikes = posts.stream()
                .mapToInt(SocialPost::getLikes)
                .average()
                .orElse(0);

        System.out.println("Total likes: " + totalLikes);
        System.out.println("Average likes per comment: " + String.format("%.2f", avgLikes));

        System.out.println("\n--- Saving Results ---");
        try {
            java.io.File dataDir = new java.io.File("data");
            if (!dataDir.exists())
                dataDir.mkdirs();

            String fileName = "data/youtube_posts.json";
            java.io.File file = new java.io.File(fileName);

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class,
                            (JsonSerializer<LocalDateTime>) (src, typeOfSrc,
                                    context) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .registerTypeAdapter(LocalDateTime.class,
                            (JsonDeserializer<LocalDateTime>) (json, type, context) -> LocalDateTime
                                    .parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .setPrettyPrinting()
                    .create();

            List<SocialPost> allPosts = new ArrayList<>();

            if (file.exists() && file.length() > 0) {
                try (FileReader reader = new FileReader(file)) {
                    Type listType = new TypeToken<List<SocialPost>>() {
                    }.getType();
                    List<SocialPost> existingPosts = gson.fromJson(reader, listType);
                    if (existingPosts != null) {
                        allPosts.addAll(existingPosts);
                    }
                }
            }

            allPosts.addAll(posts);

            try (java.io.FileWriter writer = new java.io.FileWriter(fileName)) {
                gson.toJson(allPosts, writer);
            }

            System.out.println("✓ Saved " + posts.size() + " new posts. Total: " + allPosts.size());
        } catch (Exception e) {
            System.err.println("✗ Failed to save data: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("\n========================================");
        System.out.println("✓ Test completed successfully!");
        System.out.println("========================================");
    }
}
package com.humanitarian.logistics.test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.humanitarian.logistics.collector.YouTubeCollector;
import com.humanitarian.logistics.config.AppConfig;
import com.humanitarian.logistics.config.YouTubeConfig;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;
import com.humanitarian.logistics.userInterface.InputBoxController;
import com.humanitarian.logistics.userInterface.InputData;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class TestYoutubeCollectorUI extends Application {
	public static InputData userData;
	
	@Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/InputInterface.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            InputBoxController controller = loader.getController();
            
            primaryStage.setTitle("Input Example");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            controller.setOnSubmit(data -> {
                userData = data;
            });
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
        if (userData == null) {
        	System.out.println("Cancelling search...");
        	return;
        }
        
        System.out.println("========================================");
        System.out.println("YouTube Collector Test");
        System.out.println("========================================\n");
        
        // Load config
        AppConfig appConfig = new AppConfig();
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
        SearchCriteria criteria = new SearchCriteria.Builder()
            .keyword(userData.getKeyWord())
            .hashtags(userData.getHashTags())
            .dateRange(
                userData.getStartDate(),
                userData.getEndDate()
            )
            .language("vi")
            .maxResults(userData.getMaxResult())
            .build();
        
        System.out.println("Keyword: " + criteria.getKeyword());
        System.out.println("Max results: " + criteria.getMaxResults());
        
        // Collect
        System.out.println("\n--- Starting Collection ---");
        List<SocialPost> posts = collector.collect(criteria);
        
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
            if (!dataDir.exists()) dataDir.mkdirs();

            String fileName = "data/youtube_posts.json";

            // Custom Gson with LocalDateTime support
            Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, 
                    (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> 
                        new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .setPrettyPrinting()
                .create();

            try (java.io.FileWriter writer = new java.io.FileWriter(fileName)) {
                gson.toJson(posts, writer);
            }

            System.out.println("✓ Saved " + posts.size() + " posts to " + fileName);
        } catch (Exception e) {
            System.err.println("✗ Failed to save data: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n========================================");
        System.out.println("✓ Test completed successfully!");
        System.out.println("========================================");
    }
}
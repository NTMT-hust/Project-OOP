package com.humanitarian.logistics.collector;

import com.humanitarian.logistics.collector.strategy.MaxCollectionStrategy;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

/**
 * Manager for MAXIMUM data collection across all sources
 */
public class MaxCollectionManager {

    private static final Logger logger = LoggerFactory.getLogger(MaxCollectionManager.class);

    private final Map<String, Collector<SearchCriteria, ?, List<SocialPost>>> collectors;
    private final ExecutorService executor;

    public MaxCollectionManager() {
        this.collectors = new HashMap<>();
        this.executor = Executors.newFixedThreadPool(5);
        loadCollectors();
    }

    private void loadCollectors() {
        logger.info("Loading all available collectors...");
        for (Collector<SearchCriteria, ?, List<SocialPost>> collector : GenericCollectorFactory.getAllCollectors()) {

            if (collector.isInitialized()) {
                collectors.put(collector.getSource(), collector);
                logger.info("  ✓ Loaded: {}", collector.getSource());
            }
        }

        logger.info("Total collectors: {}", collectors.size());
    }

    /**
     * Collect MAXIMUM data from ALL sources in PARALLEL
     */
    public Map<String, List<SocialPost>> collectMaxFromAll(SearchCriteria criteria) {
        logger.info("╔════════════════════════════════════════╗");
        logger.info("║  MAXIMUM COLLECTION - ALL SOURCES      ║");
        logger.info("╚════════════════════════════════════════╝");
        logger.info("");
        logger.info("Target: Collect MAXIMUM possible data");
        logger.info("Strategy: Parallel execution with optimized algorithms");
        logger.info("Sources: {}", collectors.keySet());
        logger.info("");

        Map<String, List<SocialPost>> results = new ConcurrentHashMap<>();
        Map<String, Future<List<SocialPost>>> futures = new HashMap<>();

        long overallStart = System.currentTimeMillis();

        // Submit all collection tasks in parallel
        for (Map.Entry<String, Collector<SearchCriteria, ?, List<SocialPost>>> entry : collectors.entrySet()) {

            String source = entry.getKey();
            Collector<SearchCriteria, ?, List<SocialPost>> collector = entry.getValue();

            Future<List<SocialPost>> future = executor.submit(() -> {
                try {
                    return MaxCollectionStrategy.collectMax(collector, criteria);
                } catch (Exception e) {
                    logger.error("[{}] Collection failed: {}",
                            source.toUpperCase(), e.getMessage());
                    return new ArrayList<>();
                }
            });

            futures.put(source, future);
        }

        // Wait for all to complete
        logger.info("\n" + "=".repeat(60));
        logger.info("Waiting for all collections to complete...");
        logger.info("=".repeat(60) + "\n");

        for (Map.Entry<String, Future<List<SocialPost>>> entry : futures.entrySet()) {
            String source = entry.getKey();

            try {
                List<SocialPost> posts = entry.getValue().get(30, TimeUnit.MINUTES);
                results.put(source, posts);
                logger.info("[{}] ✓ Completed: {} posts",
                        source.toUpperCase(), posts.size());
            } catch (TimeoutException e) {
                logger.error("[{}] ✗ Timeout after 30 minutes", source.toUpperCase());
                results.put(source, new ArrayList<>());
            } catch (Exception e) {
                logger.error("[{}] ✗ Error: {}",
                        source.toUpperCase(), e.getMessage());
                results.put(source, new ArrayList<>());
            }
        }

        long overallDuration = System.currentTimeMillis() - overallStart;

        // Summary
        displaySummary(results, overallDuration);

        // Save results
        saveResults(results, criteria);

        return results;
    }

    /**
     * Collect maximum from single source
     */
    public List<SocialPost> collectMaxFromSource(String source, SearchCriteria criteria) {
        Collector<SearchCriteria, ?, List<SocialPost>> collector = collectors.get(source);

        if (collector == null) {
            logger.error("Collector not found: {}", source);
            return new ArrayList<>();
        }

        logger.info("╔════════════════════════════════════════╗");
        logger.info("║  MAXIMUM COLLECTION: {:18s} ║", source.toUpperCase());
        logger.info("╚════════════════════════════════════════╝");
        logger.info("");

        long startTime = System.currentTimeMillis();
        List<SocialPost> posts = MaxCollectionStrategy.collectMax(collector, criteria);
        long duration = System.currentTimeMillis() - startTime;

        logger.info("");
        logger.info("╔════════════════════════════════════════╗");
        logger.info("║  COLLECTION COMPLETE                   ║");
        logger.info("╚════════════════════════════════════════╝");
        logger.info("Source: {}", source);
        logger.info("Total posts: {}", posts.size());
        logger.info("Duration: {:.2f} seconds", duration / 1000.0);
        logger.info("");

        return posts;
    }

    /**
     * Display collection summary
     */
    private void displaySummary(Map<String, List<SocialPost>> results, long durationMs) {
        logger.info("\n");
        logger.info("╔══════════════════════════════════════════════════════════╗");
        logger.info("║           MAXIMUM COLLECTION SUMMARY                     ║");
        logger.info("╚══════════════════════════════════════════════════════════╝");
        logger.info("");

        // Table header
        logger.info("┌─────────────────────┬────────────────┬───────────────────┐");
        logger.info("│ Source              │ Posts Collected│ Status            │");
        logger.info("├─────────────────────┼────────────────┼───────────────────┤");

        int totalPosts = 0;
        for (Map.Entry<String, List<SocialPost>> entry : results.entrySet()) {
            String source = entry.getKey();
            int count = entry.getValue().size();
            totalPosts += count;

            String status = count > 0 ? "✓ SUCCESS" : "✗ FAILED";

            logger.info("│ {:19s} │ {:14d} │ {:17s} │",
                    source, count, status);
        }

        logger.info("├─────────────────────┼────────────────┼───────────────────┤");
        logger.info("│ {:19s} │ {:14d} │ {:17s} │",
                "TOTAL", totalPosts, "");
        logger.info("└─────────────────────┴────────────────┴───────────────────┘");
        logger.info("");

        // Statistics
        logger.info("Total duration: {:.2f} minutes", durationMs / 60000.0);
        logger.info("Average per source: {:.2f} posts",
                totalPosts / (double) results.size());

        // Top source
        String topSource = results.entrySet().stream()
                .max(Comparator.comparingInt(e -> e.getValue().size()))
                .map(Map.Entry::getKey)
                .orElse("None");

        int topCount = results.get(topSource).size();
        logger.info("Top source: {} ({} posts)", topSource, topCount);
        logger.info("");
    }

    /**
     * Save results to files
     */
    private void saveResults(Map<String, List<SocialPost>> results, SearchCriteria criteria) {
        try {
            String timestamp = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            String filename = String.format("output/max_collection_%s.txt", timestamp);

            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                writer.println("MAXIMUM COLLECTION RESULTS");
                writer.println("=".repeat(60));
                writer.println("Timestamp: " + LocalDateTime.now());
                writer.println("Keyword: " + criteria.getKeyword());
                writer.println("");

                for (Map.Entry<String, List<SocialPost>> entry : results.entrySet()) {
                    writer.println("");
                    writer.println(entry.getKey().toUpperCase() + ": " +
                            entry.getValue().size() + " posts");
                    writer.println("-".repeat(40));

                    for (int i = 0; i < Math.min(10, entry.getValue().size()); i++) {
                        SocialPost post = entry.getValue().get(i);
                        writer.println((i + 1) + ". " + post.getMetadata().get("title"));
                    }
                }
            }

            logger.info("✓ Results saved to: {}", filename);

        } catch (Exception e) {
            logger.error("Failed to save results: {}", e.getMessage());
        }
    }

    /**
     * Get aggregated results (all posts combined)
     */
    public List<SocialPost> getAggregatedResults(Map<String, List<SocialPost>> results) {
        List<SocialPost> allPosts = new ArrayList<>();

        for (List<SocialPost> posts : results.values()) {
            allPosts.addAll(posts);
        }

        // Remove duplicates
        return removeDuplicates(allPosts);
    }

    private List<SocialPost> removeDuplicates(List<SocialPost> posts) {
        Map<String, SocialPost> uniqueMap = new LinkedHashMap<>();

        for (SocialPost post : posts) {
            String key = post.getId();
            if (post.getMetadata().containsKey("url")) {
                key = (String) post.getMetadata().get("url");
            }

            if (!uniqueMap.containsKey(key)) {
                uniqueMap.put(key, post);
            }
        }

        return new ArrayList<>(uniqueMap.values());
    }

    public Set<String> getAvailableSources() {
        return collectors.keySet();
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
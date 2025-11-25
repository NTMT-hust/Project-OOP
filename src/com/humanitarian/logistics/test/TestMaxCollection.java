package com.humanitarian.logistics.test;

import com.humanitarian.logistics.collector.MaxCollectionManager;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Test Maximum Data Collection
 */
public class TestMaxCollection {

        private static final Logger logger = LoggerFactory.getLogger(TestMaxCollection.class);

        public static void main(String[] args) {
                logger.info("╔══════════════════════════════════════════════════════════╗");
                logger.info("║                                                          ║");
                logger.info("║      MAXIMUM DATA COLLECTION TEST                        ║");
                logger.info("║      Collect as much data as possible from each source   ║");
                logger.info("║                                                          ║");
                logger.info("╚══════════════════════════════════════════════════════════╝");
                logger.info("");

                // Create manager
                MaxCollectionManager manager = new MaxCollectionManager();

                logger.info("Available sources: {}", manager.getAvailableSources());
                logger.info("");

                if (manager.getAvailableSources().isEmpty()) {
                        logger.error("No collectors available!");
                        logger.error("Please configure at least one collector.");
                        return;
                }

                // Build criteria
                SearchCriteria criteria = new SearchCriteria.Builder()
                                .keyword("bão")
                                .hashtags()
                                .dateRange(
                                                LocalDateTime.now().minusDays(100),
                                                LocalDateTime.now())
                                .language("vi")
                                .maxResults(10) // Will be overridden by max strategy
                                .build();

                logger.info("Base Search Criteria:");
                logger.info("  Keyword: {}", criteria.getKeyword());
                logger.info("  Hashtags: {}", criteria.getHashtags());
                logger.info("  Date range: Last 30 days");
                logger.info("  Strategy: MAXIMUM collection per source");
                logger.info("");

                logger.info("Press ENTER to start maximum collection...");
                try {
                        System.in.read();
                } catch (Exception e) {
                        // Continue
                }

                logger.info("");
                logger.info("Starting MAXIMUM collection from ALL sources...");
                logger.info("This may take 10-30 minutes depending on sources.");
                logger.info("");

                // Collect maximum from all sources
                Map<String, List<SocialPost>> results = manager.collectMaxFromAll(criteria);

                // Get aggregated results
                List<SocialPost> allPosts = manager.getAggregatedResults(results);

                // Final summary
                logger.info("\n");
                logger.info("╔══════════════════════════════════════════════════════════╗");
                logger.info("║                  FINAL RESULTS                           ║");
                logger.info("╚══════════════════════════════════════════════════════════╝");
                logger.info("");
                logger.info("Total unique posts: {}", allPosts.size());
                logger.info("");

                // Distribution by source
                logger.info("Distribution:");
                Map<String, Long> distribution = allPosts.stream().collect(java.util.stream.Collectors.groupingBy(
                                SocialPost::getSource,
                                java.util.stream.Collectors.counting()));

                distribution.forEach((source, count) -> logger.info("  {}: {} posts ({:.1f}%)",
                                source, count, (count * 100.0) / allPosts.size()));

                logger.info("");

                // Date distribution
                LocalDateTime earliest = allPosts.stream()
                                .map(SocialPost::getTimestamp)
                                .min(LocalDateTime::compareTo)
                                .orElse(null);

                LocalDateTime latest = allPosts.stream()
                                .map(SocialPost::getTimestamp)
                                .max(LocalDateTime::compareTo)
                                .orElse(null);

                if (earliest != null && latest != null) {
                        logger.info("Date range:");
                        logger.info("  From: {}", earliest);
                        logger.info("  To: {}", latest);
                        logger.info("  Span: {} days",
                                        java.time.Duration.between(earliest, latest).toDays());
                }

                logger.info("");
                logger.info("╔══════════════════════════════════════════════════════════╗");
                logger.info("║                  COLLECTION COMPLETE                     ║");
                logger.info("╚══════════════════════════════════════════════════════════╝");

                // Cleanup
                manager.shutdown();
        }
}
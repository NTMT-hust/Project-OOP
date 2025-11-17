package com.humanitarian.logistics.collector.strategy;

import com.humanitarian.logistics.collector.Collector;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Strategy to collect MAXIMUM data from each source
 * Uses optimized pagination and rate limit management
 */
public class MaxCollectionStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(MaxCollectionStrategy.class);
    
    /**
     * Collect maximum possible data from a collector
     */
    public static List<SocialPost> collectMax(
        Collector<SearchCriteria, ?, List<SocialPost>> collector,
        SearchCriteria baseCriteria
    ) {
        
        String source = collector.getSource();
        logger.info("========================================");
        logger.info("MAXIMUM COLLECTION: {}", source.toUpperCase());
        logger.info("========================================");
        
        switch (source.toLowerCase()) {
            case "twitter":
                return collectMaxTwitter(collector, baseCriteria);
            case "youtube":
                return collectMaxYouTube(collector, baseCriteria);
            case "google-news":
                return collectMaxGoogleNews(collector, baseCriteria);
            case "google-cse":
                return collectMaxGoogleCse(collector, baseCriteria);
            case "newsapi":
                return collectMaxNewsApi(collector, baseCriteria);
            default:
                logger.warn("Unknown source: {}, using default strategy", source);
                return collector.collect(baseCriteria);
        }
    }
    
    /**
     * Twitter: Maximize within rate limits
     * Strategy: Multiple searches with different time ranges
     */
    private static List<SocialPost> collectMaxTwitter(
        Collector<SearchCriteria, ?, List<SocialPost>> collector,
        SearchCriteria baseCriteria
    ) {
        
        List<SocialPost> allPosts = new ArrayList<>();
        
        logger.info("Twitter Max Strategy:");
        logger.info("  - Split date range into chunks");
        logger.info("  - Use pagination fully");
        logger.info("  - Target: 10,000+ tweets");
        
        // Split date range into 7 chunks (1 week each)
        LocalDateTime endDate = baseCriteria.getEndDate() != null ? 
            baseCriteria.getEndDate() : LocalDateTime.now();
        
        LocalDateTime startDate = baseCriteria.getStartDate() != null ?
            baseCriteria.getStartDate() : endDate.minusDays(30);
        
        long totalDays = java.time.Duration.between(startDate, endDate).toDays();
        int chunks = Math.max(1, (int) (totalDays / 7)); // 7 days per chunk
        
        logger.info("Splitting into {} time chunks", chunks);
        
        for (int i = 0; i < chunks; i++) {
            LocalDateTime chunkStart = startDate.plusDays(i * 7);
            LocalDateTime chunkEnd = chunkStart.plusDays(7);
            
            if (chunkEnd.isAfter(endDate)) {
                chunkEnd = endDate;
            }
            
            logger.info("\n→ Chunk {}/{}: {} to {}", 
                i + 1, chunks, chunkStart.toLocalDate(), chunkEnd.toLocalDate());
            
            try {
                SearchCriteria chunkCriteria = new SearchCriteria.Builder()
                    .keyword(baseCriteria.getKeyword())
                    .hashtags(baseCriteria.getHashtags() != null ? 
                        baseCriteria.getHashtags().toArray(new String[0]) : new String[0])
                    .dateRange(chunkStart, chunkEnd)
                    .language(baseCriteria.getLanguage())
                    .maxResults(5000) // Max per chunk
                    .build();
                
                List<SocialPost> chunkPosts = collector.collect(chunkCriteria);
                allPosts.addAll(chunkPosts);
                
                logger.info("  Collected: {} tweets (Total: {})", 
                    chunkPosts.size(), allPosts.size());
                
                // Rate limit cooldown
                if (i < chunks - 1) {
                    logger.info("  Cooling down 60 seconds...");
                    Thread.sleep(60000);
                }
                
            } catch (Exception e) {
                logger.error("Chunk {} failed: {}", i + 1, e.getMessage());
            }
        }
        
        logger.info("\n✓ Twitter total: {} tweets", allPosts.size());
        return removeDuplicates(allPosts);
    }
    
    /**
     * YouTube: Maximize quota usage
     * Strategy: Get many videos, collect all comments
     */
    private static List<SocialPost> collectMaxYouTube(
        Collector<SearchCriteria, ?, List<SocialPost>> collector,
        SearchCriteria baseCriteria
    ) {
        
        logger.info("YouTube Max Strategy:");
        logger.info("  - Search for maximum videos");
        logger.info("  - Collect ALL comments from each video");
        logger.info("  - Target: 5,000+ comments");
        
        // YouTube strategy: Get as many comments as quota allows
        SearchCriteria maxCriteria = new SearchCriteria.Builder()
            .keyword(baseCriteria.getKeyword())
            .hashtags(baseCriteria.getHashtags() != null ? 
                baseCriteria.getHashtags().toArray(new String[0]) : new String[0])
            .dateRange(
                baseCriteria.getStartDate(),
                baseCriteria.getEndDate()
            )
            .language(baseCriteria.getLanguage())
            .maxResults(10000) // Try to max out quota
            .build();
        
        List<SocialPost> posts = collector.collect(maxCriteria);
        
        logger.info("✓ YouTube total: {} comments", posts.size());
        return posts;
    }
    
    /**
     * Google News RSS: Multiple searches
     * Strategy: Different keyword variations
     */
    private static List<SocialPost> collectMaxGoogleNews(
        Collector<SearchCriteria, ?, List<SocialPost>> collector,
        SearchCriteria baseCriteria
    ) {
        
        List<SocialPost> allPosts = new ArrayList<>();
        
        logger.info("Google News RSS Max Strategy:");
        logger.info("  - Multiple keyword variations");
        logger.info("  - No rate limit!");
        logger.info("  - Target: 2,000+ articles");
        
        // Generate keyword variations
        List<String> keywordVariations = generateKeywordVariations(
            baseCriteria.getKeyword()
        );
        
        logger.info("Using {} keyword variations", keywordVariations.size());
        
        for (int i = 0; i < keywordVariations.size(); i++) {
            String keyword = keywordVariations.get(i);
            logger.info("\n→ Variation {}/{}: \"{}\"", 
                i + 1, keywordVariations.size(), keyword);
            
            try {
                SearchCriteria varCriteria = new SearchCriteria.Builder()
                    .keyword(keyword)
                    .dateRange(
                        baseCriteria.getStartDate(),
                        baseCriteria.getEndDate()
                    )
                    .maxResults(100)
                    .build();
                
                List<SocialPost> posts = collector.collect(varCriteria);
                allPosts.addAll(posts);
                
                logger.info("  Collected: {} articles (Total: {})", 
                    posts.size(), allPosts.size());
                
                // Small delay to be polite
                Thread.sleep(2000);
                
            } catch (Exception e) {
                logger.error("Variation \"{}\" failed: {}", keyword, e.getMessage());
            }
        }
        
        logger.info("\n✓ Google News total: {} articles", allPosts.size());
        return removeDuplicates(allPosts);
    }
    
    /**
     * Google CSE: Use all 100 daily queries
     * Strategy: Multiple searches with variations
     */
    private static List<SocialPost> collectMaxGoogleCse(
        Collector<SearchCriteria, ?, List<SocialPost>> collector,
        SearchCriteria baseCriteria
    ) {
        
        List<SocialPost> allPosts = new ArrayList<>();
        
        logger.info("Google CSE Max Strategy:");
        logger.info("  - Use all 100 daily queries");
        logger.info("  - 100 results per query");
        logger.info("  - Target: 10,000 articles");
        
        // Generate keyword variations (limit to 100 total queries)
        List<String> keywordVariations = generateKeywordVariations(
            baseCriteria.getKeyword()
        );
        
        int maxQueries = 100;
        int queriesPerKeyword = maxQueries / keywordVariations.size();
        
        logger.info("Using {} keywords, {} queries each", 
            keywordVariations.size(), queriesPerKeyword);
        
        for (String keyword : keywordVariations) {
            logger.info("\n→ Keyword: \"{}\"", keyword);
            
            try {
                SearchCriteria varCriteria = new SearchCriteria.Builder()
                    .keyword(keyword)
                    .dateRange(
                        baseCriteria.getStartDate(),
                        baseCriteria.getEndDate()
                    )
                    .maxResults(100) // Max per search
                    .build();
                
                List<SocialPost> posts = collector.collect(varCriteria);
                allPosts.addAll(posts);
                
                logger.info("  Collected: {} articles (Total: {})", 
                    posts.size(), allPosts.size());
                
                // Rate limit
                Thread.sleep(1000);
                
            } catch (Exception e) {
                logger.error("Keyword \"{}\" failed: {}", keyword, e.getMessage());
            }
        }
        
        logger.info("\n✓ Google CSE total: {} articles", allPosts.size());
        return removeDuplicates(allPosts);
    }
    
    /**
     * NewsAPI: Use all 100 daily requests
     * Strategy: Different time windows
     */
    private static List<SocialPost> collectMaxNewsApi(
        Collector<SearchCriteria, ?, List<SocialPost>> collector,
        SearchCriteria baseCriteria
    ) {
        
        List<SocialPost> allPosts = new ArrayList<>();
        
        logger.info("NewsAPI Max Strategy:");
        logger.info("  - Use all 100 daily requests");
        logger.info("  - Split into time windows");
        logger.info("  - Target: 10,000 articles");
        
        // NewsAPI free tier: max 1 month back
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);
        
        // Split into daily windows (30 days)
        for (int day = 0; day < 30; day++) {
            LocalDateTime dayStart = startDate.plusDays(day);
            LocalDateTime dayEnd = dayStart.plusDays(1);
            
            logger.info("\n→ Day {}/30: {}", day + 1, dayStart.toLocalDate());
            
            try {
                SearchCriteria dayCriteria = new SearchCriteria.Builder()
                    .keyword(baseCriteria.getKeyword())
                    .dateRange(dayStart, dayEnd)
                    .maxResults(100)
                    .build();
                
                List<SocialPost> posts = collector.collect(dayCriteria);
                allPosts.addAll(posts);
                
                logger.info("  Collected: {} articles (Total: {})", 
                    posts.size(), allPosts.size());
                
                // Rate limit
                Thread.sleep(1000);
                
            } catch (Exception e) {
                logger.error("Day {} failed: {}", day + 1, e.getMessage());
            }
        }
        
        logger.info("\n✓ NewsAPI total: {} articles", allPosts.size());
        return removeDuplicates(allPosts);
    }
    
    /**
     * Generate keyword variations for broader search
     */
    private static List<String> generateKeywordVariations(String baseKeyword) {
        List<String> variations = new ArrayList<>();
        
        // Add base keyword
        variations.add(baseKeyword);
        
        // Vietnamese specific variations for "bão Yagi"
        if (baseKeyword.toLowerCase().contains("bão yagi")) {
            variations.add("siêu bão Yagi");
            variations.add("bão Yagi Việt Nam");
            variations.add("thiên tai bão Yagi");
            variations.add("bão Yagi miền Bắc");
            variations.add("thiệt hại bão Yagi");
            variations.add("cứu trợ bão Yagi");
            variations.add("Typhoon Yagi Vietnam");
        }
        
        // Generic variations
        variations.add(baseKeyword + " Việt Nam");
        variations.add(baseKeyword + " Vietnam");
        
        return variations;
    }
    
    /**
     * Remove duplicate posts by URL or ID
     */
    private static List<SocialPost> removeDuplicates(List<SocialPost> posts) {
        java.util.Map<String, SocialPost> uniqueMap = new java.util.LinkedHashMap<>();
        
        for (SocialPost post : posts) {
            String key = post.getId();
            
            // Try URL if available
            if (post.getMetadata().containsKey("url")) {
                key = (String) post.getMetadata().get("url");
            }
            
            if (!uniqueMap.containsKey(key)) {
                uniqueMap.put(key, post);
            }
        }
        
        int duplicatesRemoved = posts.size() - uniqueMap.size();
        if (duplicatesRemoved > 0) {
            logger.info("Removed {} duplicates", duplicatesRemoved);
        }
        
        return new ArrayList<>(uniqueMap.values());
    }
}
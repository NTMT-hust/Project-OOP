// collector/TwitterCollector.java
package com.humanitarian.logistics.collector;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import com.humanitarian.logistics.config.TwitterConfig;
import com.humanitarian.logistics.util.CustomRateLimiter;
import com.humanitarian.logistics.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitterCollector{
    private static final Logger logger = LoggerFactory.getLogger(TwitterCollector.class);
    
    private Twitter twitter;
    private TwitterConfig config;
    private CustomRateLimiter rateLimiter;
    
    public TwitterCollector(TwitterConfig config) {
        this.config = config;
        initializeClient();
        this.rateLimiter = new CustomRateLimiter(
            config.getRateLimit(),
            config.getRateWindow()
        );
    }
    
    private void initializeClient() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb  .setDebugEnabled(true)
            .setOAuthConsumerKey(config.getApiKey())
            .setOAuthConsumerSecret(config.getApiSecret())
            .setOAuthAccessToken(config.getAccessToken())
            .setOAuthAccessTokenSecret(config.getAccessSecret());
        
        TwitterFactory tf = new TwitterFactory(cb.build());
        this.twitter = tf.getInstance();
        
        logger.info("Twitter client initialized");
    }
    

    public String getSourceName() {
        return "twitter";
    }
    

    public boolean testConnection() {
        try {
            twitter.getAccountSettings(); // this verifies credentials implicitly
            logger.info("Twitter connection successful");
            return true;
        } catch (TwitterException e) {
            logger.error("Twitter connection failed: {}", e.getMessage());
            return false;
        }
    }


    public List<SocialPost> collect(SearchCriteria criteria) {
        List<SocialPost> posts = new ArrayList<>();
        
        try {
            Query query = buildQuery(criteria);
            
            logger.info("Starting Twitter collection with query: {}", query.getQuery());
            
            int totalCollected = 0;
            int maxResults = criteria.getMaxResults();
            
            // Pagination
            QueryResult result;
            do {
                // Rate limiting
                rateLimiter.acquire();
                
                // Execute search
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                
                logger.info("Fetched {} tweets", tweets.size());
                
                // Convert to SocialPost
                for (Status status : tweets) {
                    if (totalCollected >= maxResults) {
                        break;
                    }
                    
                    SocialPost post = convertToPost(status);
                    posts.add(post);
                    totalCollected++;
                }
                
                // Next page
                query = result.nextQuery();
                
                if (totalCollected >= maxResults) {
                    break;
                }
                
            } while (query != null && result.getTweets().size() > 0);
            
            logger.info("Twitter collection completed. Total posts: {}", posts.size());
            
        } catch (TwitterException e) {
            logger.error("Twitter collection failed", e);
            handleTwitterException(e);
        } catch (InterruptedException e) {
            logger.error("Rate limiter interrupted", e);
            Thread.currentThread().interrupt();
        }
        
        return posts;
    }
    
    /**
     * Build Twitter query from criteria
     */
    private Query buildQuery(SearchCriteria criteria) {
        StringBuilder queryStr = new StringBuilder();
        
        // Keyword
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            queryStr.append(criteria.getKeyword());
        }
        
        // Hashtags
        if (criteria.getHashtags() != null && !criteria.getHashtags().isEmpty()) {
            for (String hashtag : criteria.getHashtags()) {
                queryStr.append(" ").append(hashtag.startsWith("#") ? hashtag : "#" + hashtag);
            }
        }
        
        // Language
        if (criteria.getLanguage() != null) {
            queryStr.append(" lang:").append(criteria.getLanguage());
        }
        
        // Exclude retweets (optional)
        queryStr.append(" -filter:retweets");
        
        Query query = new Query(queryStr.toString());
        
        // Date range
        if (criteria.getStartDate() != null) {
            query.setSince(criteria.getStartDate().toLocalDate().toString());
        }
        if (criteria.getEndDate() != null) {
            query.setUntil(criteria.getEndDate().toLocalDate().toString());
        }
        
        // Results per page (max 100)
        query.setCount(100);
        
        // Result type: recent, popular, mixed
        query.setResultType(Query.ResultType.recent);
        
        return query;
    }
    
    /**
     * Convert Twitter Status to SocialPost
     */
    private SocialPost convertToPost(Status status) {
        SocialPost post = new SocialPost();
        
        // Basic info
        post.setId(String.valueOf(status.getId()));
        post.setContent(getFullText(status));
        post.setTimestamp(LocalDateTime.ofInstant(
            status.getCreatedAt().toInstant(),
            ZoneId.systemDefault()
        ));
        post.setSource("twitter");
        
        // Author info
        User user = status.getUser();
        post.setAuthor(user.getScreenName());
        
        // Engagement metrics
        post.setLikes(status.getFavoriteCount());
        post.setShares(status.getRetweetCount());
        post.setComments(0); // Twitter4J doesn't provide reply count easily
        
        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("user_name", user.getName());
        metadata.put("user_verified", user.isVerified());
        metadata.put("user_followers", user.getFollowersCount());
        metadata.put("user_location", user.getLocation());
        metadata.put("is_retweet", status.isRetweet());
        
        // Hashtags
        HashtagEntity[] hashtags = status.getHashtagEntities();
        List<String> hashtagList = new ArrayList<>();
        for (HashtagEntity hashtag : hashtags) {
            hashtagList.add(hashtag.getText());
        }
        metadata.put("hashtags", hashtagList);
        
        // URLs
        URLEntity[] urls = status.getURLEntities();
        List<String> urlList = new ArrayList<>();
        for (URLEntity url : urls) {
            urlList.add(url.getExpandedURL());
        }
        metadata.put("urls", urlList);
        
        // Location (if available)
        GeoLocation geo = status.getGeoLocation();
        if (geo != null) {
            metadata.put("latitude", geo.getLatitude());
            metadata.put("longitude", geo.getLongitude());
        }
        
        Place place = status.getPlace();
        if (place != null) {
            metadata.put("place_name", place.getFullName());
            metadata.put("place_country", place.getCountry());
        }
        
        post.setMetadata(metadata);
        
        return post;
    }
    
    /**
     * Get full text (handle truncated tweets)
     */
    private String getFullText(Status status) {
        // Check if it's a retweet
        if (status.isRetweet()) {
            return status.getRetweetedStatus().getText();
        }
        
        // For extended tweets (>140 chars)
        if (status.getText().endsWith("…")) {
            // Try to get full text if available
            return status.getText(); // Twitter4J handles this automatically
        }
        
        return status.getText();
    }
    
    /**
     * Handle Twitter API exceptions
     */
    private void handleTwitterException(TwitterException e) {
        int statusCode = e.getStatusCode();
        
        switch (statusCode) {
            case 401:
                logger.error("Authentication failed. Check your API credentials.");
                break;
            case 403:
                logger.error("Access forbidden. Your app may not have the required permissions.");
                break;
            case 429:
                logger.error("Rate limit exceeded. Wait before retrying.");
                RateLimitStatus rateLimit = e.getRateLimitStatus();
                if (rateLimit != null) {
                    int secondsUntilReset = rateLimit.getSecondsUntilReset();
                    logger.error("Rate limit resets in {} seconds", secondsUntilReset);
                }
                break;
            case 503:
                logger.error("Twitter service unavailable. Try again later.");
                break;
            default:
                logger.error("Twitter API error ({}): {}", statusCode, e.getMessage());
        }
    }
    
    /**
     * Get user timeline (alternative method)
     */
    public List<SocialPost> collectUserTimeline(String username, int count) {
        List<SocialPost> posts = new ArrayList<>();
        
        try {
            Paging paging = new Paging(1, count);
            List<Status> statuses = twitter.getUserTimeline(username, paging);
            
            for (Status status : statuses) {
                posts.add(convertToPost(status));
            }
            
            logger.info("Collected {} tweets from @{}", posts.size(), username);
            
        } catch (TwitterException e) {
            logger.error("Failed to collect user timeline", e);
        }
        
        return posts;
    }
    
    /**
     * Get trending topics (for discovery)
     */
    public List<String> getTrendingTopics(int woeid) {
        List<String> trends = new ArrayList<>();
        
        try {
            // WOEID for Vietnam: 23424984
            // WOEID for Hanoi: 1236594
            Trends trendsList = twitter.getPlaceTrends(woeid);
            
            for (Trend trend : trendsList.getTrends()) {
                trends.add(trend.getName());
            }
            
            logger.info("Found {} trending topics", trends.size());
            
        } catch (TwitterException e) {
            logger.error("Failed to get trending topics", e);
        }
        
        return trends;
    }
}
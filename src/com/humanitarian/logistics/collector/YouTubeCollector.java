package com.humanitarian.logistics.collector;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.humanitarian.logistics.config.YouTubeConfig;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YouTubeCollector extends Collector<SearchCriteria, Object, List<SocialPost>> {
    private YouTube youtube;
    private YouTubeConfig config;
    
    private static final Logger logger = LoggerFactory.getLogger(YouTubeCollector.class);

    public YouTubeCollector(YouTubeConfig config) {
        super("youtube");
        this.config = config;
        if (!config.isValid()) {
            throw new IllegalStateException("YouTube API key is not configured");
        }
        initializeClient();
    }

    @Override
    public void initializeClient() {
        try {
            youtube = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    null)
                    .setApplicationName("Humanitarian-Logistics-Analyzer")
                    .build();

            logger.info("✓ YouTube client initialized");
        } catch (Exception e) {
        	logger.error("✗ Failed to initialize YouTube client: " + e.getMessage());
            throw new RuntimeException("YouTube initialization failed", e);
        }
    }

    @Override
    public boolean testConnection() {
        try {
            YouTube.Search.List search = youtube.search().list(Collections.singletonList("snippet"));
            search.setKey(config.getApiKey());
            search.setQ("test");
            search.setMaxResults(1L);
            search.execute();

            logger.info("✓ YouTube connection successful");
            return true;
        } catch (Exception e) {
        	logger.error("✗ YouTube connection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Thu thập comments từ YouTube
     */
    @Override
    public List<SocialPost> doCollect(SearchCriteria criteria) {
        List<SocialPost> posts = new ArrayList<>();

        try {
            logger.info("Step 1: Searching for videos...");
            List<String> videoIds = searchWithPagination(criteria);
            logger.info("Found " + videoIds.size() + " videos");

            if (videoIds.isEmpty()) {
                logger.info("No videos found matching criteria");
                return posts;
            }

            logger.info("\nStep 2: Collecting comments from videos...");
            int totalComments = 0;

            for (int i = 0; i < videoIds.size(); i++) {
                String videoId = videoIds.get(i);
                logger.info("  [" + (i + 1) + "/" + videoIds.size() + "] Video: " + videoId);

                try {
                    List<SocialPost> videoComments = getVideoComments(videoId, criteria);
                    posts.addAll(videoComments);
                    totalComments += videoComments.size();

                    logger.info("    → Collected " + videoComments.size() + " comments");

                    if (posts.size() >= criteria.getMaxResults()) {
                    	logger.info("    → Reached max results limit");
                        break;
                    }

                    // Rate limiting - wait 1 second between videos
                    if (i < videoIds.size() - 1) {
                        Thread.sleep(500);
                    }

                } catch (Exception e) {
                	logger.error("    ✗ Error getting comments: " + e.getMessage());
                }
            }

            logger.info("\n✓ YouTube collection completed");
            logger.info("Total posts: " + posts.size());

        } catch (Exception e) {
        	logger.error("Collection failed: " + e.getMessage());
            e.printStackTrace();
        }

        return posts;
    }

    public List<String> searchWithPagination(SearchCriteria criteria)
            throws IOException {
        List<SearchResult> allVideos = new ArrayList<>();
        List<String> videoIds = new ArrayList<>();
        String nextPageToken = null;

        while (allVideos.size() < 50) {
            // 1. Create request
            YouTube.Search.List search = youtube.search().list(Arrays.asList("id", "snippet"));
            search.setKey(config.getApiKey());

            // Build query
            String query = criteria.getKeyword();
            if (criteria.getHashtags() != null && !criteria.getHashtags().isEmpty()) {
                query += " " + String.join(" ", criteria.getHashtags());
            }
            search.setQ(query);

            search.setQ(query);
            search.setType(List.of("video"));
            search.setMaxResults(50L); // Max per page
            search.setPageToken(nextPageToken); // Give me the NEXT page

            // 2. Execute
            SearchListResponse response = search.execute();
            List<SearchResult> results = response.getItems();
            for (SearchResult item : response.getItems()) {
                videoIds.add(item.getId().getVideoId());
            }

            // 3. Add to list
            if (results == null || results.isEmpty())
                break;
            allVideos.addAll(results);

            // 4. Update Token
            nextPageToken = response.getNextPageToken();

            // 5. Stop if no more pages
            if (nextPageToken == null)
                break;
        }

        return videoIds;
    }

    /**
     * Tìm kiếm videos
     */
    private List<String> searchVideos(SearchCriteria criteria) throws Exception {
        List<String> videoIds = new ArrayList<>();

        YouTube.Search.List search = youtube.search().list(Arrays.asList("id", "snippet"));
        search.setKey(config.getApiKey());

        // Build query
        String query = criteria.getKeyword();
        if (criteria.getHashtags() != null && !criteria.getHashtags().isEmpty()) {
            query += " " + String.join(" ", criteria.getHashtags());
        }
        search.setQ(query);

        // Filters
        search.setType(List.of("video"));
        search.setMaxResults(1000L); // Tìm tối đa 10 videos
        search.setOrder("date"); // Sắp xếp theo ngày mới nhất

        // Date filters
        if (criteria.getStartDate() != null) {
            Instant instant = criteria.getStartDate()
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
            search.setPublishedAfter(instant.toString());
        }

        if (criteria.getEndDate() != null) {
            Instant instant = criteria.getEndDate()
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
            search.setPublishedBefore(instant.toString());
        }

        // Language
        if (criteria.getLanguage() != null) {
            search.setRelevanceLanguage(criteria.getLanguage());
        }

        // Execute search
        SearchListResponse response = search.execute();

        for (SearchResult item : response.getItems()) {
            videoIds.add(item.getId().getVideoId());
        }

        return videoIds;
    }

    /**
     * Lấy comments từ một video
     */
    private List<SocialPost> getVideoComments(String videoId, SearchCriteria criteria)
            throws Exception {

        List<SocialPost> posts = new ArrayList<>();

        try {
            // Get video details first
            VideoDetails videoDetails = getVideoDetails(videoId);

            // Get comment threads
            YouTube.CommentThreads.List request = youtube.commentThreads().list(Arrays.asList("snippet", "replies"));

            request.setKey(config.getApiKey());
            request.setVideoId(videoId);
            request.setTextFormat("plainText");
            request.setMaxResults(50L);

            CommentThreadListResponse response = request.execute();

            for (CommentThread thread : response.getItems()) {
                // Top-level comment
                Comment topComment = thread.getSnippet().getTopLevelComment();
                SocialPost post = convertCommentToPost(topComment, videoDetails);
                posts.add(post);

                // Replies
                if (thread.getSnippet().getTotalReplyCount() > 0) {
                    CommentThreadReplies replies = thread.getReplies();
                    if (replies != null && replies.getComments() != null) {
                        for (Comment reply : replies.getComments()) {
                            SocialPost replyPost = convertCommentToPost(reply, videoDetails);
                            replyPost.getMetadata().put("is_reply", true);
                            posts.add(replyPost);
                        }
                    }
                }

                if (posts.size() >= criteria.getMaxResults()) {
                    break;
                }
            }

        } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
            if (e.getMessage().contains("commentsDisabled")) {
                System.out.println("    ⚠ Comments disabled for this video");
            } else {
                throw e;
            }
        }

        return posts;
    }

    /**
     * Lấy thông tin chi tiết video
     */
    private VideoDetails getVideoDetails(String videoId) throws Exception {
        YouTube.Videos.List request = youtube.videos().list(Arrays.asList("snippet", "statistics"));

        request.setKey(config.getApiKey());
        request.setId(Collections.singletonList(videoId)); // cleaner

        VideoListResponse response = request.execute();

        if (response.getItems().isEmpty()) {
            return new VideoDetails(videoId, "Unknown", "Unknown");
        }

        Video video = response.getItems().get(0);
        VideoSnippet snippet = video.getSnippet();
        VideoStatistics stats = video.getStatistics();

        VideoDetails details = new VideoDetails(
                videoId,
                snippet.getTitle(),
                snippet.getChannelTitle());

        // NEW: Add description
        details.setDescription(snippet.getDescription());

        // Stats
        if (stats != null) {
            details.setViewCount(stats.getViewCount() != null ? stats.getViewCount().longValue() : 0);
            details.setLikeCount(stats.getLikeCount() != null ? stats.getLikeCount().longValue() : 0);
            details.setCommentCount(stats.getCommentCount() != null ? stats.getCommentCount().longValue() : 0);
        }

        return details;
    }

    /**
     * Convert YouTube comment to SocialPost
     */
    private SocialPost convertCommentToPost(Comment comment, VideoDetails videoDetails) {
        SocialPost post = new SocialPost();

        CommentSnippet snippet = comment.getSnippet();

        // Basic info
        post.setId(comment.getId());
        post.setContent(snippet.getTextDisplay());
        post.setTimestamp(LocalDateTime.ofInstant(
                Instant.ofEpochMilli(snippet.getPublishedAt().getValue()),
                ZoneId.systemDefault()));
        post.setSource("youtube");
        post.setAuthor(snippet.getAuthorDisplayName());

        // Metrics
        post.setLikes(snippet.getLikeCount() != null ? snippet.getLikeCount().intValue() : 0);
        post.setShares(0);
        post.setComments(0);

        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("video_id", videoDetails.getVideoId());
        metadata.put("video_title", videoDetails.getVideoTitle());
        metadata.put("video description", videoDetails.getDescription());
        metadata.put("channel", videoDetails.getChannelTitle());
        metadata.put("author_channel_id", snippet.getAuthorChannelId());
        metadata.put("comment_kind", comment.getKind());
        metadata.put("etag", comment.getEtag());

        post.setMetadata(metadata);

        return post;
    }

    @Override
    public List<SocialPost> getEmptyResult() {
        return new ArrayList<>();
    }

    /**
     * Inner class để lưu thông tin video
     */
    private static class VideoDetails {
        private String videoId;
        private String videoTitle;
        private String channelTitle;
        private long viewCount;
        private long likeCount;
        private long commentCount;
        private String description;

        public VideoDetails(String videoId, String videoTitle, String channelTitle) {
            this.videoId = videoId;
            this.videoTitle = videoTitle;
            this.channelTitle = channelTitle;
        }

        public String getVideoId() {
            return videoId;
        }

        public String getVideoTitle() {
            return videoTitle;
        }

        public String getChannelTitle() {
            return channelTitle;
        }

        public long getViewCount() {
            return viewCount;
        }

        public long getLikeCount() {
            return likeCount;
        }

        public long getCommentCount() {
            return commentCount;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setViewCount(long viewCount) {
            this.viewCount = viewCount;
        }

        public void setLikeCount(long likeCount) {
            this.likeCount = likeCount;
        }

        public void setCommentCount(long commentCount) {
            this.commentCount = commentCount;
        }
    }
}
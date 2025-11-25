package com.humanitarian.logistics.collector.youtube;

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

public class YouTubeCollector {
    private YouTube youtube;
    private YouTubeConfig config;
    
    private boolean initialize;
    
    public YouTubeCollector(YouTubeConfig config) throws IOException {
        this.config = config;
        if (!config.isValid()) {
            throw new IllegalStateException("YouTube API key is not configured");
        }
        initializeClient();
    }
    
    private void initializeClient() {
        try {
            youtube = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                null
            )
            .setApplicationName("Humanitarian-Logistics-Analyzer")
            .build();
            this.initialize = true;
        } catch (Exception e) {
        	this.initialize = false;
        }
    }
    
    public boolean getInitialize() {
    	return this.initialize;
    }
    
    /**
     * Thu thập comments từ YouTube
     */
    public List<SocialPost> collect(SearchCriteria criteria) {
        List<SocialPost> posts = new ArrayList<>();
        
        try {
            System.out.println("Searching for videos...");
            List<String> videoIds = searchVideos(criteria);
            System.out.println("Found " + videoIds.size() + " videos");
            
            if (videoIds.isEmpty()) {
                System.out.println("No videos found matching criteria");
                return posts;
            }
            
            System.out.println("\nStep 2: Collecting comments from videos...");
            int totalComments = 0;
            
            for (int i = 0; i < videoIds.size(); i++) {
                String videoId = videoIds.get(i);
                System.out.println("  [" + (i+1) + "/" + videoIds.size() + "] Video: " + videoId);
                
                try {
                    List<SocialPost> videoComments = getVideoComments(videoId, criteria);
                    posts.addAll(videoComments);
                    totalComments += videoComments.size();
                    
                    System.out.println("    → Collected " + videoComments.size() + " comments");
                    
                    if (posts.size() >= criteria.getMaxResults()) {
                        System.out.println("    → Reached max results limit");
                        break;
                    }
                    
//                    Rate limiting - wait 1 second between videos
                    if (i < videoIds.size() - 1) {
                        Thread.sleep(1000);
                    }
//                    
                } catch (Exception e) {
                    System.err.println("    ✗ Error getting comments: " + e.getMessage());
                }
            }
            
            System.out.println("\n✓ YouTube collection completed");
            System.out.println("Total posts: " + posts.size());
            
        } catch (Exception e) {
            System.err.println("Collection failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        return posts;
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
        search.setMaxResults(10L); // Tìm tối đa 10 videos
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
            request.setMaxResults(100L);
            
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
        request.setId(Arrays.asList(videoId));
        
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
            snippet.getChannelTitle()
        );
        
        if (stats != null) {
            details.setViewCount(stats.getViewCount() != null ? 
                stats.getViewCount().longValue() : 0);
            details.setLikeCount(stats.getLikeCount() != null ? 
                stats.getLikeCount().longValue() : 0);
            details.setCommentCount(stats.getCommentCount() != null ? 
                stats.getCommentCount().longValue() : 0);
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
            ZoneId.systemDefault()
        ));
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
        metadata.put("channel", videoDetails.getChannelTitle());
        metadata.put("author_channel_id", snippet.getAuthorChannelId());
        metadata.put("comment_kind", comment.getKind());
        metadata.put("etag", comment.getEtag());

        post.setMetadata(metadata);

        return post;
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
        
        public VideoDetails(String videoId, String videoTitle, String channelTitle) {
            this.videoId = videoId;
            this.videoTitle = videoTitle;
            this.channelTitle = channelTitle;
        }
        
        public String getVideoId() { return videoId; }
        public String getVideoTitle() { return videoTitle; }
        public String getChannelTitle() { return channelTitle; }
        public long getViewCount() { return viewCount; }
        public long getLikeCount() { return likeCount; }
        public long getCommentCount() { return commentCount; }
        
        public void setViewCount(long viewCount) { this.viewCount = viewCount; }
        public void setLikeCount(long likeCount) { this.likeCount = likeCount; }
        public void setCommentCount(long commentCount) { this.commentCount = commentCount; }
    }
}
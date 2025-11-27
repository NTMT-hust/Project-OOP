package com.humanitarian.logistics.collector.task;

import java.util.ArrayList;
import java.util.List;

import com.humanitarian.logistics.collector.YouTubeCollector;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;

import javafx.concurrent.Task;

public class YouTubeTaskCollector extends Task<List<SocialPost>> {
	
	private SearchCriteria criteria;
	private YouTubeCollector collector;
	
	public YouTubeTaskCollector(SearchCriteria criteria, YouTubeCollector collector) {
		this.criteria = criteria;
		this.collector = collector;
	}
	
	@Override
	protected List<SocialPost> call() throws Exception {
        List<SocialPost> posts = new ArrayList<>();

        updateMessage("Searching for videos...");
        List<String> videoIds = collector.searchWithPagination(criteria);
        updateMessage("Found " + videoIds.size() + " videos");

        if (videoIds.isEmpty()) {
        	return posts;
        }

        updateMessage("Collecting comments from videos...");

        for (int i = 0; i < videoIds.size(); i++) {
        	String videoId = videoIds.get(i);
        	updateMessage("  [" + (i + 1) + "/" + videoIds.size() + "] Video: " + videoId);

        	try {
        		List<SocialPost> videoComments = collector.getVideoComments(videoId, criteria);
        		posts.addAll(videoComments);

        		updateMessage("    → Collected " + videoComments.size() + " comments");

        		if (posts.size() >= criteria.getMaxResults()) {
        			updateMessage("    → Reached max results limit");
        			break;
        		}
                    
        	} catch (Exception e) {
        		updateMessage("    ✗ Error getting comments: " + e.getMessage());
        	}
        }

        updateMessage("\n✓ YouTube collection completed");
        updateMessage("Total posts: " + posts.size());

        return posts;
    }
}

package com.humanitarian.logistics.collector.youtube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        
		updateMessage("Step 1: Searching for videos...");
		List<String> videoIds = collector.searchVideos(criteria);
		updateMessage("Found " + videoIds.size() + " videos");
		
		if (videoIds.isEmpty()) {
			updateMessage("No videos found matching criteria");
			return posts;
		}
            
		updateMessage("\nStep 2: Collecting comments from videos...");
		
		for (int i = 0; i < videoIds.size(); i++) {
			String videoId = videoIds.get(i);
			updateMessage("  [" + (i+1) + "/" + videoIds.size() + "] Video: " + videoId);
                
			try {
				List<SocialPost> videoComments = collector.getVideoComments(videoId, criteria);
				posts.addAll(videoComments);
                    
				updateMessage("    → Collected " + videoComments.size() + " comments");
                    
				if (posts.size() >= criteria.getMaxResults()) {
					break;
				}
                    
			} catch (Exception e) {
				updateMessage("    ✗ Error getting comments: " + e.getMessage());
			}
		}
		updateMessage("Done searching");
            
        return posts;
	}
}

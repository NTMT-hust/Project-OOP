package com.humanitarian.logistics.userInterface;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.humanitarian.logistics.collector.youtube.YouTubeCollector;
import com.humanitarian.logistics.config.AppConfig;
import com.humanitarian.logistics.config.YouTubeConfig;
import com.humanitarian.logistics.dataStructure.InputData;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.concurrent.Task;

public class SearchingController {
	
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label statusLabel;
	
    private Stage stage;
    private Scene scene;
    private Parent root;
    
    private Task<Void> searchTask;
    
    public void startSearch() {
    	searchTask = new Task<Void>() {
    		@Override
    		protected Void call() throws Exception {
    			return null;
    		}
    		
    		@Override
            protected void succeeded() {
                statusLabel.setText("Search Done!");
            }

            @Override
            protected void cancelled() {
                statusLabel.setText("Search Cancelled.");
            }

            @Override
            protected void failed() {
                statusLabel.setText("Error occurred!");
            }
    	};
    }
	
	public void savePost(List<SocialPost> resultPost) throws IOException {
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
        
        java.io.FileWriter writer = new java.io.FileWriter(fileName);
        gson.toJson(resultPost, writer);
	}
	
	public void searchProcedure(InputData inputData) throws IOException {
		AppConfig appConfig = new AppConfig();
		YouTubeConfig config = new YouTubeConfig(appConfig);
		YouTubeCollector youtubeCollector = new YouTubeCollector(config);
		SearchCriteria searchCriteria = new SearchCriteria.Builder()
				.keyword(inputData.getKeyWord())
				.hashtags(inputData.getHashTags())
				.dateRange(
						inputData.getStartDate(),
						inputData.getEndDate()
						)
				.language("vi")
				.maxResults(inputData.getMaxResult())
				.build();
		List<SocialPost> resultPost = youtubeCollector.collect(searchCriteria);
		
		savePost(resultPost);
	}
	
}
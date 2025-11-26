package com.humanitarian.logistics.userInterface.youtube;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.humanitarian.logistics.collector.youtube.YouTubeCollector;
import com.humanitarian.logistics.collector.youtube.YouTubeTaskCollector;
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
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.concurrent.Task;

public class SearchingController {
	
	@FXML
	private ProgressBar progressBar;
	@FXML
	private VBox scenePane;
	
	List<SocialPost> resultPost;
	
	private AppConfig appConfig = new AppConfig();
	private YouTubeConfig config = new YouTubeConfig(appConfig);
	private YouTubeCollector youtubeCollector;
	
	@FXML
	public void initialize() throws IOException {
		youtubeCollector = new YouTubeCollector(config);
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
		SearchCriteria searchCriteria = new SearchCriteria.Builder()
				.keyword(inputData.getKeyWord())
				.hashtags(inputData.getHashTags())
				.dateRange(
						inputData.getStartDate(),
						inputData.getEndDate()
						)
				.language("vi")
				.maxResults(inputData.getMaxResult())
				.maxVideos(inputData.getMaxVideo())
				.build();
//		List<SocialPost> resultPost = youtubeCollector.collect(searchCriteria);
		
		YouTubeTaskCollector collectTask = new YouTubeTaskCollector(searchCriteria, this.youtubeCollector);
		
		progressBar.progressProperty().bind(collectTask.progressProperty());
		
		collectTask.setOnSucceeded(event -> {
			try {
				resultPost = collectTask.getValue();
				
	        	Stage currentStage = (Stage) scenePane.getScene().getWindow();
	        	currentStage.close();
				
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/youtube/SearchComplete.fxml"));
				Parent root;

				root = loader.load();
				Stage stage = new Stage();
        	
				Scene scene = new Scene(root);
//        		String css = this.getClass().getResource("/resources/youtube/InputInterface.css").toExternalForm();
//        		scene.getStylesheets().add(css);
				stage.setScene(scene);
				stage.setTitle("Complete searching!");
				stage.centerOnScreen();
				stage.show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		Thread t = new Thread(collectTask);
		t.setDaemon(true);
		t.start();
		
		savePost(resultPost);
	}
	
}
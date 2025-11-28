package com.humanitarian.logistics.userInterface.searching;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.humanitarian.logistics.collector.Collector;
import com.humanitarian.logistics.collector.task.GoogleCseTaskCollector;
import com.humanitarian.logistics.collector.task.YouTubeTaskCollector;
import com.humanitarian.logistics.collector.task.NewsApiTaskCollector;
import com.humanitarian.logistics.collector.task.TaskCollector;
import com.humanitarian.logistics.dataStructure.InputData;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SearchingController {
	
	@FXML
	private ProgressBar progressBar;
	@FXML
	private VBox scenePane;
	@FXML
	private Label statusLabel;
	
	private List<SocialPost> resultPost;
	private TaskCollector collectTask;
	
	private Collector collector;
	private String collectorType;
	
	public SearchingController(Collector collector, String collectorType) {
		this.collector = collector;
		this.collectorType = collectorType;
	}
	
	@FXML
	public void initialize() {
		switch (collectorType) {
			case "Youtube":
				collectTask = new YouTubeTaskCollector(this.collector);
				break;
		
			case "GoogleCSE":
				collectTask = new GoogleCseTaskCollector(this.collector);
				break;
			
			case "NewsAPI":
				collectTask = new NewsApiTaskCollector(this.collector);
				break;
			
			default:
				collectTask = null;
		}
	
		progressBar.progressProperty().bind(collectTask.progressProperty());
		statusLabel.textProperty().bind(collectTask.messageProperty());
	
		collectTask.setOnSucceeded(event -> {
			try {
				resultPost = collectTask.getValue();
			
				Stage currentStage = (Stage) scenePane.getScene().getWindow();
				currentStage.close();
			
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/SearchComplete.fxml"));
			
				Parent root = loader.load();
				Stage stage = new Stage();
    	
				Scene scene = new Scene(root);
//    			String css = this.getClass().getResource("/com/humanitarian/logistics/userInterface/inputBox/InputInterface.css").toExternalForm();
//    			scene.getStylesheets().add(css);
				stage.setScene(scene);
				stage.setTitle("Complete searching!");
				stage.centerOnScreen();
				stage.show();
			
				savePost(resultPost);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	
		collectTask.setOnFailed(event -> {
			try {
				Stage currentStage = (Stage) scenePane.getScene().getWindow();
				currentStage.close();
			
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Error.fxml"));
				Parent root = loader.load();
				Stage stage = new Stage();
        	
				Scene scene = new Scene(root);
//        		String css = this.getClass().getResource("/resources/InputInterface.css").toExternalForm();
//        		scene.getStylesheets().add(css);
				stage.setScene(scene);
				stage.setTitle("Error");
				stage.centerOnScreen();
				stage.show();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}
	
	public void savePost(List<SocialPost> resultPost) throws IOException {
        java.io.File dataDir = new java.io.File("data");
        if (!dataDir.exists()) dataDir.mkdirs();
        
        String fileName;
        
        switch (collectorType) {
        	case "Youtube":
        		fileName = "data/youtube_posts.json";
        		break;
        		
        	case "GoogleCSE":
        		fileName = "data/googlecse_posts.json";
        		break;
        	
        	case "NewsAPI":
        		fileName = "data/newsapi_posts.json";
        		break;
        	default:
        		fileName = "data/nosource_posts.json";
        		break;
        }
        // Custom Gson with LocalDateTime support
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, 
                (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> 
                    new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .setPrettyPrinting()
            .create();
        
        try (java.io.FileWriter writer = new java.io.FileWriter(fileName)) {
            gson.toJson(resultPost, writer);
        };
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
				.build();
		
		collectTask.setCriteria(searchCriteria);
		
		Thread t = new Thread(collectTask);
		t.setDaemon(true);
		t.start();
	}
	
}
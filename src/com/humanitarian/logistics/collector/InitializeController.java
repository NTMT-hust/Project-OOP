package com.humanitarian.logistics.collector;

import java.io.IOException;

import com.humanitarian.logistics.config.*;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class InitializeController {
	
	@FXML
	private ProgressBar progressBar;
	
	@FXML
	private Label statusLabel;
	
	@FXML
	private VBox rootPane;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private AppConfig appConfig;
	private Collector collector;
	
	public void initializeCollector(String collectorName) throws IOException {
		 switch (collectorName) {
		 	case "Youtube":
		 		appConfig = new AppConfig("youtube");
		 		YouTubeConfig youtubeConfig = new YouTubeConfig(appConfig);
		 		collector = new YouTubeCollector(youtubeConfig);
		 	
		 	case "GoogleCSE":
		 		appConfig = new AppConfig("google.cse");
		 		GoogleCseConfig googleCseConfig = new GoogleCseConfig(appConfig);
		 		collector = new GoogleCseCollector(googleCseConfig);
		 	
		 	case "NewsAPI":
		 		appConfig = new AppConfig("newsapi");
		 		NewsApiConfig newsApiConfig = new NewsApiConfig(appConfig);
		 		collector = new NewsCollector(newsApiConfig);
		 }
		 statusLabel.setText("Initializing " + collectorName + " Collector...");
		 
		 simulateInitialize();
	}
	
	public void simulateInitialize() throws IOException {
		
		KeyValue endValue;
		
        if (collector.testConnection()) {
    		// 1. Define the END value we want (Progress = 1.0)
            endValue = new KeyValue(progressBar.progressProperty(), 1.0);
        } else {
        	endValue = new KeyValue(progressBar.progressProperty(), 0.8);
        }
        // 2. Create a KeyFrame that says "Reach the endValue at 0.5 seconds"
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), endValue);

        // 3. Create the Timeline with that frame
        Timeline timeline = new Timeline(keyFrame);
        	
        // 4. Cleanup when done
        timeline.setOnFinished(e -> {        	
        	Stage currentStage = (Stage) rootPane.getScene().getWindow();
        	currentStage.close();
            
        	if (collector.testConnection()) {
        		try {
        			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/InputInterface.fxml"));
        			root = loader.load();
        			stage = new Stage();
            	
        			scene = new Scene(root);
//	        		String css = this.getClass().getResource("/resources/InputInterface.css").toExternalForm();
//	        		scene.getStylesheets().add(css);
        			stage.setScene(scene);
        			stage.setTitle("Input your request...");
        			stage.centerOnScreen();
        			stage.show();
        		} catch (IOException e1) {
        			// TODO Auto-generated catch block
        			e1.printStackTrace();
        			} 
        	} else {
        		try {
        			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Error.fxml"));
        			root = loader.load();
        			stage = new Stage();
                	
        			scene = new Scene(root);
//    	        	String css = this.getClass().getResource("/resources/InputInterface.css").toExternalForm();
//    	        	scene.getStylesheets().add(css);
        			stage.setScene(scene);
        			stage.setTitle("Input your request...");
        			stage.centerOnScreen();
        			stage.show();
   				} catch (IOException e1) {
   					// TODO Auto-generated catch block
   					e1.printStackTrace();
   				}
        	}
        });
        
        timeline.play();
	}
}

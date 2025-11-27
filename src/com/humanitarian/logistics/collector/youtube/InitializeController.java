package com.humanitarian.logistics.collector.youtube;

import java.io.IOException;

import com.humanitarian.logistics.config.AppConfig;
import com.humanitarian.logistics.config.YouTubeConfig;

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
	
	private AppConfig appConfig = new AppConfig("youtube");
	private YouTubeConfig config = new YouTubeConfig(appConfig);
	private YouTubeCollector youtubeCollector;
	
	@FXML
	public void initialize() throws IOException {
		initializeCollector();
		simulateInitialize();
	}
	
	public void initializeCollector() throws IOException {
		 this.youtubeCollector = new YouTubeCollector(config);
	}
	
	public void simulateInitialize() throws IOException {
		
		KeyValue endValue;
		
        if (youtubeCollector.getInitialize()) {
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
            
        	if (youtubeCollector.getInitialize()) {
        		try {
        			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/youtube/InputInterface.fxml"));
        			root = loader.load();
        			stage = new Stage();
            	
        			scene = new Scene(root);
//	        		String css = this.getClass().getResource("/resources/youtube/InputInterface.css").toExternalForm();
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
        			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/youtube/Error.fxml"));
        			root = loader.load();
        			stage = new Stage();
                	
        			scene = new Scene(root);
//    	        	String css = this.getClass().getResource("/resources/youtube/InputInterface.css").toExternalForm();
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

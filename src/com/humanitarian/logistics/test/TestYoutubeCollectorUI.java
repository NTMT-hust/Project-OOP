package com.humanitarian.logistics.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class TestYoutubeCollectorUI extends Application {
	
	@Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/youtube/InputInterface.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
//            String css = this.getClass().getResource("/resources/youtube/InputInterface.css").toExternalForm();
//            scene.getStylesheets().add(css);
            
            primaryStage.setTitle("Input Search Criteria");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
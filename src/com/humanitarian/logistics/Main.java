package com.humanitarian.logistics;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) throws IOException {
        // Load the FXML file
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/SelectionMenu.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
//        String css = this.getClass().getResource("/resources/youtube/InputInterface.css").toExternalForm();
//        scene.getStylesheets().add(css);
        
        primaryStage.setTitle("Humanitarian Logistics Project");
        primaryStage.setScene(scene);
        primaryStage.show();
	}
	
    public static void main(String[] args) {
        launch(args);
    }
}
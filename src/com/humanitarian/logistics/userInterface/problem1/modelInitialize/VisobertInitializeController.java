package com.humanitarian.logistics.userInterface.problem1.modelInitialize;

import java.io.IOException;

import com.humanitarian.logistics.sentimentAnalysis.Visobert;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class VisobertInitializeController {
	
	@FXML
	private Label statusLabel;
	@FXML
	private ProgressBar progressBar;
	
	private VisobertInitializeTask initializeTask = new VisobertInitializeTask();
	private Visobert sentimentModel;
	
	@FXML
    public void initialize() {
		
		progressBar.progressProperty().bind(initializeTask.progressProperty());
		statusLabel.textProperty().bind(initializeTask.messageProperty());
        
		initializeTask.setOnSucceeded(event -> {
			try {
				sentimentModel = initializeTask.getValue();
			
				Stage currentStage = (Stage) statusLabel.getScene().getWindow();
				currentStage.close();
			
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/humanitarian/logistics/userInterface/problem1/startAnalysis/StartAnalysis.fxml"));
				
				Parent root = loader.load();
				Stage stage = new Stage();
    	
				Scene scene = new Scene(root);
//    			String css = this.getClass().getResource("/com/humanitarian/logistics/userInterface/inputBox/InputInterface.css").toExternalForm();
//    			scene.getStylesheets().add(css);
				stage.setScene(scene);
				stage.setTitle("Humanitarian Logistics Sentiment Analysis Model");
				stage.centerOnScreen();
				stage.show();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	
		initializeTask.setOnFailed(event -> {
			try {
				Stage currentStage = (Stage) statusLabel.getScene().getWindow();
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
		
    	Thread t = new Thread(initializeTask);
    	t.setDaemon(true);
    	t.start();

	}
	
}

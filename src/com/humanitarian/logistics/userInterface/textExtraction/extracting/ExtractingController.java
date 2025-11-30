package com.humanitarian.logistics.userInterface.textExtraction.extracting;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.humanitarian.logistics.userInterface.textExtraction.extractComplete.ExtractCompleteController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class ExtractingController {
	
	@FXML
	private Label statusLabel;
	@FXML
	private ProgressBar progressBar;

	private TextExtractTask extractTask;
	
	public ExtractingController(Path dataPath, List<String> damageType) {
		this.extractTask = new TextExtractTask(dataPath, damageType);
	}
	
	@FXML
	public void initialize() {
		progressBar.progressProperty().bind(extractTask.progressProperty());
		statusLabel.textProperty().bind(extractTask.messageProperty());
		
		extractTask.setOnSucceeded(_ -> {
			try {
				Map<String, Integer> extractResults = extractTask.getValue();
			
				Stage currentStage = (Stage) statusLabel.getScene().getWindow();
				currentStage.close();
			
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/humanitarian/logistics/userInterface/textExtraction/extractComplete/ExtractComplete.fxml"));
				
				loader.setControllerFactory(type -> {
    				if (type == ExtractCompleteController.class) {
    					return new ExtractCompleteController(extractResults);
    				}
    				try {
    					return type.getDeclaredConstructor().newInstance();
    				} catch (Exception except) {
    					throw new RuntimeException(except);
    				}
    			});
				
				Parent root = loader.load();
				Stage stage = new Stage();
    	
				Scene scene = new Scene(root);
//    			String css = this.getClass().getResource("/com/humanitarian/logistics/userInterface/inputBox/InputInterface.css").toExternalForm();
//    			scene.getStylesheets().add(css);
				stage.setScene(scene);
				stage.setTitle("Analyse Results");
				stage.centerOnScreen();
				stage.show();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	
		extractTask.setOnFailed(_ -> {
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

	}
	
	public void extractProcedure() {
		Thread t = new Thread(extractTask);
		t.setDaemon(true);
		t.start();
	}
	
}

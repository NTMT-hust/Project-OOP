package com.humanitarian.logistics.userInterface.sentimentAnalysis.analysing;

import java.io.IOException;
import java.nio.file.Path;

import com.humanitarian.logistics.userInterface.sentimentAnalysis.Model;
import com.humanitarian.logistics.userInterface.sentimentAnalysis.analyseComplete.AnalyseCompleteController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class AnalysingController {

	@FXML
	private ProgressBar progressBar;
	
	@FXML
	private Label statusLabel;
	
	private AnalysingTask analyseTask;
	
	public AnalysingController(Path dataPath, Model sentimentModel) {
		analyseTask = new AnalysingTask(dataPath, sentimentModel);
	}
	
	@FXML
	public void initialize() {
		
		progressBar.progressProperty().bind(analyseTask.progressProperty());
		statusLabel.textProperty().bind(analyseTask.messageProperty());
		
		analyseTask.setOnSucceeded(_ -> {
			try {
				TotalResult analyseResults = analyseTask.getValue();
			
				Stage currentStage = (Stage) statusLabel.getScene().getWindow();
				currentStage.close();
			
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/humanitarian/logistics/userInterface/sentimentAnalysis/analyseComplete/AnalyseComplete.fxml"));
				
				loader.setControllerFactory(type -> {
    				if (type == AnalyseCompleteController.class) {
    					return new AnalyseCompleteController(analyseResults.getString(), analyseResults.getTotalSentiment());
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
	
		analyseTask.setOnFailed(_ -> {
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
	
	public void analyseProcedure() {
		Thread t = new Thread(analyseTask);
		t.setDaemon(true);
		t.start();
	}
}

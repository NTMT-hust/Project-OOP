package com.humanitarian.logistics.userinterface.sentimentanalysis.modelinitialize;

import com.humanitarian.logistics.userinterface.sentimentanalysis.Model;
import com.humanitarian.logistics.userinterface.sentimentanalysis.startanalysis.StartAnalysisController;
import com.humanitarian.logistics.util.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class InitializeController {
	
	@FXML
	private Label statusLabel;
	@FXML
	private ProgressBar progressBar;
	
	private InitializeTask initializeTask = new InitializeTask();
	private Model sentimentModel;
	
	@FXML
    public void initialize() {
		
		progressBar.progressProperty().bind(initializeTask.progressProperty());
		statusLabel.textProperty().bind(initializeTask.messageProperty());
        
		initializeTask.setOnSucceeded(event -> {
			sentimentModel = initializeTask.getValue();

			Stage currentStage = (Stage) statusLabel.getScene().getWindow();
			currentStage.close();
			
			Stage stage = new Stage();
			SceneManager.loadSceneWithParam(stage, "/com/humanitarian/logistics/userInterface/sentimentAnalysis/startAnalysis/StartAnalysis.fxml",
					"Humanitarian Logistics Sentiment Analysis Model", type -> new StartAnalysisController(this.sentimentModel));
		});
	
		initializeTask.setOnFailed(event -> {
			Stage currentStage = (Stage) statusLabel.getScene().getWindow();
			currentStage.close();
			
			Stage stage = new Stage();
			SceneManager.loadScene(stage, "/resources/Error.fxml",
					"Error");
		});
		
    	Thread t = new Thread(initializeTask);
    	t.setDaemon(true);
    	t.start();

	}
	
}

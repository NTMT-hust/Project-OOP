package com.humanitarian.logistics.userinterface.sentimentanalysis.analysing;

import java.nio.file.Path;

import com.humanitarian.logistics.userinterface.sentimentanalysis.Model;
import com.humanitarian.logistics.userinterface.sentimentanalysis.analysecomplete.AnalyseCompleteController;
import com.humanitarian.logistics.util.SceneManager;

import javafx.fxml.FXML;
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
		
		analyseTask.setOnSucceeded(event -> {
			TotalResult analyseResults = analyseTask.getValue();

			Stage currentStage = (Stage) statusLabel.getScene().getWindow();
			currentStage.close();
			
			Stage stage = new Stage();
			SceneManager.loadSceneWithParam(stage, "/com/humanitarian/logistics/userInterface/sentimentAnalysis/analyseComplete/AnalyseComplete.fxml",
					"Analyse Results", type -> new AnalyseCompleteController(analyseResults.getString(), analyseResults.getTotalSentiment()));
		});
	
		analyseTask.setOnFailed(event -> {
			Stage currentStage = (Stage) statusLabel.getScene().getWindow();
			currentStage.close();
			
			Stage stage = new Stage();
			SceneManager.loadScene(stage, "/resources/Error.fxml",
					"Error");
		});

		Thread t = new Thread(analyseTask);
		t.setDaemon(true);
		t.start();
	}

}

package com.humanitarian.logistics.userinterface.textextraction.extracting;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.humanitarian.logistics.userinterface.textextraction.extractcomplete.ExtractCompleteController;
import com.humanitarian.logistics.util.SceneManager;

import javafx.fxml.FXML;
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
		
		extractTask.setOnSucceeded(event -> {
			Map<String, Integer> extractResults = extractTask.getValue();

			Stage currentStage = (Stage) statusLabel.getScene().getWindow();
			currentStage.close();

			Stage stage = new Stage();
			SceneManager.loadSceneWithParam(stage, "/com/humanitarian/logistics/userinterface/textextraction/extractcomplete/ExtractComplete.fxml",
					"Analyse Results", type -> new ExtractCompleteController(extractResults));
		});
	
		extractTask.setOnFailed(event -> {
			Stage currentStage = (Stage) statusLabel.getScene().getWindow();
			currentStage.close();
			
			Stage stage = new Stage();
			SceneManager.loadScene(stage, "/resources/Error.fxml",
					"Error");
		});
		
		Thread t = new Thread(extractTask);
		t.setDaemon(true);
		t.start();

	}
	
}

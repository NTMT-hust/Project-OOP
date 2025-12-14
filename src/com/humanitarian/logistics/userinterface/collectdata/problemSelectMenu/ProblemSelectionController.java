package com.humanitarian.logistics.userinterface.collectdata.problemSelectMenu;

import java.io.IOException;

import com.humanitarian.logistics.util.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ProblemSelectionController {

	@FXML
	private Button button1, button2, buttonCollectData;

	@FXML
	public void collectData(ActionEvent e) throws IOException {
		
		Stage currentStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		currentStage.close();
		
		Stage stage = new Stage();
		SceneManager.loadScene(stage, "/com/humanitarian/logistics/userInterface/collectData/collectorSelectMenu/CollectorSelectionMenu.fxml"
				, "Select Collector");
		
	}

	@FXML
	public void sentimentAnalysis(ActionEvent e) throws IOException {
		
		Stage currentStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		currentStage.close();
		
		Stage stage = new Stage();
		SceneManager.loadScene(stage, "/com/humanitarian/logistics/userInterface/sentimentAnalysis/modelInitialize/ModelInitialize.fxml",
				"Initializing Sentiment Model...");
		
	}

	@FXML
	public void textExtraction(ActionEvent e) throws IOException {
		
		Stage currentStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		currentStage.close();
		
		Stage stage = new Stage();
		SceneManager.loadScene(stage, "/com/humanitarian/logistics/userInterface/textExtraction/startExtraction/StartExtraction.fxml",
				"Initializing Damage Analyse Model...");
		
	}

}

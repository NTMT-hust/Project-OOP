package com.humanitarian.logistics.userinterface.sentimentanalysis.startanalysis;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.humanitarian.logistics.userinterface.sentimentanalysis.Model;
import com.humanitarian.logistics.userinterface.sentimentanalysis.analysing.AnalysingController;
import com.humanitarian.logistics.util.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class StartAnalysisController {
	
	List<String> dataSourceList = List.of("Youtube", "GoogleCSE", "NewsAPI");
	private String selection;
	
	@FXML
	private Label statusLabel;
	
	@FXML
	private MenuButton menuButton = new MenuButton();
	
	@FXML
	private Button button;
	
	private Model sentimentModel;
	
	public StartAnalysisController(Model sentimentModel) {
		this.sentimentModel = sentimentModel;
	}
	
	@FXML
	public void initialize() {
		
		statusLabel.setText("Sentiment Analysis Model");
		menuButton.getItems().clear();
		
		for (String option: dataSourceList) {
			MenuItem item = new MenuItem(option);
			
			item.setOnAction(event -> {
				menuButton.setText(option);
				this.selection = option;
			});
			
			menuButton.getItems().add(item);
		}
	}
	
	@FXML
	public void startAnalysis(ActionEvent e) throws IOException {
		
		Stage currentStage = (Stage) statusLabel.getScene().getWindow();
		currentStage.close();
		
		Path dataPath = Paths.get("data", this.selection + "_posts.json");
		
		Stage stage = new Stage();
		SceneManager.loadSceneWithParam(stage, "/com/humanitarian/logistics/userInterface/sentimentAnalysis/analysing/Analysing.fxml",
				"Analyzing...", type -> new AnalysingController(dataPath, sentimentModel));
		
	}
	
}

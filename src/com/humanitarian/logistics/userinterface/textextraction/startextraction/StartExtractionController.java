package com.humanitarian.logistics.userinterface.textextraction.startextraction;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.humanitarian.logistics.userinterface.textextraction.extracting.ExtractingController;
import com.humanitarian.logistics.util.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class StartExtractionController {

	
	@FXML
	private Label statusLabel;
	
	@FXML
	private MenuButton menuButton = new MenuButton();
	
	@FXML
	private Button button;
	
	@FXML
	private TextField textField;
	
	List<String> dataSourceList = List.of("Youtube", "GoogleCSE", "NewsAPI");
	private String selection;
	
	@FXML
	public void initialize() {
		
		menuButton.getItems().clear();
		
		for (String option: dataSourceList) {
			MenuItem item = new MenuItem(option);
			
			item.setOnAction(_ -> {
				menuButton.setText(option);
				this.selection = option;
			});
			
			menuButton.getItems().add(item);
		}
	}
	
	@FXML
	public void startExtract(ActionEvent e) throws IOException {
		
		Stage currentStage = (Stage) statusLabel.getScene().getWindow();
		currentStage.close();
		
		Path dataPath = Paths.get("data", this.selection + "_posts.json");
		String[] keywords = textField.getText().split(",");
		for (int i = 0; i < keywords.length; i++) {
			keywords[i] = keywords[i].strip();
		}
		
		Stage stage = new Stage();
		SceneManager.loadSceneWithParam(stage, "/com/humanitarian/logistics/userInterface/textExtraction/extracting/Extracting.fxml",
				"Analysing...", type -> new ExtractingController(dataPath, List.of(keywords)));
		
	}
	
}

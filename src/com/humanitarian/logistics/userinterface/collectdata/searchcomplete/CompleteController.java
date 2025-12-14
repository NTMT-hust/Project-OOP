package com.humanitarian.logistics.userinterface.collectdata.searchcomplete;

import java.io.IOException;

import com.humanitarian.logistics.util.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class CompleteController {
	@FXML
	private Button backButton;
	@FXML
	private Label statusLabel;
	
	private String status;
	
	public CompleteController(String status) {
		this.status = status;
	}
	
	@FXML
	public void initialize() {
		this.statusLabel.setText("Results are saved in data/" + status + "_posts.json");
	}

	@FXML
	public void returnToMenu(ActionEvent e) throws IOException {

		Stage currentStage = (Stage) statusLabel.getScene().getWindow();
		currentStage.close();
		
		Stage stage = new Stage();
		SceneManager.loadScene(stage, "/com/humanitarian/logistics/userinterface/collectdata/problemselectmenu/SelectionMenu.fxml",
				"Complete searching!");
	}
}

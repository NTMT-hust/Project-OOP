package com.humanitarian.logistics.userInterface.collectData.searchComplete;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class CompleteController {
	@FXML
	private Button backButton;

	@FXML
	private Label statusLabel;
	
	public void setStatus(String status) {
		statusLabel.setText("Results are saved in data/" + status.toLowerCase() + "_posts.json");
	}

	@FXML
	public void returnToMenu(ActionEvent e) throws IOException {

		Stage currentStage = (Stage) statusLabel.getScene().getWindow();
		currentStage.close();

		FXMLLoader loader = new FXMLLoader(getClass().getResource(
				"/com/humanitarian/logistics/userInterface/collectData/problemSelectMenu/SelectionMenu.fxml"));

		Parent root = loader.load();
		Stage stage = new Stage();

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Complete searching!");
		stage.centerOnScreen();
		stage.show();
	}
}

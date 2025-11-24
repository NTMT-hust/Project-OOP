package com.humanitarian.logistics.collector.youtube;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class InitializeController {
	
	@FXML
	private ProgressBar progressBar;
	
	@FXML
	private Label statusLabel;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML
	public void initialize() {
		return;
	}
}

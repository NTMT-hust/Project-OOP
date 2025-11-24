package com.humanitarian.logistics.collector.youtube;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ErrorController {
	@FXML
	private Label statusLabel;

	public void returnError(String error) {
		statusLabel.setText(error);
	}
}

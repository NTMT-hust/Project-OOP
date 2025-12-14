package com.humanitarian.logistics.userinterface.collectdata.inputbox;

import java.io.IOException;
import java.time.LocalDateTime;

import com.humanitarian.logistics.collector.Collector;
import com.humanitarian.logistics.datastructure.InputData;
import com.humanitarian.logistics.userinterface.collectdata.searching.SearchingController;
import com.humanitarian.logistics.util.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;

public class InputBoxController {
	@FXML
	private TextField userKeyword, userHashtags, userMaxResult, userMaxVideo;
	@FXML
	private Button OK, Cancel;
	@FXML
	private DatePicker userStartDate, userEndDate;
	@FXML
	private AnchorPane scenePane;

	private String keyWord;
	private String[] hashTags;
	private LocalDateTime startDate, endDate;
	private int maxResult;

	private String collectorType;

	public InputBoxController(Collector<?, ?, ?> collector, String collectorType) {
		this.collectorType = collectorType;
	}

	@FXML
	public void submit(ActionEvent event) throws IOException {
		keyWord = userKeyword.getText();
		hashTags = userKeyword.getText().split(",");
		startDate = userStartDate.getValue().atStartOfDay();
		endDate = userEndDate.getValue().atTime(23, 59, 59);
		maxResult = Integer.parseInt(userMaxResult.getText());

		InputData userInput = new InputData(keyWord, hashTags, startDate, endDate, maxResult);
		Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		currentStage.close();
		
		Stage stage = new Stage();
		SceneManager.loadSceneWithParam(stage, "/com/humanitarian/logistics/userInterface/collectData/searching/SearchingInterface.fxml",
				"Searching...", type -> new SearchingController(collectorType, userInput, stage));
		
	}

	@FXML
	public void cancel(ActionEvent event) throws IOException {
		
		Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		currentStage.close();
		
		Stage stage = new Stage();
		SceneManager.loadScene(stage, "/com/humanitarian/logistics/userInterface/collectData/cancelling/CancellingInterface.fxml",
				"Cancelling...");
		
	}
}

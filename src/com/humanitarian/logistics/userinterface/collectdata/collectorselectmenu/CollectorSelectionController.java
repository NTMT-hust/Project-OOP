package com.humanitarian.logistics.userinterface.collectdata.collectorselectmenu;

import java.io.IOException;
import java.util.List;

import com.humanitarian.logistics.userinterface.collectdata.intializecollector.InitializeController;
import com.humanitarian.logistics.util.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class CollectorSelectionController {

	List<String> collectorList = List.of("Youtube", "GoogleCSE", "NewsAPI");
	private String selection;

	@FXML
	private MenuButton menuSelection = new MenuButton("             ");

	@FXML
	private Button startButton;

	@FXML
	public void initialize() {
		menuSelection.getItems().clear();

		for (String option : collectorList) {
			MenuItem item = new MenuItem(option);

			item.setOnAction(event -> {
				menuSelection.setText(option);
				this.selection = option;
			});

			menuSelection.getItems().add(item);
		}
	}

	@FXML
	public void startSearching(ActionEvent e) throws IOException {
		
		Stage currentStage = (Stage) menuSelection.getScene().getWindow();
		SceneManager.loadSceneWithParam(currentStage, "/com/humanitarian/logistics/userInterface/collectData/intializeCollector/InitializeCollector.fxml",
				"Initializing Collector...", type -> {
					try {
						return new InitializeController(this.selection);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					return type;
				});

	}
}

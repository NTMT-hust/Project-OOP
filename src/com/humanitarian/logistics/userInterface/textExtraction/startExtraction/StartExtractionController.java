package com.humanitarian.logistics.userInterface.textExtraction.startExtraction;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.humanitarian.logistics.userInterface.textExtraction.extracting.ExtractingController;

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
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/humanitarian/logistics/userInterface/textExtraction/extracting/Extracting.fxml"));
		
		loader.setControllerFactory(type -> {
			if (type == ExtractingController.class) {
				return new ExtractingController(dataPath, List.of(keywords));
			}
			try {
				return type.getDeclaredConstructor().newInstance();
			} catch (Exception except) {
				throw new RuntimeException(except);
			}
		});
		
		Parent root = loader.load();
		ExtractingController extractController = loader.getController();
		
		Stage stage = new Stage();

		Scene scene = new Scene(root);
//		String css = this.getClass().getResource("/com/humanitarian/logistics/userInterface/inputBox/InputInterface.css").toExternalForm();
//		scene.getStylesheets().add(css);
		stage.setScene(scene);
		stage.setTitle("Analyzing...");
		stage.centerOnScreen();
		stage.show();
		
		extractController.extractProcedure();
	}
	
}

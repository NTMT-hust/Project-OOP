package com.humanitarian.logistics.userInterface.sentimentAnalysis.startAnalysis;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.humanitarian.logistics.userInterface.sentimentAnalysis.Visobert;
import com.humanitarian.logistics.userInterface.sentimentAnalysis.analysing.AnalysingController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class StartAnalysisController {
	
	List<String> dataSourceList = List.of("Youtube", "GoogleCSE", "NewsAPI", "All");
	private String selection;
	
	@FXML
	private Label statusLabel;
	
	@FXML
	private MenuButton menuButton = new MenuButton();
	
	@FXML
	private Button button;
	
	private Visobert sentimentModel;
	
	public StartAnalysisController(Visobert sentimentModel) {
		this.sentimentModel = sentimentModel;
	}
	
	@FXML
	public void initialize() {
		
		statusLabel.setText("Sentiment Analysis Model");
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
	public void startAnalysis(ActionEvent e) throws IOException {
		
		Stage currentStage = (Stage) statusLabel.getScene().getWindow();
		currentStage.close();
		
		Path dataPath = Paths.get("data", this.selection + "_posts.json");
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/humanitarian/logistics/userInterface/sentimentAnalysis/analysing/Analysing.fxml"));
		
		loader.setControllerFactory(type -> {
			if (type == AnalysingController.class) {
				return new AnalysingController(dataPath, sentimentModel);
			}
			try {
				return type.getDeclaredConstructor().newInstance();
			} catch (Exception except) {
				throw new RuntimeException(except);
			}
		});
		
		Parent root = loader.load();
		AnalysingController analyseController = loader.getController();
		
		Stage stage = new Stage();

		Scene scene = new Scene(root);
//		String css = this.getClass().getResource("/com/humanitarian/logistics/userInterface/inputBox/InputInterface.css").toExternalForm();
//		scene.getStylesheets().add(css);
		stage.setScene(scene);
		stage.setTitle("Analyzing...");
		stage.centerOnScreen();
		stage.show();
		
		analyseController.analyseProcedure();
	}
	
}

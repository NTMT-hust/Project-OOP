package com.humanitarian.logistics.userinterface.collectdata.intializeCollector;

import java.io.IOException;
import java.util.List;

import com.humanitarian.logistics.collector.*;
import com.humanitarian.logistics.config.*;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;
import com.humanitarian.logistics.userinterface.collectdata.inputBox.InputBoxController;
import com.humanitarian.logistics.util.SceneManager;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class InitializeController {

	@FXML
	private ProgressBar progressBar = new ProgressBar();

	@FXML
	private Label statusLabel = new Label();

	@FXML
	private VBox rootPane;
	
	private AppConfig appConfig;
	private Collector<SearchCriteria, ?, List<SocialPost>> collector;
	private String collectorType;

	public InitializeController(String collectorName) throws IOException {

		this.collectorType = collectorName;
		this.appConfig = new AppConfig(collectorName);
		switch (collectorName) {
			case "Youtube":
				YouTubeConfig youtubeConfig = new YouTubeConfig(appConfig);
				collector = new YouTubeCollector(youtubeConfig);
				break;

			case "GoogleCSE":
				GoogleCseConfig googleCseConfig = new GoogleCseConfig(appConfig);
				collector = new GoogleCseCollector(googleCseConfig);
				break;

			case "NewsAPI":
				NewsApiConfig newsApiConfig = new NewsApiConfig(appConfig);
				collector = new NewsCollector(newsApiConfig);
				break;
		}
	}

	@FXML
	public void initialize() throws IOException {

		statusLabel.setText("Initializing " + this.collectorType + " Collector...");
		KeyValue endValue;

		if (collector.testConnection()) {
			// 1. Define the END value we want (Progress = 1.0)
			endValue = new KeyValue(progressBar.progressProperty(), 1.0);
		} else {
			endValue = new KeyValue(progressBar.progressProperty(), 0.8);
		}
		// 2. Create a KeyFrame that says "Reach the endValue at 0.5 seconds"
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), endValue);

		// 3. Create the Timeline with that frame
		Timeline timeline = new Timeline(keyFrame);

		// 4. Cleanup when done
		timeline.setOnFinished(e -> {
			Stage currentStage = (Stage) rootPane.getScene().getWindow();
			currentStage.close();
			Stage stage = new Stage();

			if (collector.testConnection()) {
				SceneManager.loadSceneWithParam(stage, "/com/humanitarian/logistics/userInterface/collectData/inputBox/InputInterface.fxml",
						"Input request...", type -> new InputBoxController(this.collector, this.collectorType));
				
			} else {
				SceneManager.loadScene(stage, "/resources/Error.fxml", "Error");
			}
		});

		timeline.play();
	}
}

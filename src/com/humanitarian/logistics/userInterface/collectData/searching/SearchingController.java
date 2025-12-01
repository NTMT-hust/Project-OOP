package com.humanitarian.logistics.userInterface.collectData.searching;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.humanitarian.logistics.collector.Collector;
import com.humanitarian.logistics.collector.SingleCollectorTask;
import com.humanitarian.logistics.collector.GoogleCseCollector;
import com.humanitarian.logistics.collector.NewsCollector;
import com.humanitarian.logistics.collector.YouTubeCollector;
import com.humanitarian.logistics.config.AppConfig;
import com.humanitarian.logistics.config.GoogleCseConfig;
import com.humanitarian.logistics.config.NewsApiConfig;
import com.humanitarian.logistics.config.YouTubeConfig;
import com.humanitarian.logistics.dataStructure.InputData;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;
import com.humanitarian.logistics.userInterface.collectData.searchComplete.CompleteController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SearchingController {

	@FXML
	private ProgressBar progressBar;
	@FXML
	private VBox scenePane;
	@FXML
	private Label statusLabel;

	private String apiName;
	private Collector<SearchCriteria, ?, List<SocialPost>> collector;

	public SearchingController(String apiName) {
		this.apiName = apiName;
		setControllerData(apiName);
	}

	public void setControllerData(String apiName) {
		AppConfig appConfig = new AppConfig(apiName.toLowerCase());

		switch (apiName) {
			case "Youtube":
				YouTubeConfig ytConfig = new YouTubeConfig(appConfig);
				this.collector = new YouTubeCollector(ytConfig);
				break;
			case "GoogleCSE":
				GoogleCseConfig googleConfig = new GoogleCseConfig(appConfig);
				this.collector = new GoogleCseCollector(googleConfig);
				break;
			case "NewsA":
				NewsApiConfig newsConfig = new NewsApiConfig(appConfig);
				this.collector = new NewsCollector(newsConfig);
				break;
			default:
				statusLabel.setText("Error: Unknown Collector " + apiName);
		}
	}

	public void searchProcedure(InputData inputData) {
		if (this.collector == null) {
			statusLabel.setText("Error: Collector not initialized!");
			return;
		}

		SearchCriteria searchCriteria = new SearchCriteria.Builder()
				.keyword(inputData.getKeyWord())
				.hashtags(inputData.getHashTags())
				.dateRange(inputData.getStartDate(), inputData.getEndDate())
				.language("vi")
				.maxResults(inputData.getMaxResult())
				.maxVideos(inputData.getMaxVideo())
				.build();

		SingleCollectorTask collectTask = new SingleCollectorTask(this.collector, searchCriteria, apiName);

		progressBar.progressProperty().bind(collectTask.progressProperty());
		statusLabel.textProperty().bind(collectTask.messageProperty());

		collectTask.setOnSucceeded(event -> {
			List<SocialPost> resultPost = collectTask.getValue();
			savePost(resultPost);
			loadNextScene("/com/humanitarian/logistics/userInterface/collectData/searchComplete/SearchComplete.fxml",
					"Complete!");
		});

		collectTask.setOnFailed(event -> {
			Throwable e = collectTask.getException();
			e.printStackTrace();
			loadNextScene("/resources/Error.fxml", "Error");
		});

		Thread t = new Thread(collectTask);
		t.setDaemon(true);
		t.start();
	}

	private void savePost(List<SocialPost> newPosts) {
		if (newPosts == null || newPosts.isEmpty()) {
			System.out.println("null posts");
			return;
		}

		String fileName = "data/" + apiName.toLowerCase() + "_posts.json";
		File file = new File(fileName);

		if (file.getParentFile() != null)
			file.getParentFile().mkdirs();

		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDateTime.class,
						(JsonSerializer<LocalDateTime>) (src, type,
								ctx) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
				.registerTypeAdapter(LocalDateTime.class,
						(JsonDeserializer<LocalDateTime>) (json, type, ctx) -> LocalDateTime.parse(json.getAsString(),
								DateTimeFormatter.ISO_LOCAL_DATE_TIME))
				.setPrettyPrinting()
				.create();

		List<SocialPost> allPosts = new ArrayList<>();

		if (file.exists() && file.length() > 0) {
			try (FileReader reader = new FileReader(file)) {
				Type listType = new TypeToken<List<SocialPost>>() {
				}.getType();
				List<SocialPost> existing = gson.fromJson(reader, listType);
				if (existing != null)
					allPosts.addAll(existing);
			} catch (Exception e) {
				System.err.println("Warning: Could not read existing file. Overwriting.");
			}
		}

		allPosts.addAll(newPosts);

		try (FileWriter writer = new FileWriter(file)) {
			gson.toJson(allPosts, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadNextScene(String fxmlPath, String title) {
		try {
			// DEBUG CHECK: Does the file exist?
			if (getClass().getResource(fxmlPath) == null) {
				System.err.println("❌ FATAL ERROR: Could not find FXML file at: " + fxmlPath);
				System.err.println("   Please check your folder structure!");
				return; // Stop here instead of crashing
			}

			// Load new window
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			Parent root = loader.load();
			CompleteController controller = loader.getController();

			// 2. Pass the data MANUALLY now
			if (controller != null) {
				controller.setStatus(this.apiName);
			}

			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle(title);
			stage.centerOnScreen();
			stage.show();

			// Close current window
			Stage currentStage = (Stage) scenePane.getScene().getWindow();
			currentStage.close();

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error loading scene: " + e.getMessage());
		}
	}
}
package com.humanitarian.logistics.userinterface.collectdata.searching;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import com.humanitarian.logistics.collector.Collector;
import com.humanitarian.logistics.collector.SingleCollectorTask;
import com.humanitarian.logistics.collector.GoogleCseCollector;
import com.humanitarian.logistics.collector.NewsCollector;
import com.humanitarian.logistics.collector.YouTubeCollector;
import com.humanitarian.logistics.config.AppConfig;
import com.humanitarian.logistics.config.GoogleCseConfig;
import com.humanitarian.logistics.config.NewsApiConfig;
import com.humanitarian.logistics.config.YouTubeConfig;
import com.humanitarian.logistics.datastructure.InputData;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;
import com.humanitarian.logistics.userinterface.collectdata.searchcomplete.CompleteController;
import com.humanitarian.logistics.util.SceneManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SearchingController {

	@FXML
	private ProgressBar progressBar = new ProgressBar();
	@FXML
	private VBox scenePane = new VBox();
	@FXML
	private Label statusLabel = new Label();

	private String apiName;
	private Collector<SearchCriteria, ?, List<SocialPost>> collector;
	private SearchCriteria searchCriteria;
	private Stage currentStage;

	public SearchingController(String apiName, InputData inputData, Stage currentStage) {
		this.apiName = apiName;
		this.searchCriteria = new SearchCriteria.Builder()
				.keyword(inputData.getKeyWord())
				.hashtags(inputData.getHashTags())
				.dateRange(inputData.getStartDate(), inputData.getEndDate())
				.language("vi")
				.maxResults(inputData.getMaxResult())
				.maxVideos(inputData.getMaxVideo())
				.build();
		this.currentStage = currentStage;
		
		setControllerData(apiName);
	}

	public void setControllerData(String apiName) {
		AppConfig appConfig = new AppConfig(apiName);

		switch (apiName) {
			case "Youtube":
				YouTubeConfig ytConfig = new YouTubeConfig(appConfig);
				this.collector = new YouTubeCollector(ytConfig);
				break;
			case "GoogleCSE":
				GoogleCseConfig googleConfig = new GoogleCseConfig(appConfig);
				this.collector = new GoogleCseCollector(googleConfig);
				break;
			case "NewsAPI":
				NewsApiConfig newsConfig = new NewsApiConfig(appConfig);
				this.collector = new NewsCollector(newsConfig);
				break;
			default:
				statusLabel.setText("Error: Unknown Collector " + apiName);
		}
	}

	@FXML
	public void initialize() {
		if (this.collector == null) {
			statusLabel.setText("Error: Collector not initialized!");
			return;
		}

		SingleCollectorTask collectTask = new SingleCollectorTask(this.collector, searchCriteria, apiName);
		progressBar.progressProperty().bind(collectTask.progressProperty());
		statusLabel.textProperty().bind(collectTask.messageProperty());
			
		Stage stage = new Stage();
			
		collectTask.setOnSucceeded(event -> {
			List<SocialPost> resultPost = collectTask.getValue();
			savePost(resultPost);
			SceneManager.loadSceneWithParam(stage, "/com/humanitarian/logistics/userInterface/collectData/searchComplete/SearchComplete.fxml",
					"Complete!", type -> new CompleteController(apiName));
			this.currentStage.close();
		});
		collectTask.setOnFailed(event -> {
			SceneManager.loadScene(stage, "/resources/Error.fxml",
					"Error!");
		});

		Thread t = new Thread(collectTask);
		t.setDaemon(true);
		t.start();
		
	}

	private void savePost(List<SocialPost> newPosts) {

		String fileName = "data/" + apiName + "_posts.json";
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
		allPosts.addAll(newPosts);

		try (FileWriter writer = new FileWriter(file)) {
			gson.toJson(allPosts, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
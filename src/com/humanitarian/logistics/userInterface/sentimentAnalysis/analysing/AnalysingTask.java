package com.humanitarian.logistics.userInterface.sentimentAnalysis.analysing;

import java.io.BufferedReader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.humanitarian.logistics.userInterface.sentimentAnalysis.Model;

import ai.djl.modality.Classifications;
import javafx.concurrent.Task;

class TotalResult {
	StringBuffer buffer;
	List<Integer> numbers;

	TotalResult(StringBuffer buffer, List<Integer> numbers) {
		this.buffer = buffer;
		this.numbers = numbers;
	}

	public String getString() {
		return this.buffer.toString();
	}

	public List<Integer> getTotalSentiment() {
		return this.numbers;
	}
}

public class AnalysingTask extends Task<TotalResult> {

	private Path dataPath;
	private StringBuffer results = new StringBuffer();
	private Model sentimentModel;

	private List<Integer> sentiment;

	public AnalysingTask(Path dataPath, Model sentimentModel) {
		this.dataPath = dataPath;
		this.sentimentModel = sentimentModel;
	}

	@Override
	protected TotalResult call() throws Exception {
		// TODO Auto-generated method stub

		int positive = 0, negative = 0, neutral = 0;

		updateMessage("Analysing data from: " + dataPath.toAbsolutePath());

		List<Map<String, String>> data = loadDataFromFile(dataPath);

		for (Map<String, String> item : data) {
			String text = item.get("content");
			if (text == null)
				continue;

			Classifications result = sentimentModel.predict(text);

			results.append("Text: " + text + "\n");
			results.append("Prediction: " + result.best().getClassName() + "\n");
			results.append("Confidence: " + String.format("%.4f", result.best().getProbability()) + "\n");
			results.append("-----------------------\n");

			switch (result.best().getClassName()) {
				case "Positive":
					positive += 1;
					break;
					
				case "Negative":
					negative += 1;
					break;
					
				case "Neutral":
					neutral += 1;
					break;
			}
		}

		sentiment = new ArrayList<>(List.of(positive, negative, neutral));
		TotalResult finalResult = new TotalResult(results, sentiment);

		return finalResult;
	}

	private static List<Map<String, String>> loadDataFromFile(Path path) throws Exception {
		if (!Files.exists(path)) {
			throw new RuntimeException("File not found at: " + path.toAbsolutePath());
		}

		Gson gson = new Gson();

		// Use Files.newBufferedReader for efficient file reading
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			return gson.fromJson(reader, new TypeToken<List<Map<String, Object>>>() {
			}.getType());
		}
	}
}

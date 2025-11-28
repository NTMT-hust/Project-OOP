package com.humanitarian.logistics.userInterface.problem1;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.humanitarian.logistics.sentimentAnalysis.Visobert;

import ai.djl.modality.Classifications;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class VisobertController {

    public static void main(String[] args) {
        // Update these paths to where your actual files are on your disk
        Path modelPath = Paths.get("src/resources/visobert/model.onnx");
        Path tokenizerPath = Paths.get("src/resources/visobert/tokenizer.json");
        Path dataPath = Paths.get("data", "GoogleCSE_posts.json");

        // Use try-with-resources to ensure the model closes automatically
        try (Visobert analyzer = new Visobert(modelPath, tokenizerPath)) {
            
        	System.out.println("Model loaded. Reading data from: " + dataPath.toAbsolutePath());
        	
        	List<Map<String, String>> data = loadDataFromFile(dataPath);
        	
        	for (Map<String, String> item : data) {
                String text = item.get("content");
                if (text == null) continue;

                Classifications result = analyzer.predict(text);

                System.out.println("Text: " + text);
                System.out.println("Prediction: " + result.best().getClassName());
                System.out.println("Confidence: " + String.format("%.4f", result.best().getProbability()));
                System.out.println("-----------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to keep main clean
    private static List<Map<String, String>> loadDataFromFile(Path path) throws Exception {
        if (!Files.exists(path)) {
            throw new RuntimeException("File not found at: " + path.toAbsolutePath());
        }

        Gson gson = new Gson();
        
        // Use Files.newBufferedReader for efficient file reading
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
        	return gson.fromJson(reader, new TypeToken<List<Map<String, Object>>>(){}.getType());
        }
    }
}
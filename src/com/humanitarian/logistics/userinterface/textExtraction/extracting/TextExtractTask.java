package com.humanitarian.logistics.userinterface.textExtraction.extracting;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.concurrent.Task;

public class TextExtractTask extends Task<Map<String, Integer>> {

	private Path dataPath;
	private List<String> keywords;

	public TextExtractTask(Path dataPath, List<String> keywords) {
		this.dataPath = dataPath;
		this.keywords = keywords;
	}
	
	@Override
	protected Map<String, Integer> call() throws Exception {

        // INITIALIZE COUNTER
        Map<String, Integer> extractResults = new LinkedHashMap<>();
        for (String kw : keywords) extractResults.put(kw, 0);

        updateMessage("Processing " + dataPath.toString() + " files...");

        try {
            processFileEntries(dataPath, keywords, extractResults);
            
            // 3. PRINT RESULTS
            updateMessage("Analysis of file: " + dataPath);
            updateMessage("---------------------------------");
            for (Map.Entry<String, Integer> entry : extractResults.entrySet()) {
            	updateMessage("Keyword '" + entry.getKey() + "' found in " + entry.getValue() + " entries.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

		return extractResults;
	}

	private void processFileEntries(Path filePath, List<String> keywords, Map<String, Integer> counts) throws Exception {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            JsonElement root = JsonParser.parseReader(reader);

            if (!root.isJsonArray()) {
            	updateMessage("Error: Root of file is not an Array []. Cannot count entries.");
                return;
            }

            JsonArray entries = root.getAsJsonArray();
            updateMessage("Total Entries (Objects) in file: " + entries.size());

            // --- LOOP THROUGH EACH ENTRY {...} ---
            for (JsonElement entry : entries) {
                
                // For this specific entry, check which keywords appear
                Set<String> keywordsInThisEntry = new HashSet<>();
                scanRecursively(entry, keywords, keywordsInThisEntry);

                // Update the main counters
                for (String foundKw : keywordsInThisEntry) {
                    counts.put(foundKw, counts.get(foundKw) + 1);
                }
            }
        }
    }

    /**
     * Recursively looks for keywords inside a specific JSON Element (Entry).
     * Adds found keywords to the 'foundSet'.
     */
    private void scanRecursively(JsonElement element, List<String> targets, Set<String> foundSet) {
        if (element.isJsonNull()) return;

        if (element.isJsonPrimitive()) {
            // Check the value string
            String text = element.getAsString().toLowerCase();
            for (String target : targets) {
                if (text.contains(target.toLowerCase())) {
                    foundSet.add(target);
                }
            }
        } 
        else if (element.isJsonArray()) {
            for (JsonElement item : element.getAsJsonArray()) {
                scanRecursively(item, targets, foundSet);
            }
        } 
        else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                // Optional: Check Key Name if you want (e.g. "error_message": ...)
                // checkText(entry.getKey(), targets, foundSet); 
                
                // Check Value
                scanRecursively(entry.getValue(), targets, foundSet);
            }
        }
    }
}
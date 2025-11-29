package com.humanitarian.logistics.userInterface.sentimentAnalysis.modelInitialize;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.humanitarian.logistics.userInterface.sentimentAnalysis.Visobert;

import javafx.concurrent.Task;

public class VisobertInitializeTask extends Task<Visobert> {
	
    Path modelPath = Paths.get("src/resources/visobert/model.onnx");
    Path tokenizerPath = Paths.get("src/resources/visobert/tokenizer.json");
	
    @Override
	protected Visobert call() {
		// TODO Auto-generated method stub
    	Visobert analyzer;
		try {
			updateMessage("Initializing...");
			analyzer = new Visobert(modelPath, tokenizerPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			analyzer = null;
		}
		return analyzer;
	}

}

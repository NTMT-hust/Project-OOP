package com.humanitarian.logistics.userInterface.sentimentAnalysis.modelInitialize;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.humanitarian.logistics.userInterface.sentimentAnalysis.Model;

import javafx.concurrent.Task;

public class InitializeTask extends Task<Model> {
	
    Path modelPath = Paths.get("src/resources/visobert/model.onnx");
    Path tokenizerPath = Paths.get("src/resources/visobert/tokenizer.json");
	
    @Override
	protected Model call() {
		// TODO Auto-generated method stub
    	Model analyzer;
		try {
			updateMessage("Initializing...");
			analyzer = new Model(modelPath, tokenizerPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			analyzer = null;
		}
		return analyzer;
	}

}

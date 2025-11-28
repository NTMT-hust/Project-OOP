package com.humanitarian.logistics.userInterface.problem1.modelInitialize;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.humanitarian.logistics.sentimentAnalysis.Visobert;

import javafx.concurrent.Task;

public class VisobertInitializeTask extends Task<Visobert> {
	
    Path modelPath = Paths.get("src/resources/visobert/model.onnx");
    Path tokenizerPath = Paths.get("src/resources/visobert/tokenizer.json");
	
    @Override
	protected Visobert call() {
		// TODO Auto-generated method stub
    	Visobert analyzer;
		try {
			analyzer = new Visobert(modelPath, tokenizerPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			analyzer = null;
		}
		return analyzer;
	}

}

package com.humanitarian.logistics.userInterface.collectData.cancelling;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

public class CancellingController {

	@FXML
	private ProgressBar progressBar;
	
	@FXML
	public void initialize() {
		simulateCleanupProcess();
	}
	
	private void simulateCleanupProcess() {
		// 1. Define the END value we want (Progress = 1.0)
        KeyValue endValue = new KeyValue(progressBar.progressProperty(), 1.0);

        // 2. Create a KeyFrame that says "Reach the endValue at 0.5 seconds"
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.25), endValue);

        // 3. Create the Timeline with that frame
        Timeline timeline = new Timeline(keyFrame);
        	
        // 4. Cleanup when done
        timeline.setOnFinished(e -> {
            Platform.exit(); 
            System.exit(0);
        });

        timeline.play();
    }
}
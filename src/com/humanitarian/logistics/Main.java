package com.humanitarian.logistics;

import java.io.IOException;

import com.humanitarian.logistics.util.SceneManager;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
    	SceneManager.loadScene(primaryStage, "/com/humanitarian/logistics/userInterface/collectData/problemSelectMenu/SelectionMenu.fxml",
    			"Humanitarian Logistics Project");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

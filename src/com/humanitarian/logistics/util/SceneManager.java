package com.humanitarian.logistics.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public final class SceneManager {

    // Prevent instantiation
    private SceneManager() {}

    /**
     * Load a new scene from FXML and set it on the given stage.
     *
     * @param stage     target stage
     * @param fxmlPath  path to FXML (relative to resources)
     * @param title     window title
     */
    public static void loadScene(Stage stage, String fxmlPath, String title) {
        try {
        	FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }
    
    public static void loadSceneWithParam(Stage stage, String fxmlPath, String title, Callback<Class<?>, Object> controllerFactory) {
    	try {
    		FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
    		loader.setControllerFactory(controllerFactory);
    		
    		Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }
}

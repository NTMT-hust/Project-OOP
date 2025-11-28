package com.humanitarian.logistics.userInterface.collectData.problemSelectMenu;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ProblemSelectionController {
	
	@FXML
	private Button buttonProblem1, buttonProblem2, buttonProblem3, buttonCollectData;
	
    private Stage stage;
    private Scene scene;
    private Parent root;

	@FXML
	public void collectData(ActionEvent e) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/humanitarian/logistics/userInterface/collectData/collectorSelectMenu/CollectorSelectionMenu.fxml"));
        root = loader.load();
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        
        scene = new Scene(root);
//        String css = this.getClass().getResource("/resources/InputInterface.css").toExternalForm();
//        scene.getStylesheets().add(css);
    	stage.setScene(scene);
    	
    	stage.setTitle("Select Collector");
    	stage.centerOnScreen();
    	stage.show();
	}
	
	@FXML
	public void problem1(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/humanitarian/logistics/userInterface/problem1/modelInitialize/ModelInitialize.fxml"));
        root = loader.load();
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        
        scene = new Scene(root);
//        String css = this.getClass().getResource("/resources/InputInterface.css").toExternalForm();
//        scene.getStylesheets().add(css);
    	stage.setScene(scene);
    	
    	stage.setTitle("Initializing Sentiment Model...");
    	stage.centerOnScreen();
    	stage.show();
	}
	
	@FXML
	public void problem2(ActionEvent e) {
		return;
	}
	
	@FXML
	public void problem3(ActionEvent e) {
		return;
	}
}

package com.humanitarian.logistics;

import java.io.IOException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class CollectorSelectionController {
	
	List<String> collectorList = List.of("Youtube", "GoogleCSE", "NewsAPI");
	private String selection;
	
	@FXML
	private MenuButton menuSelection = new MenuButton("             ");
	
	@FXML
	private Button startButton;
	
    private Stage stage;
    private Scene scene;
    private Parent root;
	
    @FXML
	public void initialize() {
    	menuSelection.getItems().clear();
    	
		for (String option: collectorList) {
			MenuItem item = new MenuItem(option);
			
			item.setOnAction(event -> {
				menuSelection.setText(option);
				this.selection = option;
			});
			
			menuSelection.getItems().add(item);
		}
	}
	
	@FXML
	public void startSearching(ActionEvent e) throws IOException {
		switch (selection) {
			case "Youtube":
	    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/youtube/InputInterface.fxml"));
	    		root = loader.load();
	    		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
	        
	    		scene = new Scene(root);
//	        	String css = this.getClass().getResource("/resources/youtube/InputInterface.css").toExternalForm();
//	        	scene.getStylesheets().add(css);
	    		stage.setScene(scene);
	    	
	    		stage.setTitle("Input Search Criteria");
	    		stage.centerOnScreen();
	    		stage.show();
	    	
			case "GoogleCSE":
				return;
			
			case "NewsAPI":
				return;
		}
	}
}

package com.humanitarian.logistics.userInterface.collectData.collectorSelectMenu;

import java.io.IOException;
import java.util.List;

import com.humanitarian.logistics.userInterface.collectData.intializeCollector.InitializeController;

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
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/humanitarian/logistics/userInterface/collectData/intializeCollector/InitializeCollector.fxml"));
		
		root = loader.load();
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		
		InitializeController init = loader.getController();
		init.initializeCollector(this.selection);
    
		scene = new Scene(root);
//    	String css = this.getClass().getResource("/resources/InputInterface.css").toExternalForm();
//    	scene.getStylesheets().add(css);
		stage.setScene(scene);
		stage.setTitle("Initializing Collector...");
		stage.centerOnScreen();
		stage.show();
	}
}

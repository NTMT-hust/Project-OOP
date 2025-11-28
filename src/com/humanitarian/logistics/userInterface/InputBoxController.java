package com.humanitarian.logistics.userInterface;

import java.io.IOException;
import java.time.LocalDateTime;

import com.humanitarian.logistics.collector.Collector;
import com.humanitarian.logistics.dataStructure.InputData;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;

public class InputBoxController {
    @FXML
    private TextField userKeyword, userHashtags, userMaxResult, userMaxVideo;
    @FXML
    private Button OK, Cancel;
    @FXML
    private DatePicker userStartDate, userEndDate;
    @FXML
    private AnchorPane scenePane;
    
    private String keyWord;
    private String[] hashTags;
    private LocalDateTime startDate, endDate;
    private int maxResult;
    
    private Stage stage;
    private Scene scene;
    private Parent root;
    
    private Collector collector;
    private String collectorType;
    
    public InputBoxController(Collector collector, String collectorType) {
    	this.collector = collector;
    	this.collectorType = collectorType;
    }
    
    @FXML
    public void submit(ActionEvent event) throws IOException {
    	keyWord = userKeyword.getText();
    	hashTags = userKeyword.getText().split(",");
    	startDate = userStartDate.getValue().atStartOfDay();
    	endDate = userEndDate.getValue().atTime(23, 59, 59);
    	maxResult = Integer.parseInt(userMaxResult.getText());
    	
    	InputData userInput = new InputData(keyWord, hashTags, startDate, endDate, maxResult);
    	
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/SearchingInterface.fxml"));
    	
    	loader.setControllerFactory(type -> {
    		if (type == SearchingController.class) {
    			return new SearchingController(collector, collectorType);
    		}
    		
    		try {
    			return type.getDeclaredConstructor().newInstance();
    		} catch (Exception e) {
    			throw new RuntimeException(e);
    		}
    	});
    	
    	root = loader.load();
    	SearchingController searchController = loader.getController();
    	
    	stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    	scene = new Scene(root);
    	stage.setScene(scene);
    	stage.setTitle("Searching data...");
    	
    	stage.centerOnScreen();
    	stage.show();
    	
    	searchController.searchProcedure(userInput);
    }
    
    @FXML
    public void cancel(ActionEvent e) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/CancellingInterface.fxml"));
    	root = loader.load();
    	stage = (Stage)((Node)e.getSource()).getScene().getWindow();
    	scene = new Scene(root);
    	stage.setScene(scene);
    	
    	stage.centerOnScreen();
    	stage.show();
    }
}

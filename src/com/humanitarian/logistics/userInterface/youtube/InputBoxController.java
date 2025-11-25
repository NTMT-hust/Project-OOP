package com.humanitarian.logistics.userInterface.youtube;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import com.humanitarian.logistics.dataStructure.InputData;

import javafx.animation.PauseTransition;
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
import javafx.util.Duration;
import javafx.scene.control.DatePicker;

public class InputBoxController {
    @FXML
    private TextField userKeyword, userHashtags, userMaxResult;
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
    
    @FXML
    public void submit(ActionEvent event) throws IOException {
    	keyWord = userKeyword.getText();
    	hashTags = userKeyword.getText().split(",");
    	startDate = userStartDate.getValue().atStartOfDay();
    	endDate = userEndDate.getValue().atTime(23, 59, 59);
    	maxResult = Integer.parseInt(userMaxResult.getText());
    	
    	InputData userInput = new InputData(keyWord, hashTags, startDate, endDate, maxResult);
    	
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/youtube/SearchingInterface.fxml"));
    	root = loader.load();
    	
    	SearchingController searchController = loader.getController();
    	
    	stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    	scene = new Scene(root);
    	stage.setScene(scene);
    	stage.setTitle("Searching data...");
    	
    	stage.centerOnScreen();
    	stage.show();
    
    	try {
    		searchController.searchProcedure(userInput);
    	} catch (Exception e) {
    		stage.close();
    		try {
    			FXMLLoader loaderError = new FXMLLoader(getClass().getResource("/resources/youtube/Error.fxml"));
    			Parent rootError = loaderError.load();
    			Stage stageError = new Stage();
            	
    			Scene sceneError = new Scene(rootError);
//	        	String css = this.getClass().getResource("/resources/youtube/InputInterface.css").toExternalForm();
//	        	scene.getStylesheets().add(css);
    			stageError.setScene(sceneError);
    			stageError.setTitle("Error");
    			stageError.centerOnScreen();
    			stageError.show();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    		e.printStackTrace();
    	}
    }
    
    @FXML
    public void cancel(ActionEvent e) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/youtube/CancellingInterface.fxml"));
    	root = loader.load();
    	stage = (Stage)((Node)e.getSource()).getScene().getWindow();
    	scene = new Scene(root);
    	stage.setScene(scene);
    	
    	stage.centerOnScreen();
    	stage.show();
    }
}

package com.humanitarian.logistics.userInterface;

import java.io.IOException;
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
    
    public interface SubmitListener {
    	void onSubmit(InputData data);
    }
    private SubmitListener submitListener;
    
    public void setOnSubmit(SubmitListener listener) {
    	this.submitListener = listener;
    }
    
    @FXML
    public void submit(ActionEvent e) throws IOException {
    	keyWord = userKeyword.getText();
    	hashTags = userKeyword.getText().split(",");
    	startDate = userStartDate.getValue().atStartOfDay();
    	endDate = userEndDate.getValue().atTime(23, 59, 59);
    	maxResult = Integer.parseInt(userMaxResult.getText());
    	
    	InputData userInput = new InputData(keyWord, hashTags, startDate, endDate, maxResult);
    	
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/SearchingInterface.fxml"));
    	root = loader.load();
    	stage = (Stage)((Node)e.getSource()).getScene().getWindow();
    	scene = new Scene(root);
    	stage.setScene(scene);
    	stage.show();
    	
    	if (submitListener != null) {
    		submitListener.onSubmit(userInput);
    	}
    }
    
    @FXML
    public void cancel(ActionEvent e) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/CancellingInterface.fxml"));
    	root = loader.load();
    	stage = (Stage)((Node)e.getSource()).getScene().getWindow();
    	scene = new Scene(root);
    	stage.setScene(scene);
    	
//    	stage.setWidth(300);
//    	stage.setHeight(150);
//    	stage.centerOnScreen();
    	
    	stage.show();
    	
    	PauseTransition delay = new PauseTransition(Duration.seconds(1));
    	delay.setOnFinished(event -> {
    		stage.close();
    	});
    	
    	delay.play();
    }
}

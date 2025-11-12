package com.humanitarian.logistics.userInterface;

import java.time.LocalDateTime;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
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
    
    public interface SubmitListener {
    	void onSubmit(InputData data);
    }
    private SubmitListener submitListener;
    
    public void setOnSubmit(SubmitListener listener) {
    	this.submitListener = listener;
    }
    
    public void submit(ActionEvent e) {
    	keyWord = userKeyword.getText();
    	hashTags = userKeyword.getText().split(",");
    	startDate = userStartDate.getValue().atStartOfDay();
    	endDate = userEndDate.getValue().atTime(23, 59, 59);
    	maxResult = Integer.parseInt(userMaxResult.getText());
    	
    	InputData userInput = new InputData(keyWord, hashTags, startDate, endDate, maxResult);
    	
    	stage = (Stage) scenePane.getScene().getWindow();
    	stage.close();
    	
    	if (submitListener != null) {
    		submitListener.onSubmit(userInput);
    	}
    }
    
    public void cancel(ActionEvent e) {
    	stage = (Stage) scenePane.getScene().getWindow();
    	stage.close();
    }
}

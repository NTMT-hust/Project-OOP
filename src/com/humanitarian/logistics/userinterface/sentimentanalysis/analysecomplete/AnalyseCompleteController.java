package com.humanitarian.logistics.userinterface.sentimentanalysis.analysecomplete;

import java.io.IOException;
import java.util.List;

import com.humanitarian.logistics.util.SceneManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class AnalyseCompleteController {
	
	@FXML
	private TextArea textArea = new TextArea();
	@FXML
	private PieChart pieChart;
	@FXML
	private VBox customLegend;
	@FXML
	private Button returnButton;
	
	private String stringResults;
	private int positive, negative, neutral;
	
	public AnalyseCompleteController(String stringResults, List<Integer> sentimentResults) {
		this.stringResults = stringResults;
		this.positive = sentimentResults.get(0);
		this.negative = sentimentResults.get(1);
		this.neutral = sentimentResults.get(2);
	}
	
	@FXML
	public void initialize() {
		
		int total = this.negative + this.positive + this.neutral;
		
		String posColor = "#4caf50"; // Green
	    String neuColor = "#ffbf00"; // Yellow
	    String negColor = "#ff4d4d"; // Red
		
		textArea.setText(stringResults);
		
        PieChart.Data slice1 = new PieChart.Data("Positive", positive);
        PieChart.Data slice2 = new PieChart.Data("Negative", negative);
        PieChart.Data slice3 = new PieChart.Data("Neutral", neutral);

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                slice1, slice2, slice3
        );
        pieChart.setData(pieData);
        pieChart.setLegendVisible(false);
        
        styleSlice(slice1, posColor);
        addToLegend("Positive", posColor);

        styleSlice(slice2, negColor);
        addToLegend("Negative", negColor);

        styleSlice(slice3, neuColor);
        addToLegend("Neutral", neuColor);

        for (PieChart.Data d : pieChart.getData()) {
            double percentage = (d.getPieValue() / total) * 100;
            d.setName(String.format("%.1f%%", percentage));
        }
	}
	
	// Helper 1: Style the Chart Slice
	private void styleSlice(PieChart.Data data, String hexColor) {
	    // Slices use -fx-pie-color
	    data.getNode().setStyle("-fx-pie-color: " + hexColor + ";");
	}

	// Helper 2: Build the Legend Item
	private void addToLegend(String name, String hexColor) {
	    // 1. Create the Dot
	    Circle dot = new Circle(5);
	    dot.setStyle("-fx-fill: " + hexColor + ";");

	    // 2. Create the Label
	    Label label = new Label(name);
	    label.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 0 5; -fx-text-fill: white;");

	    // 3. CRITICAL STEP: Put them side-by-side in an HBox
	    HBox itemRow = new HBox(10); // 10 is the gap between dot and text
	    itemRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT); // Center vertically
	    itemRow.getChildren().addAll(dot, label);

	    // 4. Add this entire row to your main VBox
	    customLegend.getChildren().add(itemRow);
	}
	
	public void returnToMenu(ActionEvent e) throws IOException {
		
		Stage currentStage = (Stage) textArea.getScene().getWindow();
		currentStage.close();
		
		Stage stage = new Stage();
		SceneManager.loadScene(stage, "/com/humanitarian/logistics/userinterface/collectdata/problemselectmenu/SelectionMenu.fxml",
				"Humanitarian Logistics Project");
		
	}
	 
}

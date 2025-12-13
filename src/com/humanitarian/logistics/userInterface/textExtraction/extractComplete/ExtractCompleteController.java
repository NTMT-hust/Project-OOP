package com.humanitarian.logistics.userInterface.textExtraction.extractComplete;

import java.io.IOException;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ExtractCompleteController {
	
	@FXML
	private BarChart<String, Number> barChart;
	@FXML
	private CategoryAxis xAxis = new CategoryAxis();
	@FXML
	private NumberAxis yAxis = new NumberAxis();
	@FXML
	private Button returnButton;
	
	private Map<String, Integer> extractResults;
	
	public ExtractCompleteController(Map<String, Integer> extractResults) {
		this.extractResults = extractResults;
	}
	
	@FXML
	public void initialize() {
		loadChartData();
		
	}
	
    private void loadChartData() {
        // Optional: Label your axes
        xAxis.setLabel("Keywords");
        yAxis.setLabel("Frequency");
        barChart.setTitle("Damage Frequency based on News");
        barChart.setLegendVisible(false);

        // 2. Create a Series (a group of data bars)
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // 3. Add Data points to the series
        for (Map.Entry<String, Integer> entry : extractResults.entrySet()) {
            String category = entry.getKey();
            Integer value = entry.getValue();
            
            series.getData().add(new XYChart.Data<>(category, value));
        }

        barChart.getData().add(series);
    }
	
	public void returnToMenu(ActionEvent e) throws IOException {
		
		Stage currentStage = (Stage) returnButton.getScene().getWindow();
		currentStage.close();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/humanitarian/logistics/userInterface/collectData/problemSelectMenu/SelectionMenu.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setTitle("Humanitarian Logistics Project");
        stage.setScene(scene);
        stage.show();
	}
}

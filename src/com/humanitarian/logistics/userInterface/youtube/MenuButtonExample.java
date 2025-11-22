package com.humanitarian.logistics.userInterface.youtube;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.List;

public class MenuButtonExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Create the MenuButton with a default label
        MenuButton menuButton = new MenuButton("Select a Language");

        // 2. Create your list of objects (Strings in this case)
        List<String> dataList = List.of("Java", "Python", "C++", "JavaScript");

        // 3. Loop through the data to create MenuItems
        for (String option : dataList) {
            MenuItem item = new MenuItem(option);

            // 4. Define what happens when an item is clicked
            item.setOnAction(event -> {
                // Change the text of the main button to match the selection
                menuButton.setText(option);
                
                // Optional: Do other logic here
                System.out.println("User chose: " + option);
            });

            // 5. Add the item to the MenuButton
            menuButton.getItems().add(item);
        }

        // Standard Scene Setup
        StackPane root = new StackPane(menuButton);
        Scene scene = new Scene(root, 300, 200);

        primaryStage.setTitle("JavaFX MenuButton Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
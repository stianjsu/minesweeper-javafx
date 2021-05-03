package minesweeper;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class App extends Application {
    private Scene mainScene, gameScene;
    private MainController mainController;
    private GameController gameController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("fxml/Main.fxml"));
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("fxml/Game.fxml"));

        mainScene = new Scene(mainLoader.load());
        gameScene = new Scene(gameLoader.load());

        // Storing JavaFX controllers in variables
        mainController = mainLoader.getController();
        gameController = gameLoader.getController();

        // Set event handlers for buttons on start screen

        mainController.easyButton.setOnAction(e -> {
            gameController.initGame(9, 9, 10);
            primaryStage.setScene(gameScene);
        });

        mainController.mediumButton.setOnAction(e -> {
            gameController.initGame(16, 16, 40);
            primaryStage.setScene(gameScene);
        });

        mainController.hardButton.setOnAction(e -> {
            gameController.initGame(30, 16, 99);
            primaryStage.setScene(gameScene);
        });

        mainController.loadButton.setOnAction(e -> {
            try {
                // Allow user to select save file
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open save file");
                fileChooser.setInitialDirectory(new File("."));
                fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Minesweeper data files (.txt)", "*.txt"));
                File saveFile = fileChooser.showOpenDialog(primaryStage);

                if (saveFile != null) {
                    gameController.initGameFromFile(saveFile);
                    primaryStage.setScene(gameScene);
                }
            } catch (IOException e1) {
                // Show alert box with error if file operation fails
                Alert errorAlert = new Alert(AlertType.ERROR,
                        "Save file appears to be corrupt. Try loading another file.", ButtonType.OK);
                errorAlert.show();
            }
        });

        // Set event handlers for game screen

        gameController.newGameButton.setOnAction(e -> primaryStage.setScene(mainScene));

        gameController.saveButton.setOnAction(e -> {
            try {
                // Allow user to pick save location
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save game");
                fileChooser.setInitialDirectory(new File("."));
                fileChooser.setInitialFileName("minesweeper-save-file.txt");
                fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Minesweeper data files (.txt)", "*.txt"));
                File saveFile = fileChooser.showSaveDialog(primaryStage);

                if (saveFile != null) {
                    gameController.saveGameToFile(saveFile);
                }
            } catch (IOException e1) {
                // Show alert box with error if file operation fails
                Alert errorAlert = new Alert(AlertType.ERROR, "Failed to save the game file.", ButtonType.OK);
                errorAlert.show();
            }
        });

        // Shows main screen on app start
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Minesweeper");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(App.class, args);
    }
}

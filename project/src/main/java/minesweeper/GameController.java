package minesweeper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class GameController {
    // Constants for setting tile size and padding around board (in pixels)
    private static final int TILE_SIZE = 30;
    private static final int PADDING = 20;

    private Board gameBoard;
    private FileHandler fileHandler;

    // Defining FXML elements
    @FXML
    AnchorPane anchorPane;
    @FXML
    GridPane gridPane;
    @FXML
    StackPane controlPane;
    @FXML
    Pane boardPane;
    @FXML
    Button restartButton;
    @FXML
    Button newGameButton;
    @FXML
    Button saveButton;

    public void initialize() {
        fileHandler = new TextFileHandler();
    }

    public void initGameFromFile(File saveFile) throws IOException {
        this.gameBoard = this.fileHandler.readFromFile(saveFile);
        this.setFXMLStyle();

        this.createBoard();

        this.draw();
    }

    public void initGame(int width, int height, int numBombs) {
        this.gameBoard = new Board(width, height, numBombs);

        this.setFXMLStyle();

        this.createBoard();
    }

    private void setFXMLStyle() {
        anchorPane.setPrefHeight(this.gameBoard.getHeight() * TILE_SIZE + PADDING + 50);
        anchorPane.setPrefWidth(this.gameBoard.getWidth() * TILE_SIZE + PADDING);
        gridPane.setPrefHeight(this.gameBoard.getHeight() * TILE_SIZE + 50 + PADDING);
        gridPane.setPrefWidth(this.gameBoard.getWidth() * TILE_SIZE + PADDING);
        controlPane.setPrefHeight(this.gameBoard.getWidth() * TILE_SIZE + PADDING);
        boardPane.setPrefHeight(this.gameBoard.getHeight() * TILE_SIZE + PADDING);
        boardPane.setPrefWidth(this.gameBoard.getWidth() * TILE_SIZE + PADDING);
    }

    public void restart() {
        this.initGame(this.gameBoard.getWidth(), this.gameBoard.getHeight(), this.gameBoard.getNumBombs());
    }

    public void saveGameToFile(File file) throws IOException {
        this.fileHandler.writeToFile(this.gameBoard, file);
    }

    // Creates a Pane for each tile in gameBoard and assign the properties x and y
    // for corresponding tile. Sets OnMouseClicked event for flagging/opening tiles
    private void createBoard() {
        boardPane.getChildren().clear();
        boardPane.getHeight();
        for (int y = 0; y < gameBoard.getHeight(); y++) {
            for (int x = 0; x < gameBoard.getWidth(); x++) {
                Pane tilePane = new Pane();

                tilePane.setTranslateX(x * TILE_SIZE + PADDING / 2);
                tilePane.setTranslateY(y * TILE_SIZE + PADDING / 2);
                tilePane.setPrefWidth(TILE_SIZE);
                tilePane.setPrefHeight(TILE_SIZE);
                tilePane.getProperties().put("x", x);
                tilePane.getProperties().put("y", y);
                tilePane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        if (gameBoard.isGameActive()) {
                            if (e.getButton() == MouseButton.SECONDARY) {
                                gameBoard.flagTile((int) tilePane.getProperties().get("x"),
                                        (int) tilePane.getProperties().get("y"));
                            } else if (e.getButton() == MouseButton.PRIMARY) {
                                gameBoard.openTile((int) tilePane.getProperties().get("x"),
                                        (int) tilePane.getProperties().get("y"));
                            }

                            draw();
                        }
                    }
                });

                tilePane.setStyle("-fx-border-color: grey; -fx-border-width: 2px; -fx-background-color: lightgray;");

                boardPane.getChildren().add(tilePane);
            }
        }
    }

    public void draw() {
        for (int y = 0; y < gameBoard.getHeight(); y++) {
            for (int x = 0; x < gameBoard.getWidth(); x++) {
                Tile tile = gameBoard.getTile(x, y);
                Pane tilePane = (Pane) boardPane.getChildren().get(y * gameBoard.getWidth() + x);
                tilePane.getChildren().clear();

                String filename = "";

                if (tile.isOpen()) {
                    tilePane.setStyle("-fx-border-color: gray; -fx-border-width: 2px; -fx-background-color: white;");

                    if (tile.isBomb()) {
                        filename = "bomb";
                        tilePane.setStyle(
                                "-fx-border-color: grey; -fx-border-width: 2px; -fx-background-color: #ffa6a6;");
                    } else if (tile.getBombsAround() > 0) {
                        filename = String.valueOf(tile.getBombsAround());
                    }
                } else if (tile.isFlagged()) {
                    filename = "flag";
                }

                // If filename is specified, place the corresponding image on top of the tile
                if (filename.length() != 0) {
                    try {
                        InputStream imageStream = GameController.class
                                .getResourceAsStream("resources/" + filename + ".png");
                        Image tileImage = new Image(imageStream);
                        ImageView tileImageView = new ImageView(tileImage);
                        tileImageView.setFitWidth(TILE_SIZE * 0.6);
                        tileImageView.setFitHeight(TILE_SIZE * 0.6);
                        tileImageView.setX(TILE_SIZE * 0.2);
                        tileImageView.setY(TILE_SIZE * 0.2);
                        tilePane.getChildren().add(tileImageView);
                        imageStream.close();
                    } catch (FileNotFoundException e) {
                        Alert errorAlert = new Alert(AlertType.ERROR,
                                "Failed to load game resources. Try reinstalling the game.", ButtonType.OK);
                        errorAlert.showAndWait();
                        System.exit(0);
                    } catch (NullPointerException e) {
                        Alert errorAlert = new Alert(AlertType.ERROR,
                                "Failed to load game resources. Try reinstalling the game.", ButtonType.OK);
                        errorAlert.showAndWait();
                        System.exit(0);
                    } catch (IOException e) {
                        Alert errorAlert = new Alert(AlertType.ERROR,
                                "Failed to load game resources. Try reinstalling the game.", ButtonType.OK);
                        errorAlert.showAndWait();
                        System.exit(0);
                    }
                }
            }
        }

        if (gameBoard.checkVictory()) {
            Alert winAlert = new Alert(AlertType.NONE, "You Won!", ButtonType.OK);
            winAlert.show();
        }
    }
}

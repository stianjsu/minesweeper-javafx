package minesweeper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextFileHandler implements FileHandler {
    public Board readFromFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        try {
            // Reading board properties
            String[] props = reader.readLine().split(" ");

            // Creating two-dimensional ArrayList from the file content
            List<List<String>> boardLayout = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                boardLayout.add(Arrays.asList(line.split(" ")));
            }

            int boardHeight = boardLayout.size();
            int boardWidth = boardLayout.get(0).size();
            int numBombs = Integer.valueOf(props[0]);

            Board board = new Board(boardWidth, boardHeight, numBombs);

            // Set fields in Board object
            board.setBoardProps(Integer.valueOf(props[0]), Integer.valueOf(props[1]) != 0,
                    Integer.valueOf(props[2]) != 0, Integer.valueOf(props[3]) != 0);

            // Load tile data from file
            for (int y = 0; y < boardHeight; y++) {
                for (int x = 0; x < boardWidth; x++) {
                    String tileData = boardLayout.get(y).get(x);

                    boolean bomb = tileData.contains("b");
                    boolean flagged = tileData.contains("f");
                    boolean open = tileData.contains("o");

                    board.setTileProps(x, y, bomb, flagged, open);
                }
            }

            return board;
        } catch (IllegalArgumentException e) {
            // File contains illegal values
            throw new IOException("Invalid save file!");
        } catch (IndexOutOfBoundsException e) {
            // File is not formatted correctly
            throw new IOException("Invalid save file!");
        } finally {
            reader.close();
        }
    }

    public void writeToFile(Board board, File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        try {
            writer.write(board.getBoardProps());
            writer.newLine();

            for (int y = 0; y < board.getHeight(); y++) {
                for (int x = 0; x < board.getWidth(); x++) {
                    Tile tile = board.getTile(x, y);
                    writer.write(tile.isBomb() ? "b" : "x");
                    writer.write(tile.isFlagged() ? "f" : "x");
                    writer.write(tile.isOpen() ? "o " : "x ");
                }

                writer.newLine();
            }
        } finally {
            // Ensures that BufferedWriter gets closed, even if IOException is thrown
            writer.close();
        }
    }
}
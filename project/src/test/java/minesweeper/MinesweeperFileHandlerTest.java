package minesweeper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

public class MinesweeperFileHandlerTest {
    FileHandler fileHandler;
    Board board1;
    Board board2;
    Board board3;

    @BeforeEach
    public void setup() {
        // Initializes three board objects and a file handler object

        fileHandler = new TextFileHandler();

        board1 = new Board(50, 50, 30);

        board2 = new Board(30, 50, 20);
        board2.openTile(5, 5);

        board3 = new Board(9, 9, 10);
        board3.flagTile(7, 4);
        board3.openTile(4, 7);
    }

    @Test
    @DisplayName("Testing saving and loading game state from file")
    public void testSaveLoad() throws IOException {
        fileHandler.writeToFile(board1, new File("./TestBoard1.txt"));
        fileHandler.writeToFile(board2, new File("./TestBoard2.txt"));
        fileHandler.writeToFile(board3, new File("./TestBoard3.txt"));
        Board savedBoard1 = fileHandler.readFromFile(new File("./TestBoard1.txt"));
        Board savedBoard2 = fileHandler.readFromFile(new File("./TestBoard2.txt"));
        Board savedBoard3 = fileHandler.readFromFile(new File("./TestBoard3.txt"));

        assertEquals(board1.toString(), savedBoard1.toString(), "Saved board should be equal");

        assertEquals(board2.toString(), savedBoard2.toString(), "Saved board should be equal");
        board2.openTile(3, 3);
        savedBoard2.openTile(3, 3);
        assertEquals(board2.toString(), savedBoard2.toString(), "Saved board should be equal");

        assertEquals(board3.toString(), savedBoard3.toString(), "Saved board should be equal");
        assertNotEquals(board2.toString(), savedBoard3.toString(),
                "Boards of different dimensions should not be equal");
        assertNotEquals(board3.toString(), savedBoard2.toString(),
                "Boards of different dimensions should not be equal");
    }

    // Cleans up leftover save files after test is completed
    @AfterAll
    public static void teardown() throws IOException {
        File board1File = new File("./TestBoard1.txt");
        File board2File = new File("./TestBoard2.txt");
        File board3File = new File("./TestBoard3.txt");

        if (!board1File.delete()) {
            throw new IOException("Failed to delete file at " + board1File.getPath());
        }
        if (!board2File.delete()) {
            throw new IOException("Failed to delete file at " + board2File.getPath());
        }
        if (!board3File.delete()) {
            throw new IOException("Failed to delete file at " + board3File.getPath());
        }
    }
}

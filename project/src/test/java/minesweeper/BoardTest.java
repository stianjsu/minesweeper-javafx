package minesweeper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BoardTest {
    Board board;
    Board premadeBoard;

    @BeforeEach
    public void setup() {
        board = new Board(70, 70, 60);

        // Manually creates a non-random board with this bomb layout:
        // 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 1 1 1 0
        // 0 0 0 0 1 2 X 2 1
        // 0 0 0 0 1 X 4 X 1
        // 0 0 0 0 1 2 X 2 1
        // 0 0 0 0 0 1 1 1 0

        premadeBoard = new Board(9, 9, 10);
        premadeBoard.preventAutoGeneratingBombLayout();
        premadeBoard.placeBombAtTile(6, 5);
        premadeBoard.placeBombAtTile(6, 7);
        premadeBoard.placeBombAtTile(5, 6);
        premadeBoard.placeBombAtTile(7, 6);
    }

    @Test
    @DisplayName("Testing constructor")
    public void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Board(10, 10, 100);
        }, "Should not be able to make a board with as many bombs as tiles");

        assertThrows(IllegalArgumentException.class, () -> {
            new Board(10, 10, 110);
        }, "Should not be able to make a board with more bombs than tiles");

        assertThrows(IllegalArgumentException.class, () -> {
            new Board(3, 10, 4);
        }, "Should not be able to make a board smaller than 4x4");

        assertThrows(IllegalArgumentException.class, () -> {
            new Board(-100, 10, 4);
        }, "Should not be able to make a board with non-positive values");

        assertThrows(IllegalArgumentException.class, () -> {
            new Board(100, -10, 4);
        }, "Should not be able to make a board with non-positive values");

        assertThrows(IllegalArgumentException.class, () -> {
            new Board(100, 10, -4);
        }, "Should not be able to make a board with non-positive values");

        assertThrows(IllegalArgumentException.class, () -> {
            new Board(100, 10, 0);
        }, "Should not be able to make a board with no bombs");
    }

    @Test
    @DisplayName("Testing setting and getting board properties")
    public void testBoardProps() {
        board.setBoardProps(20, true, false, false);
        assertEquals(board.getBoardProps(), "20 1 0 0 ", "Board properties were not set correctly");

        assertThrows(IllegalArgumentException.class, () -> {
            board.setBoardProps(0, true, false, true);
        }, "Number of bombs must be a positive number");
    }

    @Test
    @DisplayName("Testing logic for recursive tile opening")
    public void testOpeningTiles() {
        premadeBoard.openTile(4, 4);

        // Checks that the appropriate tiles are open, like this:
        // 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 1 1 1 0
        // 0 0 0 0 1 2 □ 2 1
        // 0 0 0 0 1 □ □ □ □
        // 0 0 0 0 1 2 □ □ □
        // 0 0 0 0 0 1 □ □ □
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (x > 5 && y > 5 || (x == 5 && y == 6) || (x == 6 && y == 5)) {
                    assertFalse(premadeBoard.getTile(x, y).isOpen(), "This tile not should be open");
                } else {
                    assertTrue(premadeBoard.getTile(x, y).isOpen(), "This tile should be open");
                }
            }
        }

    }

    @Test
    @DisplayName("Testing logic for generating bomb layout")
    public void testBombLayout() {
        // The first opened tile triggers generation of bomb layout
        board.openTile(30, 60);

        // Count number of bombs
        int bombCount = 0;
        for (int y = 0; y < 70; y++) {
            for (int x = 0; x < 70; x++) {
                if (board.getTile(x, y).isBomb()) {
                    bombCount++;
                }
            }
        }

        assertEquals(bombCount, 60, "Incorrect number of bombs");

        // Checks that there are no bombs around the first opened tile
        assertFalse(board.getTile(29, 59).isBomb(), "Bomb should not be placed around opening tile");
        assertFalse(board.getTile(29, 60).isBomb(), "Bomb should not be placed around opening tile");
        assertFalse(board.getTile(29, 61).isBomb(), "Bomb should not be placed around opening tile");
        assertFalse(board.getTile(30, 59).isBomb(), "Bomb should not be placed around opening tile");
        assertFalse(board.getTile(30, 61).isBomb(), "Bomb should not be placed around opening tile");
        assertFalse(board.getTile(31, 59).isBomb(), "Bomb should not be placed around opening tile");
        assertFalse(board.getTile(31, 60).isBomb(), "Bomb should not be placed around opening tile");
        assertFalse(board.getTile(31, 61).isBomb(), "Bomb should not be placed around opening tile");
    }

    @Test
    @DisplayName("Testing manually placing bomb on tile")
    public void testPlaceBombAtTile() {
        assertTrue(premadeBoard.getTile(6, 5).isBomb(), "Tile should have a bomb placed on it");
        assertTrue(premadeBoard.getTile(6, 7).isBomb(), "Tile should have a bomb placed on it");
        assertTrue(premadeBoard.getTile(5, 6).isBomb(), "Tile should have a bomb placed on it");
        assertTrue(premadeBoard.getTile(7, 6).isBomb(), "Tile should have a bomb placed on it");
        assertFalse(premadeBoard.getTile(6, 6).isBomb(), "Tile should not have a bomb placed on it");
    }

    @Test
    @DisplayName("Testing flagging of tiles")
    public void testFlagging() {
        int x = 5;
        int y = 5;
        assertFalse(board.getTile(x, y).isFlagged(), "Tile should default to being flagged flagged");
        board.flagTile(x, y);
        assertTrue(board.getTile(x, y).isFlagged(), "Flagging a tile is not working");
        board.flagTile(x, y);
        assertFalse(board.getTile(x, y).isFlagged(), "Unflagging a tile is not working");

        assertThrows(IllegalArgumentException.class, () -> {
            board.flagTile(-5, 1);
        }, "Should not be able to flag tile outside of board area");
        assertThrows(IllegalArgumentException.class, () -> {
            board.flagTile(1, -1);
        }, "Should not be able to flag tile outside of board area");
        assertThrows(IllegalArgumentException.class, () -> {
            board.flagTile(100, 1);
        }, "Should not be able to flag tile outside of board area");
        assertThrows(IllegalArgumentException.class, () -> {
            board.flagTile(1, 100);
        }, "Should not be able to flag tile outside of board area");

        Board board1 = new Board(10, 10, 9);

        board1.openTile(x, y);
        board1.flagTile(x, y);
        assertFalse(board1.getTile(x, y).isFlagged(), "Should not be able to flag an already opened tile");
    }

    @Test
    @DisplayName("Testing victory recognition")
    public void testVictory() {
        premadeBoard.openTile(4, 4);
        premadeBoard.openTile(6, 6);
        premadeBoard.openTile(8, 8);
        premadeBoard.openTile(8, 6);

        assertFalse(premadeBoard.checkVictory(), "Victory handler gets called while the game should still be active");

        premadeBoard.openTile(6, 8);

        assertTrue(premadeBoard.checkVictory(), "Victory was not recognized");
        assertFalse(premadeBoard.isGameActive(), "Game is still set to active after player has won");
        assertTrue(premadeBoard.getTile(5, 6).isFlagged(), "All bombs are not flagged after player has won");
    }

    @Test
    @DisplayName("Testing game over recognition")
    public void testGameOver() {
        assertTrue(premadeBoard.isGameActive(), "Game should still be active");

        premadeBoard.openTile(5, 6);

        assertFalse(premadeBoard.isGameActive(), "Game is still set to active after player has lost");
        assertTrue(premadeBoard.getTile(6, 5).isOpen(), "Tiles with bombs should be opened after player has lost");
    }
}

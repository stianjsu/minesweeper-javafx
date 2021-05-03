package minesweeper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TileTest {
    Tile tile;

    @BeforeEach
    public void setup() {
        tile = new Tile();
    }

    @Test
    @DisplayName("Testing constructor")
    public void testConstructor() {
        assertFalse(tile.isBomb(), "Tile should not contain a bomb");
        assertFalse(tile.isFlagged(), "Tile should not be flagged");
        assertFalse(tile.isOpen(), "Tile should not be open");

        Tile tile2 = new Tile(true, true, false);
        assertTrue(tile2.isBomb(), "Tile should contain a bomb");
        assertTrue(tile2.isFlagged(), "Tile should be flagged");
        assertFalse(tile2.isOpen(), "Tile should not be open");
    }

    @Test
    @DisplayName("Testing placement of bomb on tile")
    public void testBomb() {
        assertFalse(tile.isBomb(), "Tile should initiate without bomb");
        tile.setBomb();
        assertTrue(tile.isBomb(), "setBomb() method failed");
    }

    @Test
    @DisplayName("Testing toggling flag")
    public void testFlagging() {
        assertFalse(tile.isFlagged(), "Tile should initiate without flag");
        tile.toggleFlag();
        assertTrue(tile.isFlagged(), "toggleFlag() method failed, tile should now be flagged");
        tile.toggleFlag();
        assertFalse(tile.isFlagged(), "toggleFlag() method failed, tile should now be unflagged");
    }

    @Test
    @DisplayName("Testing opening and closing tile")
    public void testOpenClose() {
        assertFalse(tile.isOpen(), "Tile should initiate as closed");
        tile.open();
        assertTrue(tile.isOpen(), "open() method failed");
    }

    @Test
    @DisplayName("Testing number of bombs around a tile")
    public void testBombsAround() {
        assertEquals(tile.getBombsAround(), 0, "Tile should initiate with 0 bombs around it");
        tile.setBombsAround(3);
        assertEquals(tile.getBombsAround(), 3, "Number of bombs around tile was not set correctly");
        tile.setBombsAround(2);
        assertEquals(tile.getBombsAround(), 2, "Number of bombs around tile was not set correctly");

        assertThrows(IllegalArgumentException.class, () -> {
            tile.setBombsAround(-3);
        }, "Should not be able to set a negative number of bombs around tile");
    }
}

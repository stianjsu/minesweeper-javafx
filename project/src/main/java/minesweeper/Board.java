package minesweeper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Board {
    private Tile[][] board;
    private int height;
    private int width;
    private int numBombs;
    private boolean isBombLayoutGenerated;
    private boolean hasWon;
    private boolean gameOver;

    public Board(int width, int height, int numBombs) {
        if (width * height <= numBombs) {
            throw new IllegalArgumentException("Number of bombs must not match or exceed number of tiles");
        }

        if (width < 4 || height < 4 || numBombs < 1) {
            throw new IllegalArgumentException("Board dimensions must be at least 4x4");
        }

        this.height = height;
        this.width = width;
        this.numBombs = numBombs;

        // Initiates the board with a tile class in each grid square
        board = new Tile[this.height][this.width];
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                board[y][x] = new Tile();
            }
        }
    }

    private void generateBombLayout(int firstX, int firstY) {
        // Bombs must not be placed around the first opened tile
        ArrayList<int[]> illegalPlacements = getCoordinatesForTilesAround(firstX, firstY);

        // Itereates until the board is populated with the specified number of bombs
        int i = 0;
        Random random = new Random();
        while (i < this.numBombs) {
            int randRow = random.nextInt(this.height);
            int randCol = random.nextInt(this.width);

            int[] placement = { randCol, randRow };

            // Checking whether a bomb is already selected to be on that tile
            boolean illegalTile = false;
            for (int[] illegalPlacement : illegalPlacements) {
                if (Arrays.equals(placement, illegalPlacement)) {
                    illegalTile = true;
                    break;
                }
            }

            // Places bomb if the placement is legal and there is no bomb there already
            if (!illegalTile && !board[randRow][randCol].isBomb()) {
                this.board[randRow][randCol].setBomb();
                i++;
            }
        }

        // Runs function for calculating the number on each tile after bomb layout is
        // generated
        calculateTileNumbers();
    }

    // Gives each tile a number representing the amount of bombs around it
    private void calculateTileNumbers() {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                ArrayList<int[]> tilesAroundCoords = this.getCoordinatesForTilesAround(x, y);

                int bombsAround = 0;
                for (int[] coords : tilesAroundCoords) {
                    Tile tileAround = this.getTile(coords[0], coords[1]);
                    if (tileAround.isBomb()) {
                        bombsAround++;
                    }
                }

                Tile tile = this.getTile(x, y);
                tile.setBombsAround(bombsAround);
            }
        }
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            throw new IllegalArgumentException("Can't get tile outside of board");
        }
        return board[y][x];
    }

    public void openTile(int x, int y) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            throw new IllegalArgumentException("Can't open tile outside of board");
        }

        // Generates the bomb layout before the first move
        if (!isBombLayoutGenerated) {
            generateBombLayout(x, y);
            isBombLayoutGenerated = true;
        }

        Tile selectedTile = getTile(x, y);
        // Only open tile if game is active and the tile is not flagged
        if (this.isGameActive() && !selectedTile.isOpen() && !selectedTile.isFlagged()) {
            selectedTile.open();

            if (selectedTile.isBomb()) {
                this.gameOver = true;
                this.handleGameCompleted();
            } else {
                // Recusively opens surrounding blank tiles
                this.recurseOpenTile(x, y);
            }

            if (this.checkVictory()) {
                this.hasWon = true;
                this.handleGameCompleted();
            }
        }
    }

    private void recurseOpenTile(int x, int y) {
        Tile selectedTile = getTile(x, y);
        selectedTile.open();

        if (selectedTile.getBombsAround() == 0) {
            ArrayList<int[]> tilesAroundCoordinates = getCoordinatesForTilesAround(x, y);
            for (int[] tileCoords : tilesAroundCoordinates) {
                Tile nextTile = getTile(tileCoords[0], tileCoords[1]);
                if (!nextTile.isOpen() && !nextTile.isFlagged()) {
                    // If tile is not already open, is not flagged and has no bombs surrounding it,
                    // continue recursing
                    this.recurseOpenTile(tileCoords[0], tileCoords[1]);
                }
            }
        }
    }

    public void flagTile(int x, int y) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            throw new IllegalArgumentException("Can't flag tile outside of board");
        }

        Tile tile = this.getTile(x, y);
        if (!tile.isOpen()) {
            tile.toggleFlag();
        }
    }

    public boolean checkVictory() {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                Tile tile = this.getTile(x, y);

                // Check if there exists a closed tile that does not contain a bomb
                if (!tile.isOpen() && !tile.isBomb()) {
                    return false;
                }
            }
        }

        return true;
    }

    private void handleGameCompleted() {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                Tile tile = this.getTile(x, y);

                // If game over: Open all tiles that contain a bomb
                if (this.gameOver) {
                    if (tile.isBomb()) {
                        tile.open();
                    }
                }

                // If victory: Flag all bombs
                else if (this.hasWon) {
                    if (tile.isBomb() && !tile.isFlagged()) {
                        tile.toggleFlag();
                    }
                }
            }
        }
    }

    // Returns an ArrayList of coordinates for the 8 tiles surrounding the input
    // coordinates
    private ArrayList<int[]> getCoordinatesForTilesAround(int x, int y) {
        ArrayList<int[]> tilesAround = new ArrayList<int[]>();

        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && j >= 0 && i < this.width && j < this.height) {
                    int[] coordinateArray = { i, j };
                    tilesAround.add(coordinateArray);
                }
            }
        }

        return tilesAround;
    }

    public void preventAutoGeneratingBombLayout() {
        this.isBombLayoutGenerated = true;
    }

    public void placeBombAtTile(int x, int y) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            throw new IllegalArgumentException("Can't open tile outside of board");
        }
        
        this.board[x][y].setBomb();
        this.calculateTileNumbers();
    }

    public void setTileProps(int x, int y, boolean bomb, boolean flagged, boolean open) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            throw new IllegalArgumentException("Can't open tile outside of board");
        }

        this.board[y][x] = new Tile(bomb, flagged, open);
        calculateTileNumbers();
    }

    // Return a text string of board properties on the format "10 1 0 0" (for file
    // saving purposes)
    public String getBoardProps() {
        return String.valueOf(this.numBombs) + " " + (this.isBombLayoutGenerated ? "1" : "0") + " "
                + (this.hasWon ? "1" : "0") + " " + (this.gameOver ? "1" : "0" + " ");
    }

    // Function for setting board properties manually
    public void setBoardProps(int numBombs, boolean isBombLayoutGenerated, boolean hasWon, boolean gameOver) {
        if (numBombs < 1) {
            throw new IllegalArgumentException("Number of bombs must be greater than 0");
        }

        this.numBombs = numBombs;
        this.isBombLayoutGenerated = isBombLayoutGenerated;
        this.hasWon = hasWon;
        this.gameOver = gameOver;
    }

    public boolean isGameActive() {
        return !(this.hasWon || this.gameOver);
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getNumBombs() {
        return numBombs;
    }

    // Returns a text representation of the board (for debugging purposes)
    @Override
    public String toString() {
        String boardString = "";
        for (Tile[] row : board) {
            for (Tile tile : row) {
                if (tile.isOpen() && !tile.isBomb()) {
                    boardString += String.valueOf(tile.getBombsAround()) + " ";
                    ;
                } else if (tile.isOpen() && tile.isBomb()) {
                    boardString += "⦻ ";
                } else if (tile.isFlagged()) {
                    boardString += "# ";
                } else {
                    boardString += "□ ";
                }
            }
            boardString += "\n";
        }

        if (this.gameOver) {
            boardString += "Game Over!";
        } else if (this.hasWon) {
            boardString += "You Won!";
        }

        return boardString;
    }
}

package minesweeper;

public class Tile {
    private boolean open;
    private boolean bomb;
    private boolean flagged;
    private int bombsAround;

    // Empty constructor for initializing new tile with default values
    public Tile() {
    }

    public Tile(boolean bomb, boolean flagged, boolean open) {
        this.open = open;
        this.flagged = flagged;
        this.bomb = bomb;
    }

    public boolean isBomb() {
        return this.bomb;
    }

    public boolean isOpen() {
        return this.open;
    }

    public boolean isFlagged() {
        return this.flagged;
    }

    public int getBombsAround() {
        return this.bombsAround;
    }

    public void setBomb() {
        this.bomb = true;
    }

    public void setBombsAround(int bombsAround) {
        if (bombsAround < 0) {
            throw new IllegalArgumentException("Bombs around a tile cant be negative");
        }
        this.bombsAround = bombsAround;
    }

    public void open() {
        this.open = true;
    }

    public void toggleFlag() {
        this.flagged = !this.flagged;
    }
}
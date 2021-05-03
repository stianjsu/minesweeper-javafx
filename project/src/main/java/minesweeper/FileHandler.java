package minesweeper;

import java.io.File;
import java.io.IOException;

public interface FileHandler {
    Board readFromFile(File file) throws IOException;

    void writeToFile(Board board, File file) throws IOException;
}

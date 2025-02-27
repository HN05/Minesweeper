package minesweeper;

import minesweeper.model.Cell;

public interface GameListener {
    public void updatedCell(Cell cell);
    public void updatedGameState();
}

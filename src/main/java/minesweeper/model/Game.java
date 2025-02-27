package minesweeper.model;

import java.util.ArrayList;

import minesweeper.GameListener;

public class Game {
    private final Board board;
    private final GameListener listener;
    private final ArrayList<Short> actions = new ArrayList<>();
    private int markCount = 0;
    private boolean isFinished = false;
    private boolean hasLost = false;

    public Game(Board board, GameListener listener) {
        this.board = board;
        this.listener = listener;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean hasLost() {
        return hasLost;
    }

    public int getBombsLeft() {
        return board.getBombCount() - markCount;
    }

    private void checkIfWon() {
        if (getBombsLeft() == 0) {
            isFinished = true;
            listener.updatedState();
        }
    }

    private short combinePos(final int x, final int y, final boolean isMark) {
        // Left-shift mark so that it is first bit
        final short mark_value = (short) (isMark ? 1 << 15 : 0);

        // Second bit is left empty in case of new flag

        // First truncate x to 7 bits (just in case)
        // Then left-shift to take up third to 9th bit
        final short x_value = (short) ((x & 0x7F) << 7);
        // Truncate y to 7 bits (just in case)
        final short y_value = (short) ((y & 0x7F));
        // Use bitwise or to combine all the values
        return (short) (mark_value | x_value | y_value);
    }

    public void reveal(final int x, final int y) {
        final Cell cell = board.get(x, y);
        if (cell.isRevealed())
            return;

        cell.revealCell();
        actions.add(combinePos(x, y, false));
        if (cell.isBomb()) {
            hasLost = true;
            isFinished = true;
            listener.updatedState();
        } else {
            checkIfWon();
        }
        listener.updatedCell(cell);
    }

    public void mark(final int x, final int y) {
        final Cell cell = board.get(x, y);
        if (cell.isMarked())
            return;

        cell.markCell();
        actions.add(combinePos(x, y, true));
        markCount++;
        listener.updatedCell(cell);
    }
}

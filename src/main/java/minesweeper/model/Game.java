package minesweeper.model;

import java.util.Collection;
import java.util.HashSet;

import minesweeper.GameListener;

public final class Game {
    private final Board board;
    private final Collection<GameListener> listeners = new HashSet<>();
    private final ActionList actionList;
    private int markCount = 0;
    private boolean isFinished = false;
    private boolean hasLost = false;

    public Game(final Board board) {
        this.board = board;
        this.actionList = new ActionList((short) board.getRowCount(), (short) board.getColCount());
    }

    public Game(final Board board, final ActionList actionList) {
        this.board = board;
        this.actionList = actionList;
        this.actionList.forEachAction(this::executeAction);
    }

    private void executeAction(final Action action) {
        if (action.type().isMark()) {
            mark(action.x(), action.y());
        } else {
            reveal(action.x(), action.y());
        }
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

    public Board getBoard() {
        return board;
    }

    public ActionList getActionList() {
        return actionList;
    }

    public void addListener(final GameListener listener) {
        listeners.add(listener);
    }

    public void removeListener(final GameListener listener) {
        listeners.remove(listener);
    }

    private void updatedGameState() {
        for (GameListener listener : listeners) {
            listener.updatedGameState();
        }
    }

    private void updatedCell(final Cell cell) {
        for (GameListener listener : listeners) {
            listener.updatedCell(cell);
        }
    }

    private void checkIfWon() {
        if (getBombsLeft() == 0) {
            isFinished = true;
            updatedGameState();
        }
    }

    public void reveal(final int x, final int y) {
        final Cell cell = board.get(x, y);
        if (cell.isRevealed())
            return;

        cell.revealCell();
        actionList.addAction(new Action(x, y, ActionType.REVEAL));
        if (cell.isBomb()) {
            hasLost = true;
            isFinished = true;
            updatedGameState();
        } else {
            checkIfWon();
        }
        updatedCell(cell);
    }

    public void mark(final int x, final int y) {
        final Cell cell = board.get(x, y);
        if (cell.isMarked())
            return;

        cell.markCell();
        actionList.addAction(new Action(x, y, ActionType.MARK));
        markCount++;
        updatedCell(cell);
    }
}

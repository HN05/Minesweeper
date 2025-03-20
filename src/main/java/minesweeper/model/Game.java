package minesweeper.model;

import minesweeper.GameListener;

public final class Game {
    private final Board board;
    private final GameListener listener;
    private final ActionList actionList;
    private int markCount = 0;
    private boolean isFinished = false;
    private boolean hasLost = false;

    public Game(final Board board, final GameListener listener) {
        this.board = board;
        this.listener = listener;
        this.actionList = new ActionList((short) board.getRowCount(), (short) board.getColCount());
    }

    public Game(final Board board, final GameListener listener, final ActionList actionList) {
        this.board = board;
        this.listener = listener;
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

    private void checkIfWon() {
        if (getBombsLeft() == 0) {
            isFinished = true;
            listener.updatedGameState();
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
            listener.updatedGameState();
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
        actionList.addAction(new Action(x, y, ActionType.MARK));
        markCount++;
        listener.updatedCell(cell);
    }
}

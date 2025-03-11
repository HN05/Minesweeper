package minesweeper.model;

import java.util.ArrayList;
import java.util.List;

import minesweeper.GameListener;

final public class Game {
    private final Board board;
    private final GameListener listener;
    private final ActionList actions;
    private int markCount = 0;
    private boolean isFinished = false;
    private boolean hasLost = false;

    public Game(final Board board, final GameListener listener) {
        this.board = board;
        this.listener = listener;
        this.actions = new ActionList((short) board.getRowCount(), (short) board.getColCount());
    }

    public Game(final Board board, final GameListener listener, final ActionList actions) {
        this.board = board;
        this.listener = listener;
        this.actions = actions;
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
            listener.updatedGameState();
        }
    }

    public void reveal(final int x, final int y) {
        final Cell cell = board.get(x, y);
        if (cell.isRevealed())
            return;

        cell.revealCell();
        actions.addAction(x, y, ActionType.REVEAL);
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
        actions.addAction(x, y, ActionType.MARK);
        markCount++;
        listener.updatedCell(cell);
    }

    final public class ActionList {
        private final List<Integer> actions;
        private final int bitRowSize;
        private final int bitColSize;
        private final int chunkSize;
        private int chunkCount = 0;

        public ActionList(final short colSize, final short rowSize) {
            if (colSize <= 0 || rowSize <= 0) {
                throw new IllegalArgumentException("Sizes must be positive");
            }
            this.bitColSize = requiredSize(colSize);
            this.bitRowSize = requiredSize(rowSize);
            this.chunkSize = bitRowSize + bitColSize + 1;
            this.actions = new ArrayList<>();
            final int metadata = rowSize << 16 | colSize;
            actions.add(metadata);
        }

        // public ActionList(final int[] bitData) {
        //
        // }

        private int requiredSize(final int value) {
            int pow = 0;
            int val = 1;
            while (val < value) {
                pow++;
                val <<= 1;
            }
            return pow;
        }

        public void addAction(final int x, final int y, final ActionType type) {
            int left = chunkSize;
            int bitNum = 32 + left * chunkCount;
            while (left > 0) {
                final int num = bitNum % 32;
                final int intIndex = bitNum / 32;

                // check if need to add int
                if (num == 0) {
                    actions.add(0);
                }
                int val = actions.get(intIndex);
                val <<= 1;
                int bit = 0;

                if (left == chunkSize) {
                    bit = type.isMark() ? 1 : 0;
                } else if (left > bitColSize) {
                    bit = y >>> bitColSize - (chunkSize - left);
                } else {
                    bit = x >>> left - 1 - bitRowSize;
                }
                val |= (bit) << (31-num);

                actions.set(intIndex, val);

                left--;
                bitNum++;
            }
            chunkCount++;
        }

    }

    private enum ActionType {
        MARK, REVEAL;

        public boolean isMark() {
            return this == ActionType.MARK;
        }
    }
}

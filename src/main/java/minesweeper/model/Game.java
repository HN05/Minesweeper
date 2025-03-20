package minesweeper.model;

import java.util.ArrayList;
import java.util.List;

import minesweeper.GameListener;

public final class Game {
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

    public Board getBoard() {
        return board;
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

    public byte[] convertToBytes() {
        return new byte[0];
    }

    public final class ActionList {
        private final List<Byte> actions;
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
            actions.add((byte) (colSize >>> 8));
            actions.add((byte) (colSize));
            actions.add((byte) (rowSize >>> 8));
            actions.add((byte) (rowSize));
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
                final int num = bitNum % 8;
                final int byteIndex = bitNum / 8;

                // check if need to add int
                if (num == 0) {
                    actions.add((byte) 0);
                }
                byte val = actions.get(byteIndex);
                val <<= 1;
                int bit = 0;

                if (left == chunkSize) {
                    bit = type.isMark() ? 1 : 0;
                } else if (left > bitColSize) {
                    bit = y >>> bitColSize - (chunkSize - left);
                } else {
                    bit = x >>> left - 1 - bitRowSize;
                }
                val |= (bit) << (7-num);

                actions.set(byteIndex, val);

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

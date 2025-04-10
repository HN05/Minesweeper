package minesweeper.model;

public record Action(int x, int y, ActionType type) {
    public Action(final Cell cell, ActionType type) {
        this(cell.getX(), cell.getY(), type);
    }
}

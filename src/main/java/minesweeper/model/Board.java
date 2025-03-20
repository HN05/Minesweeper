package minesweeper.model;

import java.nio.file.FileStore;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import minesweeper.FileStorage;

public final class Board {
    private final Cell[][] cells;
    private final int bombCount;
    private final int id;

    public Board(final Cell[][] cells) {
        this(cells, FileStorage.getNewBoardID());
    }

    public Board(final Cell[][] cells, final int id) {
        Objects.requireNonNull(cells);
        if (cells.length == 0) throw new IllegalArgumentException("Can not be empty board");
        this.cells = cells;

        final long bombCount = flatStream()
            .filter(Cell::isBomb)
            .count();

        this.bombCount = (int) bombCount;
        this.id = id;
    }

    public int getBombCount() {
        return bombCount;
    }

    public int getColCount() {
        return cells[0].length;
    }

    public int getRowCount() {
        return cells.length;
    }
    
    public Cell[][] getCells() {
        return this.cells;
    }

    public Cell get(int x, int y) {
        return cells[y][x];
    }

    public Stream<Cell[]> stream() {
        return Arrays.stream(cells);
    }

    public Stream<Cell> flatStream() {
        return stream().flatMap(Arrays::stream);
    }

    public int getID() {
        return id;
    }

}

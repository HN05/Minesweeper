package minesweeper.model;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class Board {
    private final Cell[][] cells;
    private final int bombCount;

    public Board(final Cell[][] cells) {
        Objects.requireNonNull(cells);
        if (cells.length == 0) throw new IllegalArgumentException("Can not be empty board");
        this.cells = cells;

        final long bombCount = flatStream()
            .filter(Cell::isBomb)
            .count();

        this.bombCount = (int) bombCount;
    }

    public int getBombCount() {
        return bombCount;
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

}

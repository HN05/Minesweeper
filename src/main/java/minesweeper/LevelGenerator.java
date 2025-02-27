package minesweeper;

import java.util.Random;

public class LevelGenerator {
    // each element is a row, and the bits in the element correspond to 0 = no bomb
    // 1 = bomb
    // the first square in the row is the last bit in the row int
    public int[] generateGrid(final byte size, final float bombChance) {
        if (size > 32) {
            throw new IllegalArgumentException("Too many rows/columns max 32");
        }
        if (bombChance <= 0 || bombChance >= 1) {
            throw new IllegalArgumentException("bombChance must be between 0 and 1");
        }
        final Random rand = new Random();
        int[] rows = new int[size];
        for (byte y = 0; y < size; y++) {
            int row = 0;
            for (int x = 0; x < size; x++) {
                if (rand.nextFloat() < bombChance) {
                    // Shifts 1 'x' bits to the left
                    // Use bitwise or to make that bit 1 in 'row'
                    row |= (1 << x);
                }
            }
            rows[y] = row;
        }
        return rows;
    }

    public Cell[][] generateCells(final int[] grid) {
        Cell[][] cells = new Cell[grid.length][grid.length];
        for (byte y = 0; y < grid.length; y++) {
            final int row = grid[y];
            for (byte x = 0; x < grid.length; x++) {
                // Shift the int to the right so that the first bit is the x'th bit
                // Extract the least significant bit
                // Check if it is equal to 1
                final boolean isBomb = ((row >> x) & 1) == 1;
                final Cell cell = new Cell(x, y, isBomb);
                cells[y][x] = cell;
            }
        }
        return cells;
    }
}

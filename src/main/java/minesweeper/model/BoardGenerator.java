package minesweeper.model;

import java.util.Random;

public class BoardGenerator {
    private BoardGenerator() {}

    // each element is a row, and the bits in the element correspond to 0 = no bomb
    // 1 = bomb
    // the first square in the row is the last bit in the row int
    private static long[] generateGrid(final int rowCount, final int colCount, final double bombChance) {
        if (colCount > 64 || rowCount > 64) {
            throw new IllegalArgumentException("Too many rows/columns max 64");
        }
        if (bombChance <= 0 || bombChance >= 1) {
            throw new IllegalArgumentException("bombChance must be between 0 and 1");
        }
        final Random rand = new Random();
        long[] rows = new long[rowCount + 1];
        rows[0] = colCount;
        for (int y = 1; y < rows.length; y++) {
            long row = 0;
            for (int x = 0; x < colCount; x++) {
                if (rand.nextDouble() < bombChance) {
                    // Shifts 1 'x' bits to the left
                    // Use bitwise or to make that bit 1 in 'row'
                    row |= (1L << x);
                }
            }
            rows[y] = row;
        }
        return rows;
    }

    private static Cell[][] generateCells(final long[] grid) {
        final int colCount = (int) grid[0];
        final int rowCount = grid.length - 1;

        Cell[][] cells = new Cell[rowCount][colCount];

        for (int y = 1; y < grid.length; y++) {
            final long row = grid[y];
            for (int x = 0; x < colCount; x++) {
                // Shift the int to the right so that the first bit is the x'th bit
                // Extract the least significant bit
                // Check if it is equal to 1
                final boolean isBomb = ((row >> x) & 1L) == 1;
                final Cell cell = new Cell(x, y, isBomb);
                cells[y-1][x] = cell;
            }
        }
        
        // Calculate nearby bombs
        for (int y = 0; y < rowCount; y++) {
            for (int x = 0; x < colCount; x++) { 
                if (!cells[y][x].isBomb()) continue;
                for (int i = -1; i <= 1; i++) {

                    final int increment = i == 0 ? 2 : 1; // make sure to skip (0,0)
                    for (int j = -1; j <= 1; j += increment) {
                        final int new_x = x + i;
                        final int new_y = y + j;

                        if (new_x < 0 || new_x >= colCount) continue;
                        if (new_y < 0 || new_y >= rowCount) continue;

                        cells[new_y][new_x].incrementNearbyBombs();
                    }
                }
            }
        }
        return cells;
    }

    public Cell[][] generateCells(final int rowCount, final int colCount, final double bombChance) {
        return generateCells(generateGrid(rowCount, colCount, bombChance));
    }
}

package minesweeper.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardGenerator {
    private BoardGenerator() {
    }

    private static int[] generateBitGrid(final int rowCount, final int colCount, int bombCount) {
        if (colCount < 1 || rowCount < 1) {
            throw new IllegalArgumentException("colCount and rowCount must be greater than 0");
        }
        if (colCount > 128 || rowCount > 128) {
            throw new IllegalArgumentException("Too many rows/columns max 128");
        }
        if (bombCount < 1) {
            throw new IllegalArgumentException("bombCount must be between greater than 0");
        }

        final Random rand = new Random();
        final int arrayCount = (int) Math.ceil((double) rowCount * colCount / 32);
        int[] data = new int[arrayCount + 1];

        data[0] = (colCount << 16) | rowCount; // store metadata as first int

        int nextInt = 0;
        int nextIndex = 1;
        for (int y = 0; y < rowCount; y++) {
            for (int x = 0; x < colCount; x++) {
                final int cellCount = y * colCount + x;
                nextInt <<= 1;

                if (rand.nextDouble() <= (double) bombCount / (rowCount * colCount - cellCount)) {
                    nextInt += 1;
                    bombCount--;
                }

                if ((cellCount % 32) == 31) {
                    data[nextIndex] = nextInt;
                    nextInt = 0;
                    nextIndex++;
                }
            }
        }
        if (nextInt != 0) {
            data[nextIndex] = nextInt;
        }

        return data;
    }

    private static Cell[][] generateCells(final int[] bitGrid) {
        final int metadata = bitGrid[0];
        final int colCount = metadata >>> 16; // get first 16 bits (from left)
        final int rowCount = metadata & 0xFFFF; // get last 16 bits (from left)

        final Cell[][] cells = new Cell[rowCount][colCount];
        final List<Integer> bombs = new ArrayList<>();

        int nextInt = bitGrid[1];
        for (int y = 0; y < rowCount; y++) {
            for (int x = 0; x < colCount; x++) {
                // Extract the least significant bit
                // Check if it is equal to 1
                final boolean isBomb = ((nextInt >>> 31) & 1L) == 1;
                final Cell cell = new Cell(x, y, isBomb);
                cells[y][x] = cell;

                if (isBomb) {
                    bombs.add(x);
                    bombs.add(y);
                }

                final int cellCount = y * colCount + x;
                if ((cellCount % 32) == 31) {
                    nextInt = bitGrid[(cellCount+1) / 32 + 1];
                } else {
                    nextInt <<= 1;
                }
            }
        }

        // Calculate nearby bombs
        for (int n = 0; n < bombs.size(); n += 2) {
            final int x = bombs.get(n);
            final int y = bombs.get(n + 1);

            for (int i = -1; i <= 1; i++) {
                final int increment = i == 0 ? 2 : 1; // make sure to skip (0,0)
                for (int j = -1; j <= 1; j += increment) {
                    final int new_x = x + i;
                    final int new_y = y + j;

                    if (new_x < 0 || new_x >= colCount)
                        continue;
                    if (new_y < 0 || new_y >= rowCount)
                        continue;

                    cells[new_y][new_x].incrementNearbyBombs();
                }
            }
        }
        return cells;
    }

    public Cell[][] generateCells(final int rowCount, final int colCount, final int bombCount) {
        return generateCells(generateBitGrid(rowCount, colCount, bombCount));
    }
}

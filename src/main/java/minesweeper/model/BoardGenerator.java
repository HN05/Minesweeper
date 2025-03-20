package minesweeper.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Public funcs use short for colCount and rowCount to ensure correct size
// Private funcs use int for efficiency
public final class BoardGenerator {
    private BoardGenerator() {
    }

    private static Random rand = new Random();

    private static int arrayCount(final int rowCount, final int colCount) {
        // Metadata takes four bytes (2 for each short)
        // Calculate total amount of cells
        // Each byte can store 8 cells
        // Round up
        return 4 + (int) Math.ceil((double) rowCount * colCount / 8);
    }

    private static void storeMetadata(byte[] array, final int rowCount, final int colCount) {
        // Store first 8 bits of colCount
        array[0] = (byte) (colCount >>> 8);
        // Store last 8 bits of colCount
        array[1] = (byte) colCount;
        // Same for rowCount
        array[2] = (byte) (rowCount >>> 8);
        array[3] = (byte) rowCount;
    }

    @FunctionalInterface
    private interface BombCalc {
        public boolean isBomb(int x, int y, int bombCount);
    }

    private static byte[] byteGridHelper(final int rowCount, final int colCount, final BombCalc bombCalc) {
        byte[] data = new byte[arrayCount(rowCount, colCount)];
        storeMetadata(data, rowCount, colCount);

        byte nextByte = 0;
        int nextIndex = 4; // start after metadata
        int bombCount = 0;
        for (int y = 0; y < rowCount; y++) {
            for (int x = 0; x < colCount; x++) {
                nextByte <<= 1; // shift all bits to left to make room for new bit

                if (bombCalc.isBomb(x, y, bombCount)) {
                    // If bomb make the last bit a one
                    nextByte += 1;
                    bombCount++;
                }

                final int cellCount = y * colCount + x;
                // Triggers every 8 bits (when byte is full)
                if ((cellCount % 8) == 7) {
                    data[nextIndex] = nextByte;
                    nextByte = 0;
                    nextIndex++;
                }
            }
        }
        if (nextByte != 0) {
            data[nextIndex] = nextByte;
        }

        return data;
    }

    public static byte[] generateByteGrid(final short rowCount, final short colCount, final int totalBombCount) {
        if (colCount < 1 || rowCount < 1) {
            throw new IllegalArgumentException("colCount and rowCount must be greater than 0");
        }
        if (totalBombCount < 1) {
            throw new IllegalArgumentException("bombCount must be between greater than 0");
        }

        return byteGridHelper(rowCount, colCount, (x, y, bombCount) -> {
            final int remainingCells = rowCount*colCount - (y*colCount + x);
            return rand.nextDouble() <= (double) (totalBombCount - bombCount) / remainingCells;
        });
    }

    public static Cell[][] generateCells(final byte[] byteGrid) {
        final short colCount = (short) (byteGrid[0] << 8 + byteGrid[1]);
        final short rowCount = (short) (byteGrid[2] << 8 + byteGrid[3]);

        final Cell[][] cells = new Cell[rowCount][colCount];

        // Stores coordinates of bombs for later calc
        // Each bomb gets two entries, one for x and one for y
        // Example: [x1, y1, x2, y2, x3, y3]
        final List<Integer> bombs = new ArrayList<>();

        byte workingByte = byteGrid[4]; // start at 4 due to metadata
        for (int y = 0; y < rowCount; y++) {
            for (int x = 0; x < colCount; x++) {
                // Move the first bit into last place
                // Extract the least significant bit
                // Check if it is equal to 1
                final boolean isBomb = ((workingByte >>> 7) & 1L) == 1;
                final Cell cell = new Cell(x, y, isBomb);
                cells[y][x] = cell;

                if (isBomb) {
                    bombs.add(x);
                    bombs.add(y);
                }

                final int cellCount = y * colCount + x;
                // triggers every 8 bits when byte has been calculated
                if ((cellCount % 8) == 7) {
                    // +1 to cellCount since it is 0-indexed
                    // +4 for metadata
                    workingByte = byteGrid[(cellCount + 1) / 8 + 4];
                } else {
                    // Move next bit to first position 
                    workingByte <<= 1;
                }
            }
        }

        // Calculate nearby bombs
        for (int n = 0; n < bombs.size(); n += 2) {
            final int x = bombs.get(n);
            final int y = bombs.get(n + 1);

            // Needs to update all cells in 3x3 grid except for itself
            // Moves in -1 or 1 in every direction
            // Can think of it like coordinate system with start at -1 and end at 1
            for (int i = -1; i <= 1; i++) {
                final int increment = i == 0 ? 2 : 1; // make sure to skip (0,0)
                for (int j = -1; j <= 1; j += increment) {
                    final int new_x = x + i;
                    final int new_y = y + j;

                    // Out of bounds checks
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

    public static Cell[][] generateCells(final short rowCount, final short colCount, final int bombCount) {
        return generateCells(generateByteGrid(rowCount, colCount, bombCount));
    }

    public static byte[] convertToBytes(final Cell[][] cells) {
        final int rowCount = cells.length;
        final int colCount = cells[0].length;

        return byteGridHelper(rowCount, colCount, (x, y, z) -> cells[y][x].isBomb());
    }
}

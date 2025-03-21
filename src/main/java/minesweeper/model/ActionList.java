package minesweeper.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
        this.actions = new ArrayList<>();
        actions.add((byte) (colSize >>> 8));
        actions.add((byte) (colSize));
        actions.add((byte) (rowSize >>> 8));
        actions.add((byte) (rowSize));

        this.bitColSize = requiredSize(colSize);
        this.bitRowSize = requiredSize(rowSize);
        this.chunkSize = bitRowSize + bitColSize + 1;
    }

    public ActionList(final byte[] byteData) {
        bitColSize = requiredSize((usign(byteData[0]) << 8) + usign(byteData[1]));
        bitRowSize = requiredSize((usign(byteData[2]) << 8) + usign(byteData[3]));
        chunkSize = bitRowSize + bitColSize + 1;
        actions = new ArrayList<>();
        for (byte element : byteData) {
            actions.add(element);
        }
    }
    
    // returns an int that is unsigned
    private int usign(final byte val) {
        return val & 0xFF; // 0xFF = 1111 1111
    }

    private int requiredSize(final int value) {
        int pow = 0;
        int val = 1;
        while (val < value) {
            pow++;
            val <<= 1;
        }
        return pow;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void addAction(final Action action) {
        // First 32 bits (4*8) are metadata
        int bitNum = 32 + chunkSize * chunkCount;
        int left = chunkSize;
        while (left > 0) {
            final int num = bitNum % 8; // Pos in the byte
            final int byteIndex = bitNum / 8; // Index of the byte

            // check if need to add byte
            if (num == 0) {
                actions.add((byte) 0);
            }
            byte val = actions.get(byteIndex);
            val <<= 1; // Make room for bit
            int bit = 0;

            if (left == chunkSize) {
                // Bit is type
                bit = action.type().isMark() ? 1 : 0;
            } else if (left > bitRowSize) {
                // Bit is part of x coord
                bit = action.x() >>> bitColSize - (chunkSize - left);
            } else {
                // Bit is part of y coord
                bit = action.y() >>> left - 1 - bitRowSize;
            }
            // left shift bit to align
            // add the bit val (use bitwise or for safety)
            val |= (bit) << (7 - num);

            actions.set(byteIndex, val);

            left--;
            bitNum++;
        }
        chunkCount++;
    }

    public byte[] getByteData() {
        byte[] data = new byte[actions.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = actions.get(i);
        }
        return data;
    }

    private int extract(final int startBit, final int length) {
        int result = 0;
        int byteIndex = startBit / 8;
        int bitOffset = startBit % 8;
        int bitsRemaining = length;

        while (bitsRemaining > 0) {
            // make sure it is unsigned
            final int currentByte = usign(actions.get(byteIndex));
            final int bitsAvailable = 8 - bitOffset;
            final int bitsToExtract = Math.min(bitsRemaining, bitsAvailable);
            final int mask = (1 << bitsToExtract) - 1;
            // Align the bits to the right end and mask off unwanted bits.
            final int bits = (currentByte >>> (bitsAvailable - bitsToExtract)) & mask;
            result = (result << bitsToExtract) | bits;

            bitsRemaining -= bitsToExtract;
            bitOffset = 0; // only first byte can have an offset
            byteIndex++;
        }
        return result;
    }

    public void forEachAction(Consumer<Action> consumer) {
        for (int i = 0; i < chunkCount; i++) {
            int bitNum = 32 + i * chunkSize;
            final boolean isMark = extract(bitNum, 1) == 1;
            bitNum++;

            final int x = extract(bitNum, bitColSize);
            bitNum += bitColSize;
            final int y = extract(bitNum, bitRowSize);

            final Action action = new Action(x, y, ActionType.get(isMark));
            consumer.accept(action);
        }
    }
}

record Action(int x, int y, ActionType type) {
};

enum ActionType {
    MARK, REVEAL;

    public boolean isMark() {
        return this == ActionType.MARK;
    }

    public static ActionType get(final boolean isMark) {
        return isMark ? ActionType.MARK : ActionType.REVEAL;
    }
}

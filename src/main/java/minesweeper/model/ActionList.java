package minesweeper.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ActionList {
    private final List<Byte> actions;
    private final int yBitSize;
    private final int xBitSize;
    private final int actionBitSize;
    private int actionCount = 0;

    public ActionList(final short colSize, final short rowSize) {
        this.actions = new ArrayList<>();
        this.xBitSize = requiredSize(colSize);
        this.yBitSize = requiredSize(rowSize);
		actions.add((byte) xBitSize);
		actions.add((byte) yBitSize);
        this.actionBitSize = yBitSize + xBitSize + 1;
    }

    public ActionList(final byte[] byteData) {
		xBitSize = usign(byteData[0]);
		yBitSize = usign(byteData[1]);
        actionBitSize = yBitSize + xBitSize + 1;
        actions = new ArrayList<>();
        for (byte element : byteData) {
            actions.add(element);
        }
		actionCount = (byteData.length * 8 - 16) / actionBitSize;
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

    public int getActionBitSize() {
        return actionBitSize;
    }

	public int getActionCount() {
		return actionCount;
	}

    public void addAction(final Action action) {
        // First 16 bits (2*8) are metadata
        int bitNum = 16 + actionBitSize * actionCount;
        int actionIndex = 0;
        while (actionIndex < actionBitSize) {
            final int num = bitNum % 8; // Pos in the byte
            final int byteIndex = bitNum / 8; // Index of the byte

            // check if need to add byte
            if (num == 0) {
                actions.add((byte) 0);
            }
            byte val = actions.get(byteIndex);
            int bit = 0;

            if (actionIndex == 0) {
                // Bit is type
                bit = action.type().isMark() ? 1 : 0;
            } else if (actionIndex <= xBitSize) {
                // Bit is part of x coord
                bit = action.x() >>> (xBitSize - actionIndex);
            } else {
                // Bit is part of y coord
                bit = action.y() >>> ((yBitSize - (actionIndex - xBitSize)));
            }
            // left shift bit to align
            // add the bit val (use bitwise or for safety)
            val |= (bit & 1) << (7 - num);

            actions.set(byteIndex, val);

            actionIndex++;
            bitNum++;
        }
        actionCount++;
    }

    public byte[] getByteData() {
        byte[] data = new byte[actions.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = actions.get(i);
        }
        return data;
    }

    // length should be 1-32, not validating since private func
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
        for (int i = 0; i < actionCount; i++) {
            int bitNum = 16 + i * actionBitSize;
            final boolean isMark = extract(bitNum, 1) == 1;
            bitNum++;

            final int x = extract(bitNum, xBitSize);
            bitNum += xBitSize;
            final int y = extract(bitNum, yBitSize);

            final Action action = new Action(x, y, ActionType.get(isMark));
            consumer.accept(action);
        }
    }
}

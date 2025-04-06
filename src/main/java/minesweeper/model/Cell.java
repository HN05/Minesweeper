package minesweeper.model;

public final class Cell {
    private final int x;
    private final int y;
    private final boolean isBomb;
    private int nearbyBombs = 0; // calculated later
    private boolean isRevealed = false;
    private boolean isMarked = false;

    public Cell(final int x, final int y, final boolean isBomb) {
        this.x = x;
        this.y = y;
        this.isBomb = isBomb;
    }

    // Should only be used by BoardGenerator class
    void incrementNearbyBombs() {
        nearbyBombs++;
    }

    // Should only be used by Game class
    void revealCell() {
        isRevealed = true;
    }

    // Should only be used by Game class
    void markCell() {
        isMarked = true;
    }

    // Should only be used by Game class
	void unMarkCell() {
		isMarked = false;
	}

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isBomb() {
        return isBomb;
    }

    public int getNearbyBombs() {
        return nearbyBombs;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public boolean isMarked() {
        return isMarked;
    }
}

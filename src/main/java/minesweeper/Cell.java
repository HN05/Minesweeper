package minesweeper;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class Cell {
    private final boolean isBomb;
    private boolean isRevealed = false;
    private byte x;
    private byte y;
    private Button button = null;

    public Cell(final byte x, final byte y, final boolean isBomb) {
        this.x = x;
        this.y = y;
        this.isBomb = isBomb;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public boolean isBomb() {
        return isBomb;
    }

    private void revealCell() {
        isRevealed = true;
        if (isBomb) {
            button.setText("💣");
            button.setStyle("-fx-background-color: red;");
        } else {
            button.setText("0"); // You can calculate nearby bombs later
            button.setStyle("-fx-background-color: lightgray;");
        }
    }

    public void render(GridPane grid, final byte size) {
        if (grid == null) {
            throw new IllegalArgumentException("Grid can not be null");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }

        Button button = new Button();
        this.button = button;
        button.setMinSize(size, size); // Set button size
        button.setOnAction(e -> revealCell());

        grid.add(button, x, y);
    }

}

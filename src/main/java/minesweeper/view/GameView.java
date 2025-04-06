package minesweeper.view;

import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import minesweeper.model.Action;
import minesweeper.model.ActionType;
import minesweeper.model.Board;

public class GameView {
	private boolean isMarking = false;

	public void toggleMarking() {
		isMarking = !isMarking;
	}

	private int getGridSize(final GridPane grid) {
		final double width = grid.getWidth();
		final double height = grid.getHeight();
		return 10;
	}

	public void renderGrid(final GridPane grid, final Board board, final Consumer<Action> performAction) {
		grid.getChildren().clear();
		final int gridSize = getGridSize(grid);
		for (int y = 0; y < board.getRowCount(); y++) {
			for (int x = 0; x < board.getColCount(); x++) {
				final Button button = new Button();
				button.setPrefSize(gridSize, gridSize);
				final Action action = new Action(x, y, isMarking ? ActionType.MARK : ActionType.REVEAL);
				button.setOnAction(e -> performAction.accept(action));
				grid.add(button, x, y);
			}
		}
	}
}

package minesweeper;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import minesweeper.model.Board;
import minesweeper.model.BoardGenerator;
import minesweeper.model.Cell;
import minesweeper.model.Game;
import minesweeper.model.GameListener;

public class MinesweeperController implements GameListener {

	private Game game = new Game(new Board(BoardGenerator.generateCells((short) 12, (short) 12, 12)));
	private boolean isMarking = false;

	@FXML
	private GridPane grid;

	@FXML
	private Label bombCounter;

	@FXML
	private void handleToggleFlagMode() {
		isMarking = !isMarking;
	}

	private int getGridSize() {
		final double width = grid.getWidth();
		final double height = grid.getHeight();
		return 10;
	}

	private void renderGrid() {
		grid.getChildren().clear();
		final int gridSize = getGridSize();
		final Board board = game.getBoard();
		for (int y = 0; y < board.getRowCount(); y++) {
			for (int x = 0; x < board.getColCount(); x++) {
				final Button button = new Button();
				button.setPrefSize(gridSize, gridSize);
				final int x_loc = x;
				final int y_loc = y;
				button.setOnAction(e -> handleClick(x_loc, y_loc));
				grid.add(button, x, y);
			}
		}
	}

	@FXML
	private void initialize() {
		renderGrid();
	}

	private void handleClick(final int x, final int y) {
		if (isMarking) {
			game.mark(x, y);
		} else {
			game.reveal(x, y);
		} 
	}

	@Override
	public void updatedCell(final Cell cell) {
		renderGrid();
	}

	@Override
	public void updatedGameState() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'updatedState'");
	}

}

package minesweeper;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import minesweeper.model.Board;
import minesweeper.model.BoardGenerator;
import minesweeper.model.Cell;
import minesweeper.model.Game;
import minesweeper.model.GameListener;
import minesweeper.view.GameView;

public class MinesweeperController implements GameListener {

	private Game game = new Game(new Board(BoardGenerator.generateCells((short) 12, (short) 12, 12)));
	private GameView gameView = null;

	@FXML
	private GridPane grid;

	@FXML
	private Label bombCounter;

	@FXML
	private void handleToggleFlagMode() {
		if (gameViewExists()) {
			gameView.toggleMarking();
		}
	}

	@FXML
	private void initialize() {
		render();
	}

	private void render() {
		if (!gameViewExists()) {
			return;
		}
		gameView.renderGrid(grid, game.getBoard(), game::action);
	}

	private boolean gameViewExists() {
		return gameView != null;
	}

	@Override
	public void updatedCell(final Cell cell) {
		render();
	}

	@Override
	public void updatedGameState() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'updatedState'");
	}

}

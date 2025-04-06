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
	private GameView gameView = new GameView();

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
		grid.sceneProperty().addListener((obs, oldScene, newScene) -> {
			if (newScene != null) {
				newScene.widthProperty().addListener((o, ov, nv) -> render());
				newScene.heightProperty().addListener((o, ov, nv) -> render());
				grid.layoutBoundsProperty().addListener((o, oldVal, newVal) -> render());
			}
		});
	}

	private void render() {
		if (!gameViewExists()) {
			return;
		}
		gameView.renderGrid(grid, game.getBoard(), game::action);
		gameView.renderBombCount(bombCounter, game.getBombsLeft());
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

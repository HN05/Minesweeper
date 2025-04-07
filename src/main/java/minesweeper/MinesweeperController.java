package minesweeper;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import minesweeper.model.Board;
import minesweeper.model.BoardGenerator;
import minesweeper.model.Cell;
import minesweeper.model.Game;
import minesweeper.model.GameListener;
import minesweeper.view.GameView;

public class MinesweeperController implements GameListener {

	private Game game = null;
	private GameView gameView = null;

	@FXML
	private GridPane grid;

	@FXML
	private Button modeSwitch;

	@FXML
	private Label bombCounter;

	@FXML
	private void handleToggleFlagMode() {
		if (gameViewExists()) {
			gameView.toggleMarking();
			render();
		}
	}

	@FXML
	private void handleExitGame() {
		exitGame();
	}

	@FXML
	private void initialize() {
		final Game test = new Game(new Board(BoardGenerator.generateCells((short) 12, (short) 12, 12)));
		initGame(test);
	}

	private void initGame(final Game game) {
		this.game = game;
		this.gameView = new GameView();
		game.addListener(this);
		grid.sceneProperty().addListener((obs, oldScene, newScene) -> {
			if (newScene != null) {
				// renders when resizing
				newScene.widthProperty().addListener((o, ov, nv) -> render());
				newScene.heightProperty().addListener((o, ov, nv) -> render());
				// renders on appearing
				grid.layoutBoundsProperty().addListener((o, oldVal, newVal) -> render());
			}
		});
	}

	private boolean gameViewExists() {
		return gameView != null;
	}

	private void render() {
		if (!gameViewExists()) {
			return;
		}
		gameView.renderGrid(grid, game.getBoard(), game.getActionCount(), (action, count) -> {
			// Hinders double clicking
			if (count == game.getActionCount()) {
				game.action(action);
			}
		});
		gameView.renderBombCount(bombCounter, game.getBombsLeft());
		gameView.renderModeSwitch(modeSwitch);
	}

	private void storeGame() {
		if (game == null) {
			return;
		}
		try {
			FileStorage.storeGame(game);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void exitGame() {
		if (!game.isFinished()) {
			storeGame();
		}
		game = null;
		gameView = null;
	}

	@Override
	public void updatedCell(final Cell cell) {
		// have cell here in future for potentially more efficient rendering
		render();
		storeGame();
	}

	@Override
	public void updatedGameState() {
		if (game.isFinished()) {
			if (game.hasLost()) {
				gameView.renderLossScreen();
			} else {
				gameView.renderWinScreen();
			}

			FileStorage.deleteGame(game);
			exitGame();
		}
	}
}

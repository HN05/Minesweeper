package minesweeper;

import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import minesweeper.model.Board;
import minesweeper.model.BoardGenerator;
import minesweeper.model.Cell;
import minesweeper.model.Game;
import minesweeper.model.GameListener;
import minesweeper.view.GameSelectView;
import minesweeper.view.GameView;

public class MinesweeperController implements GameListener {

	private Game game = null;
	private Board board = null;
	private GameView gameView = null;
	private GameSelectView gameSelectView = null;

	@FXML
	private VBox gameSelectBox;

	@FXML
	private Label titleLabel;

	@FXML
	private Label errorLabel;

	@FXML
	private VBox selectContainer;

	@FXML
	private VBox gameBox;

	@FXML
	private GridPane grid;

	@FXML
	private Button modeSwitch;

	@FXML
	private Label bombCounter;

	@FXML
	private Label endMessage;

	@FXML
	private VBox endScreen;

	@FXML
	private void handleToggleFlagMode() {
		if (gameViewExists()) {
			gameView.toggleMarking();
			render();
		}
	}

	@FXML
	private void handleExitGame() {
		if (gameViewExists()) {
			exitGame();
			render();
		}
	}

	@FXML
	private void handlePlayAgain() {
		endScreen.setVisible(false);
		Objects.requireNonNull(board);
		board.reset();
		initGame(new Game(board));
		render();
	}

	@FXML
	private void handleNewBoard() {
		endScreen.setVisible(false);
		getNewBoard();
		initGame(new Game(board));
		render();
	}

	@FXML
	private void initialize() {
		// renders on appear
		gameBox.sceneProperty().addListener((obs, oldScene, newScene) -> {
			if (newScene != null) {
				// renders when resizing
				newScene.widthProperty().addListener((o, ov, nv) -> render());
				newScene.heightProperty().addListener((o, ov, nv) -> render());
				// renders on appearing
				gameBox.layoutBoundsProperty().addListener((o, oldVal, newVal) -> render());
			}
		});
	}

	private void getNewBoard() {
		// set config for new boards here, could be ui in future
		board = new Board(BoardGenerator.generateCells((short) 12, (short) 12, 12));
	}

	private void renderSelectGame() {
		if (gameSelectView == null) {
			gameSelectView = new GameSelectView();
			gameBox.setVisible(false);
			gameSelectBox.setVisible(true);
		}
		if (board == null) {
			final int[] boards = FileStorage.fetchBoardIDs();
			gameSelectView.renderSelectBoard(boards, titleLabel, selectContainer, this::fetchBoard);
		} else {
			final String[] games = FileStorage.fetchGamesNames(board.getID());
			gameSelectView.renderSelectGame(games, titleLabel, selectContainer, this::fetchGame);
		}
		gameSelectView.renderErrorLabel(errorLabel);
	}

	private void fetchBoard(final Integer boardID) {
		if (boardID == null) {
			getNewBoard();
		} else {
			try {
				this.board = FileStorage.fetchBoard(boardID);
			} catch (IOException e) {
				e.printStackTrace();
				gameSelectView.setError(e.toString());
			}
		}
		render();
	}

	private void fetchGame(final String gameName) {
		Objects.requireNonNull(board);
		Game game = null;
		if (gameName == null) {
			game = new Game(board);
		} else {
			try {
				game = FileStorage.fetchGame(gameName, board.getID());
			} catch (IOException e) {
				e.printStackTrace();
				gameSelectView.setError(e.toString());
			}
		}
		initGame(game);
		render();
	}

	private void initGame(final Game game) {
		if (game == null) {
			return;
		}
		gameSelectView = null;
		this.game = game;
		this.gameView = new GameView();
		game.addListener(this);
	}

	private boolean gameViewExists() {
		return gameView != null;
	}

	private void render() {
		if (!gameViewExists()) {
			renderSelectGame();
			return;
		}
		renderGameView();
	}

	private void renderGameView() {
		gameBox.setVisible(true);
		gameSelectBox.setVisible(false);
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
		board = null;
		gameView = null;
	}

	@Override
	public void updatedCell(final Cell cell) {
		// have cell here in future for potentially more efficient rendering
		if (!game.pendingActions()) {
			render();
			storeGame();
		}
	}

	@Override
	public void updatedGameState() {
		if (game.isFinished()) {
			gameView.renderEndScreen(game, endMessage, endScreen);
			FileStorage.deleteGame(game);
			exitGame();
		}
	}
}

package minesweeper.view;

import java.net.URL;
import java.util.function.BiConsumer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import minesweeper.MinesweeperApp;
import minesweeper.model.Action;
import minesweeper.model.ActionType;
import minesweeper.model.Board;
import minesweeper.model.Cell;
import minesweeper.model.Game;

public class GameView {
	private boolean isMarking;
	private Image flag;
	private Image bomb;

	public GameView() {
		isMarking = false;
		final URL flagURL = MinesweeperApp.class.getResource("flag.png");
		flag = new Image(flagURL.toExternalForm());
		final URL bombURL = MinesweeperApp.class.getResource("bomb.png");
		bomb = new Image(bombURL.toExternalForm());
	}

	public void toggleMarking() {
		isMarking = !isMarking;
	}

	private int getGridSize(final GridPane grid, final Board board) {
		final double width = grid.getWidth();
		final double height = grid.getHeight();
		return (int) Math.min(width / board.getColCount(), height / board.getRowCount());
	}

	public void renderBombCount(final Label label, final int count) {
		label.setText("Bombs left: " + count);
	}

	public void renderModeSwitch(final Button button) {
		final String background = isMarking ? "green" : "red";
		button.setStyle("-fx-background-color:" + background + "; -fx-text-fill: white;");
	}

	private void addImage(final Image img, final Button button) {
		ImageView imgView = new ImageView(img);
		imgView.setFitWidth(button.getPrefWidth());
		imgView.setFitHeight(button.getPrefHeight());
		button.setPadding(Insets.EMPTY);
		button.setGraphic(imgView);
	}

	private void renderCell(final Cell cell, final Button button) {
		if (cell.isMarked()) {
			addImage(flag, button);
		} else if (cell.isRevealed()) {
			button.setDisable(true);
			if (cell.isBomb()) {
				addImage(bomb, button);
			} else {
				button.setText(Integer.toString(cell.getNearbyBombs()));
			}
		}
	}

	public void renderGrid(final GridPane grid, final Board board, final int actionCount, final BiConsumer<Action, Integer> performAction) {
		grid.getChildren().clear();
		final int gridSize = getGridSize(grid, board);
		for (int y = 0; y < board.getRowCount(); y++) {
			for (int x = 0; x < board.getColCount(); x++) {
				final Button button = new Button();
				button.setPrefSize(gridSize, gridSize);
				button.setStyle("-fx-background-radius: 0;");
				final Action action = new Action(x, y, ActionType.get(isMarking));
				button.setOnAction(e -> performAction.accept(action, actionCount));
				renderCell(board.get(x, y), button);
				grid.add(button, x, y);
			}
		}
	}

	public void renderEndScreen(final Game game, final Label label, final VBox vbox) {
		final String message = game.hasLost() ? "You Lost!" : "You Won!";
		label.setText(message);
		vbox.setVisible(true);
	}
}

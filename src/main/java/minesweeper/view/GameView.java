package minesweeper.view;

import java.net.URL;
import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import minesweeper.MinesweeperApp;
import minesweeper.model.Action;
import minesweeper.model.ActionType;
import minesweeper.model.Board;
import minesweeper.model.Cell;

public class GameView {
	private boolean isMarking;
	private Image flag;

	public GameView() {
		isMarking = true;
		final URL flagURL = MinesweeperApp.class.getResource("flag.png");
		flag = new Image(flagURL.toExternalForm());
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
		label.setText("Bombs: " + count);
	}

	private void renderCell(final Cell cell, final Button button) {
		button.setGraphic(null);
		button.setText("");
		if (cell.isMarked()) {
			ImageView flagView = new ImageView(flag);
			flagView.setFitWidth(button.getPrefWidth());
			flagView.setFitHeight(button.getPrefHeight());
			button.setGraphic(flagView);
		} else if (cell.isRevealed()) {
			button.setText(Integer.toString(cell.getNearbyBombs()));
			button.setDisable(true);
		}
	}

	public void renderGrid(final GridPane grid, final Board board, final Consumer<Action> performAction) {
		grid.getChildren().clear();
		final int gridSize = getGridSize(grid, board);
		System.out.println("Gridsize: " + gridSize);
		for (int y = 0; y < board.getRowCount(); y++) {
			for (int x = 0; x < board.getColCount(); x++) {
				final Button button = new Button();
				button.setPrefSize(gridSize, gridSize);
				final Action action = new Action(x, y, isMarking ? ActionType.MARK : ActionType.REVEAL);
				button.setOnAction(e -> performAction.accept(action));
				renderCell(board.get(x, y), button);
				grid.add(button, x, y);
			}
		}
	}
}

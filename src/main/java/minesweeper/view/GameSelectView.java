package minesweeper.view;

import java.util.Arrays;
import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class GameSelectView {

	private String error = null;

	public void renderSelectBoard(final int[] boards, final Label title, final VBox container,
			final Consumer<Integer> accept) {
		container.getChildren().clear();
		title.setText("Select a Board");
		Arrays.sort(boards);
		// Create a button for each board ID.
		for (int boardID : boards) {
			Button btn = new Button("Board " + boardID);
			btn.setOnAction(e -> accept.accept(boardID));
			container.getChildren().add(btn);
		}
		// Option to choose a new board.
		Button newBoardBtn = new Button("New Board");
		newBoardBtn.setOnAction(e -> accept.accept(null));
		container.getChildren().add(newBoardBtn);
	}

	public void renderSelectGame(final String[] games, final Label title, final VBox container,
			final Consumer<String> accept) {
		Arrays.sort(games);
		container.getChildren().clear();
		title.setText("Select a Game");
		// Create a button for each game.
		for (String gameName : games) {
			Button btn = new Button(gameName);
			btn.setOnAction(e -> accept.accept(gameName));
			container.getChildren().add(btn);
		}
		// Option to start a new game.
		Button newGameBtn = new Button("New Game");
		newGameBtn.setOnAction(e -> accept.accept(null));
		container.getChildren().add(newGameBtn);
		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> accept.accept("EXIT"));
		container.getChildren().add(exitBtn);
	}

	public void renderErrorLabel(final Label label) {
		if (error == null) {
			label.setText(error);
		}
	}

	public void setError(final String error) {
		this.error = error;
	}
}

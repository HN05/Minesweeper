package minesweeper;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

	@Override
	public void updatedCell(final Cell cell) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'updatedCell'");
	}

	@Override
	public void updatedGameState() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'updatedState'");
	}

}

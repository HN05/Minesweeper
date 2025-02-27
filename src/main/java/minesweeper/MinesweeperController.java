package minesweeper;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import minesweeper.model.Cell;

public class MinesweeperController implements GameListener {
    @FXML
    private TextField firstNumber, secondNumber, operator;

    @FXML
    private Label result;

    @FXML
    private void handleButtonClick() {
        /* initCalculator(operator.getText());
        try {
            int result = calculator.calculate(Integer.parseInt(firstNumber.getText()),
                    Integer.parseInt(secondNumber.getText()));
            this.result.setText(firstNumber.getText() + " " + operator.getText() + " " + secondNumber.getText() + " = "
                    + String.valueOf(result));
        } catch (NumberFormatException e) {
            result.setText("Et eller begge tallene er ugyldige");
        } */
    }

	@Override
	public void updatedCell(Cell cell) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'updatedCell'");
	}

	@Override
	public void updatedGameState() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'updatedState'");
	}

}

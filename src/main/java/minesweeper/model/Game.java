package minesweeper.model;

import java.util.Collection;
import java.util.HashSet;

public final class Game {
	private final Board board;
	private final Collection<GameListener> listeners = new HashSet<>();
	private final ActionList actionList;
	private int markCount = 0;
	private boolean isFinished = false;
	private boolean hasLost = false;

	public Game(final Board board) {
		this.board = board;
		this.actionList = new ActionList((short) board.getRowCount(), (short) board.getColCount());
	}

	public Game(final Board board, final ActionList actionList) {
		this.board = board;
		this.actionList = actionList;
		this.actionList.forEachAction(this::action);
	}

	public boolean isFinished() {
		return isFinished;
	}

	public boolean hasLost() {
		return hasLost;
	}

	public int getBombsLeft() {
		return board.getBombCount() - markCount;
	}

	public Board getBoard() {
		return board;
	}

	public ActionList getActionList() {
		return actionList;
	}

	public void addListener(final GameListener listener) {
		listeners.add(listener);
	}

	public void removeListener(final GameListener listener) {
		listeners.remove(listener);
	}

	private void updatedGameState() {
		for (GameListener listener : listeners) {
			listener.updatedGameState();
		}
	}

	private void updatedCell(final Cell cell) {
		for (GameListener listener : listeners) {
			listener.updatedCell(cell);
		}
	}

	private void checkIfWon() {
		if (getBombsLeft() != 0) {
			return;
		}
		final boolean allRevealead = board
				.flatStream()
				.filter(c -> !c.isRevealed())
				.count() == 0;

		if (allRevealead) {
			isFinished = true;
			updatedGameState();
		}
	}

	private void reveal(final Cell cell) {
		if (cell.isRevealed() || cell.isMarked())
			return;

		cell.revealCell();
		if (cell.isBomb()) {
			hasLost = true;
			isFinished = true;
			updatedGameState();
		} else {
			checkIfWon();
		}
	}

	private void mark(final Cell cell) {
		if (cell.isMarked()) {
			cell.unMarkCell();
			markCount--;
		} else {
			cell.markCell();
			markCount++;
		}
	}

	public void action(final Action action) {
		System.out.println(board.getBombCount());

		final Cell cell = board.get(action.x(), action.y());
		if (action.type().isMark()) {
			mark(cell);
			System.out.println("mark: " + action.x() + ", " + action.y());
		} else {
			reveal(cell);
			System.out.println("reveal: " + action.x() + ", " + action.y());
		}
		actionList.addAction(action);
		updatedCell(cell);
	}
}

package minesweeper.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

public final class Game {
	private final Board board;
	private final String name;
	private final Collection<GameListener> listeners = new HashSet<>();
	private final ActionList actionList;
	private int markCount = 0;
	private boolean isFinished = false;
	private boolean hasLost = false;

	public Game(final Board board) {
		this.board = board;
		this.actionList = new ActionList((short) board.getRowCount(), (short) board.getColCount());
		this.name = LocalDate.now().toString();
	}

	public Game(final Board board, final ActionList actionList, final String name) {
		this.board = board;
		this.name = name;
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

	public String getName() {
		return name;
	}

	public int getActionCount() {
		return actionList.getActionCount();
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
		}
	}

	private void revealNearby(final Cell cell) {
		for (int x = -1; x <= 1; x++) {
			final int step = x == 0 ? 2 : 1; // skip 0,0
			for (int y = -1; y <= 1; y += step) {
				final int new_x = cell.getX() + x;
				final int new_y = cell.getY() + y;
				if (!board.isValid(new_x, new_y))
					continue;
				action(new Action(new_x, new_y, ActionType.REVEAL));
			}
		}
	}

	private void reveal(final Cell cell) {
		if (cell.isRevealed() || cell.isMarked())
			return;

		cell.revealCell();
		if (cell.isBomb()) {
			hasLost = true;
			isFinished = true;
			return;
		}

		if (cell.getNearbyBombs() == 0) {
			revealNearby(cell);
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
		final Cell cell = board.get(action.x(), action.y());
		if (action.type().isMark()) {
			mark(cell);
		} else {
			reveal(cell);
		}
		actionList.addAction(action);
		updatedCell(cell);
		checkIfWon();
		if (isFinished) {
			updatedGameState();
		}
	}
}

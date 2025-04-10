package minesweeper.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.time.format.DateTimeFormatter;

public final class Game {
	private final Board board;
	private final String name;
	private final Collection<GameListener> listeners = new HashSet<>();
	private final ActionList actionList;
	private int markCount = 0;
	private boolean isFinished = false;
	private boolean hasLost = false;
	private int pendingActions = 0;

	public Game(final Board board) {
		this(board, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}

	public Game(final Board board, final String name) {
		this.board = board;
		this.name = name;
		this.actionList = new ActionList((short) board.getRowCount(), (short) board.getColCount());
	}

	public Game(final Board board, final ActionList actionList, final String name) {
		this.board = board;
		this.name = name;
		this.actionList = actionList;
		this.actionList.forEachAction(action -> action(action, false));
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

	public boolean pendingActions() {
		return pendingActions != 0;
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
		for (int dx = -1; dx <= 1; dx++) {
			final int step = dx == 0 ? 2 : 1; // skip 0,0
			for (int dy = -1; dy <= 1; dy += step) {
				final int new_x = cell.getX() + dx;
				final int new_y = cell.getY() + dy;
				if (!board.isValid(new_x, new_y))
					continue;
				action(new Action(new_x, new_y, ActionType.REVEAL));
			}
		}
	}

	private void reveal(final Cell cell, final boolean cascade) {
		if (cell.isRevealed() || cell.isMarked())
			return;

		cell.revealCell();
		if (cell.isBomb()) {
			hasLost = true;
			isFinished = true;
			return;
		}

		if (cell.getNearbyBombs() == 0 && cascade) {
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
		action(action, true);
	}

	public void action(final Action action, final boolean addToList) {
		pendingActions++;
		final Cell cell = board.get(action.x(), action.y());
		if (action.type().isMark()) {
			mark(cell);
		} else {
			reveal(cell, addToList);
		}
		if (addToList) {
			actionList.addAction(action);
		}
		pendingActions--;
		updatedCell(cell);
		checkIfWon();
		if (isFinished) {
			updatedGameState();
		}
	}
}

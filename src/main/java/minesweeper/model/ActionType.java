package minesweeper.model;

public enum ActionType {
	MARK, REVEAL;

	public boolean isMark() {
		return this == ActionType.MARK;
	}

	public static ActionType get(final boolean isMark) {
		return isMark ? ActionType.MARK : ActionType.REVEAL;
	}
}

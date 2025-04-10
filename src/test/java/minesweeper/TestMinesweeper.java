package minesweeper;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import minesweeper.model.Action;
import minesweeper.model.ActionList;
import minesweeper.model.ActionType;
import minesweeper.model.Board;
import minesweeper.model.BoardGenerator;
import minesweeper.model.Cell;
import minesweeper.model.Game;

class TestMinesweeper {

	private final int testTimes = 20; // amount of times to run each test since random inputs

	private Board generateBoard() {
		return new Board(BoardGenerator.generateCells((short) 12, (short) 12, 14));
	}

	private Game generateGame() {
		final Game game = new Game(generateBoard());
		game.action(new Action(0, 0, ActionType.MARK));
		game.action(new Action(11, 11, ActionType.MARK));
		game.action(new Action(1, 0, ActionType.REVEAL));
		return game;
	}

	private void sameCell(final Cell cell1, final Cell cell2) {
		final boolean sameCell = cell1.getX() == cell2.getX()
				&& cell1.getY() == cell2.getY()
				&& cell1.isBomb() == cell2.isBomb()
				&& cell1.isMarked() == cell2.isMarked()
				&& cell1.isRevealed() == cell2.isRevealed()
				&& cell1.getNearbyBombs() == cell2.getNearbyBombs();
		assertTrue(sameCell);
	}

	private void sameBoard(final Board board1, final Board board2) {
		assertTrue(board1.flatStream().count() == board2.flatStream().count());
		for (Cell cell : board1.flatStream().collect(Collectors.toList())) {
			assertTrue(board2.isValid(cell.getX(), cell.getY()));
			sameCell(cell, board2.get(cell.getX(), cell.getY()));
		}
	}

	private void sameActionList(final ActionList list1, final ActionList list2) {
		final byte[] bytes1 = list1.getByteData();
		final byte[] bytes2 = list2.getByteData();
		assertEquals(bytes1.length, bytes2.length);
		for (int i = 0; i < bytes1.length; i++) {
			assertEquals(bytes1[i], bytes2[i]);
		}
	}

	private void sameGame(final Game game1, final Game game2) {
		sameBoard(game1.getBoard(), game2.getBoard());
		sameActionList(game1.getActionList(), game2.getActionList());
		final boolean sameState = game1.getName().equals(game2.getName())
				&& game1.isFinished() == game2.isFinished()
				&& game1.getBombsLeft() == game2.getBombsLeft()
				&& game1.hasLost() == game2.hasLost();
		assertTrue(sameState);
	}

	@Test
	void testCell() {
		for (int i = 0; i < testTimes; i++) {
			final Game game = generateGame();
			final Cell cell = game.getBoard().get(1, 1);
			game.action(new Action(cell, ActionType.MARK));
			assertTrue(cell.isMarked());
			game.action(new Action(cell, ActionType.MARK));
			assertTrue(!cell.isMarked());
			game.action(new Action(cell, ActionType.REVEAL));
			assertTrue(cell.isRevealed());
			int nearby = cell.getNearbyBombs();
			for (int x = 0; x <= 2; x++) {
				final int step = x == 0 ? 2 : 1;
				for (int y = 0; y <= 2; y += step) {
					if (game.getBoard().get(x, y).isBomb()) {
						nearby--;
					}
				}
			}
			assertEquals(nearby, 0);
		}
	}

	@Test
	void testBoardGenerator() {

	}

	@Test
	void testBoard() {

	}

	@Test
	void testGame() {

	}

	@Test
	void testStorageGame() {
		for (int i = 0; i < testTimes; i++) {
			final Game game = generateGame();
			try {
				FileStorage.storeGame(game);
			} catch (IOException e) {
				fail("IOException was thrown when saving game: " + e.getMessage());
			}
			Game second = null;
			try {
				second = FileStorage.fetchGame(game.getName(), game.getBoard().getID());
			} catch (Exception e) {
				fail("IOException was thrown when fetching game: " + e.getMessage());
			}
			assertTrue(FileStorage.deleteGame(game));
			sameGame(game, second);
		}
	}

	@Test
	void testStorageBoard() {
		for (int i = 0; i < testTimes; i++) {
			final Board board = generateBoard();
			try {
				FileStorage.storeBoard(board);
			} catch (Exception e) {
				fail("IOException was thrown when saving board: " + e.getMessage());
			}
			Board second = null;
			try {
				second = FileStorage.fetchBoard(board.getID());
			} catch (Exception e) {
				fail("IOException was thrown when fetching board: " + e.getMessage());
			}
			assertTrue(FileStorage.deleteBoard(board));
			sameBoard(board, second);
		}
	}
}

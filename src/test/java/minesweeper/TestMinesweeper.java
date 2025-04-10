package minesweeper;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import minesweeper.model.Action;
import minesweeper.model.ActionList;
import minesweeper.model.ActionType;
import minesweeper.model.Board;
import minesweeper.model.CellGenerator;
import minesweeper.model.Cell;
import minesweeper.model.Game;

class TestMinesweeper {

	private final int testTimes = 100; // amount of times to run each test since random inputs

	private Board generateBoard() {
		return new Board(CellGenerator.generateCells((short) 12, (short) 12, 14));
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
	void testActionList() {
		for (int i = 0; i < testTimes; i++) {
			final short colSize = (short) (4 + i);
			final short rowSize = (short) (7 + i);
			final ActionList original = new ActionList(colSize, rowSize);

			List<Action> testActions = new ArrayList<>();
			testActions.add(new Action(3 + i, 5, ActionType.MARK));
			testActions.add(new Action(3, 2 + i, ActionType.REVEAL));
			testActions.add(new Action(0, 0, ActionType.MARK));
			testActions.add(new Action(2 + i, 6 + i, ActionType.REVEAL));

			for (Action action : testActions) {
				original.addAction(action);
			}

			assertEquals(testActions.size(), original.getActionCount());
		
			// test converting to byte[] and then back
			final byte[] data = original.getByteData();
			final ActionList copy = new ActionList(data);
			assertEquals(original.getActionCount(), copy.getActionCount());
			
			// extract actions from copy
			List<Action> extractedActions = new ArrayList<>();
			copy.forEachAction(extractedActions::add);

			// compare to original test actions
			assertEquals(testActions.size(), extractedActions.size());
			for (int y = 0; y < testActions.size(); y++) {
				Action orig = testActions.get(y);
				Action cop = extractedActions.get(y);
				assertEquals(orig, cop, Integer.toString(i)); // action is record so auto checks all values
			}
		}
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
				final int step = x == 1 ? 2 : 1;
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
	void testBoardGeneration() {
		for (int i = 0; i < testTimes; i++) {
			final int bombCount = 10 + i;
			final short size = (short) (4 + i);
			Board board = new Board(CellGenerator.generateCells(size, (short) (size + i), bombCount));
			assertEquals(bombCount, board.getBombCount());
			assertEquals(size, board.getRowCount());
			assertEquals(size + i, board.getColCount());

			// Verify nearby bomb count and that is valid coord
			for (Cell cell : board.flatStream().collect(Collectors.toList())) {
				assertTrue(board.isValid(cell.getX(), cell.getY()));
				if (!cell.isBomb()) {
					int expectedNearby = 0;
					for (int dx = -1; dx <= 1; dx++) {
						final int step = dx == 0 ? 2 : 1;
						for (int dy = -1; dy <= 1; dy += step) {
							int nx = cell.getX() + dx;
							int ny = cell.getY() + dy;
							if (board.isValid(nx, ny) && board.get(nx, ny).isBomb()) {
								expectedNearby++;
							}
						}
					}
					assertEquals(expectedNearby, cell.getNearbyBombs());
				}
			}
		}
	}

	@Test
	void testBoard() {
		for (int i = 0; i < testTimes; i++) {
			final Game game = generateGame();
			final Board board = game.getBoard();

			// Validate board boundaries
			assertTrue(board.isValid(0, 0));
			assertTrue(board.isValid(board.getColCount() - 1, board.getRowCount() - 1));
			assertFalse(board.isValid(-1, 0));
			assertFalse(board.isValid(0, -1));
			assertFalse(board.isValid(board.getColCount(), 0));
			assertFalse(board.isValid(0, board.getRowCount()));

			// Change cell state then reset board
			game.action(new Action(0, 1, ActionType.MARK));
			game.action(new Action(1, 0, ActionType.REVEAL));
			board.reset();

			// Verify all cells are reset
			for (Cell c : board.flatStream().collect(Collectors.toList())) {
				assertFalse(c.isMarked());
				assertFalse(c.isRevealed());
			}
		}
	}

	@Test
	void testGame() {
		for (int i = 0; i < testTimes; i++) {
			Game game = generateGame();
			int initialActions = game.getActionCount();
			boolean actionPerformed = false;

			// Perform an action on a safe cell
			for (Cell cell : game.getBoard().flatStream().collect(Collectors.toList())) {
				if (!cell.isBomb() && !cell.isRevealed() && !cell.isMarked()) {
					game.action(new Action(cell, ActionType.REVEAL));
					actionPerformed = true;
					break;
				}
			}
			assertTrue(actionPerformed); // no found safe cell
			assertTrue(game.getActionCount() > initialActions);

			// Trigger a loss by revealing a bomb cell
			for (Cell cell : game.getBoard().flatStream().collect(Collectors.toList())) {
				if (cell.isBomb() && !cell.isRevealed()) {
					// Ensure unmarked before revealing
					if (cell.isMarked()) {
						game.action(new Action(cell, ActionType.MARK));
					}
					game.action(new Action(cell, ActionType.REVEAL));
					break;
				}
			}
			assertTrue(game.hasLost());
			assertTrue(game.isFinished());
		}
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
			assertTrue(FileStorage.deleteBoard(game.getBoard()));
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

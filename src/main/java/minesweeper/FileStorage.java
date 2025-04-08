package minesweeper;

import minesweeper.model.ActionList;
import minesweeper.model.Board;
import minesweeper.model.BoardGenerator;
import minesweeper.model.Game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

public class FileStorage {
	private static final String storage = "storage/";

	private FileStorage() {
	}

	public static int getNewBoardID() {
		final File folder = new File(storage);
		return folder.list().length;
	}

	public static void storeBoard(final Board board) throws IOException {
		final String path = storage + board.getID();
		final File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdir();
		}
		final File file = new File(folder.getPath() + "/board.bin");
		if (file.exists()) {
			return;
		}
		file.createNewFile();

		final byte[] data = BoardGenerator.convertToBytes(board.getCells());
		try (final FileOutputStream out = new FileOutputStream(file)) {
			out.write(data);
		}
	}

	public static void storeGame(final Game game) throws IOException {
		final Board board = game.getBoard();
		storeBoard(board);
		final String path = storage + board.getID() + "/" + game.getName() + ".bin";
		final File file = new File(path);
		final byte[] data = game.getActionList().getByteData();
		try (final FileOutputStream out = new FileOutputStream(file)) {
			out.write(data);
		}
	}

	private static byte[] getBytes(final String path) throws IOException {
		final File file = new File(path);
		if (!file.exists()) {
			throw new FileNotFoundException(path);
		}

		byte[] data = null;
		try (final FileInputStream in = new FileInputStream(file)) {
			data = in.readAllBytes();
		}
		return data;
	}

	public static Board fetchBoard(final int id) throws IOException {
		final String path = storage + id + "/board.bin";
		return new Board(BoardGenerator.generateCells(getBytes(path)), id);
	}

	public static Game fetchGame(final String name, final int board) throws IOException {
		final String path = storage + board + "/" + name + ".bin";
		return new Game(fetchBoard(board), new ActionList(getBytes(path)), name);
	}

	private static Stream<String> fetchFileStream(final String path) {
		final File folder = new File(path);
		final String[] list = folder.list();
		if (list == null || list.length == 0) {
			return Stream.empty();
		}
		return Stream.of(list);
	}

	public static int[] fetchBoardIDs() {
		return fetchFileStream(storage).mapToInt(Integer::parseInt).toArray();
	}

	public static String[] fetchGamesNames(final int boardID) {
		return fetchFileStream(storage + boardID)
				.filter(f -> f != "board.bin")
				.map(f -> f.replace(".bin", ""))
				.toArray(String[]::new);
	}

	public static void deleteGame(final Game game) {
		final String path = storage + game.getBoard().getID() + "/" + game.getName() + ".bin";
		File file = new File(path);
		file.delete();
	}
}

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
import java.nio.file.FileAlreadyExistsException;
import java.time.LocalDate;
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
        final LocalDate date = LocalDate.now();
        final String path = storage + board.getID() + "/" + date + ".bin";
        final File file = new File(path);
        if (file.exists()) {
            throw new FileAlreadyExistsException(path);
        }
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
        return new Game(fetchBoard(board), new ActionList(getBytes(path)));
    }

    public static int[] fetchBoardIDs() throws IOException {
        final File folder = new File(storage);
        final Stream<String> stream = Stream.of(folder.list());
        return stream.mapToInt(Integer::parseInt).toArray();
    }

    public static String[] fetchGamesNames(final int boardID) {
        final File folder = new File(storage + boardID);
        final Stream<String> stream = Stream.of(folder.list());
        return stream
                .filter(f -> f != "board.bin")
                .map(f -> f.replace(".bin", ""))
                .toArray(String[]::new);
    }

}

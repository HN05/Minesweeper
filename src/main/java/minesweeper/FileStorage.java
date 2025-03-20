package minesweeper;


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

public class FileStorage {
    private static final String storage = "storage/";
    private FileStorage() {}

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

    public static void storeGame(Game game) throws IOException {
        final Board board = game.getBoard();
        storeBoard(board);
        final LocalDate date = LocalDate.now();
        final String path = storage + board.getID() + "/" + date + ".bin";
        final File file = new File(path);
        if (file.exists()) {
            throw new FileAlreadyExistsException(path);
        }
        final byte[] data = game.convertToBytes();
        try (final FileOutputStream out = new FileOutputStream(file)) {
            out.write(data);
        }
    }

    private static Board fetchBoard(int id) throws IOException {
        final String path = storage + id + "/board.bin";
        final File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException(path);
        }
        
        byte[] data = null;
        try (final FileInputStream in = new FileInputStream(file)) {
            data = in.readAllBytes();
        }
        return new Board(BoardGenerator.generateCells(data), id);   
    }

    public static Board[] fetchBoards() {
        return null;
    } 

    public static String[] fetchGames(int boardID) {
        return new String[0];
    }

    public static Game fetchGame() {
        return null;
    }  
}

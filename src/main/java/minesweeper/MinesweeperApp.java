package minesweeper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MinesweeperApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    private URL getResource(final String name) {
        return getClass().getResource(name);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Minesweeper");
        final Image appIcon = new Image(getResource("minesweeper.png").toExternalForm());
        // set app icon (does not set dock icon)
        primaryStage.getIcons().add(appIcon);
        primaryStage.setMinWidth(300);
        primaryStage.setMinHeight(300);
        primaryStage.setScene(new Scene(FXMLLoader.load(getResource("App.fxml"))));
        primaryStage.show();
    }

}

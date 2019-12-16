package sample;

import SeaBattle.SeaBattleBoardController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import object.Room;
import messagesfromserver.MessageAboutAttack;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {
    public static String name;
    private Socket socket;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    public static Room room;
    public static String field;
    public static Window window;
    private static boolean firstTime = false;
    private static Object returnedMessage;
    public static GridPane allyGridPane;
    public static GridPane enemyGridPane;
    public static Label turnLabel;
    public static String ip = null;
    private static Parent root;
    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {

    }
    public static Object sendReturnableMessage(Object message) throws IOException, ClassNotFoundException, InterruptedException {
        if (!firstTime) {
            firstTime = true;
            out.writeObject(message);
            return in.readObject();
        }
        out.writeObject(message);
        Thread.sleep(500);
        return returnedMessage;
    }
    public static void unblock() {
        Platform.runLater(() -> turnLabel.setText("Ваш ход"));
        for (Node node: enemyGridPane.getChildren()) {
            node.setOnMouseClicked(e -> {
                try {
                    String reply = (String) Main.sendReturnableMessage(new MessageAboutAttack(Main.name, enemyGridPane.getChildren().indexOf(node) - 1));
                    if (reply.equals("hit")) {
                        node.setStyle("-fx-background-color: black");
                    } else {
                        node.setStyle("-fx-background-color: grey");
                        blockGrid();
                    }
                } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
    private static void blockGrid() {
        Platform.runLater(() -> turnLabel.setText("Ход оппонента"));
        for (Node node: enemyGridPane.getChildren()) {
            node.setOnMouseClicked(e -> {});
        }
    }
}


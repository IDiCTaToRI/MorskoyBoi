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
        name = "user" + new Random().nextInt(99);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("connect.fxml"));
        root = loader.load();
        scene = new Scene(root);

        new Thread(() -> {
            while (ip == null) {
                System.out.println("thread ip " + ip);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Platform.runLater(() -> {
                try {
                    socket = new Socket(ip, 8080);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FXMLLoader loader2 = new FXMLLoader();
                loader2.setController(new Controller());
                loader2.setLocation(getClass().getResource("sample.fxml"));
                scene.getWindow().hide();
                Parent root = null;
                try {
                    root = loader2.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                scene = new Scene(root);
                Stage stage = new Stage();
                stage.setTitle("Выбор комнаты");
                stage.setScene(scene);
                stage.show();
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    Object input;
                    input = in.readObject();
                    if (input instanceof ArrayList) {
                        returnedMessage = input;
                        continue;
                    }
                    returnedMessage = input;
                    if (input.equals("Ready")) {
                        goToBattleWindow();
                    }
                    else if (((String)input).contains("new hit on")) {
                        attackedOn(Integer.parseInt(((String)input).split(" ")[3]));
                    }
                    else if (input.equals("Победа вместо обеда")) {
                        victoryBlock();
                        Platform.runLater(() -> {
                            turnLabel.setText("Вы победили!");
                        });
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        primaryStage.setTitle("Выбор комнаты");
        primaryStage.setScene(scene);
        primaryStage.show();
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
    public static void sendMessageToServer(Object message) throws IOException {
        out.writeObject(message);
    }
    public static void goToBattleWindow() {
        Platform.runLater(
                () -> {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(SeaBattleBoardController.class.getResource("/game/game.fxml"));
                    Parent root = null;
                    try {
                        root = loader.load();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Поле боя");
                    stage.setScene(new Scene(root));
                    stage.show();
                    window.getScene().getWindow().hide();
                }
        );
    }
    private static void attackedOn(int attackedIndex) throws IOException {
        Node attackedNode = allyGridPane.getChildren().get(attackedIndex + 1);
        if (attackedNode.getStyle().contains("battleship1")) {
            attackedNode.setStyle("-fx-background-color: red");
        }
        else if (!attackedNode.getStyle().contains("battleship1")) {
            attackedNode.setStyle("-fx-background-color: black");
            unblock();
        }
        if (!checkIfAnyAlive()) {
            victoryBlock();
            Platform.runLater(() -> turnLabel.setText("Все ваши корабли уничтожены. Вы проиграли."));
            sendMessageToServer(name + " dead");
        }
    }
    private static boolean checkIfAnyAlive() {
        for (Node node: allyGridPane.getChildren()) {
            if (node.getStyle().contains("battleship1")) {
                return true;
            }
        }
        return false;
    }
    private static void victoryBlock() {
        for (Node node: enemyGridPane.getChildren()) {
            node.setOnMouseClicked(e -> {});
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}


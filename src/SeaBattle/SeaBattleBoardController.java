package SeaBattle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import sample.Main;
import messagesfromserver.NewField;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SeaBattleBoardController implements Initializable {

    @FXML
    private GridPane gridPane;
    @FXML
    private ImageView battleship1ImageView;
    @FXML
    private ImageView battleship2ImageView;
    @FXML
    private ImageView battleship3ImageView;
    @FXML
    private ImageView battleship4ImageView;
    @FXML
    private Label battleship1Label;
    @FXML
    private Label battleship2Label;
    @FXML
    private Label battleship3Label;
    @FXML
    private Label battleship4Label;
    @FXML
    private AnchorPane window;
    @FXML
    private Button readyButton;
    private double startDragX;
    private double startDragY;
    private ArrayList<Rectangle> rectangles = new ArrayList<>();
    private ArrayList<SeaBattle> seaBattles = new ArrayList<>();
    private ArrayList<Label> labels = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addCells();
        setGridActions();
        setReadyButtonOnClick();
        initializeBattleships();
        setArrayOfLabels();
        for (SeaBattle seaBattle : seaBattles) {
            setImageOnDrag(seaBattle);
        }
    }

    private void setArrayOfLabels() {
        labels.add(battleship1Label);
        labels.add(battleship2Label);
        labels.add(battleship3Label);
        labels.add(battleship4Label);
    }

    private void initializeBattleships() {
        seaBattles.add(new SeaBattle(battleship1ImageView, 4, 1));
        seaBattles.add(new SeaBattle(battleship2ImageView, 3, 2));
        seaBattles.add(new SeaBattle(battleship3ImageView, 2, 3));
        seaBattles.add(new SeaBattle(battleship4ImageView, 1, 4));
    }

    private void setGridActions() {
        for (int i = 0; i < gridPane.getChildren().size(); i++) {
            Node node = gridPane.getChildren().get(i);
            double x = 28 + (i / 10) * 30;
            double y = 37 + (i % 10) * 30;
            rectangles.add(new Rectangle(x, y, 30, 30));
            node.setOnMouseEntered(e -> {
                System.out.println(node.getStyle().toString());
                if (!node.getStyle().contains("battleship1")) {
                    node.setStyle("-fx-background-image: default");
                }
            });
            node.setOnMouseExited(e -> {
                System.out.println(node.getStyle().toString());
                if (!node.getStyle().contains("battleship1")) {
                    node.setStyle("-fx-background-image: default");
                }
            });
            //127.0.0.1
            node.setOnMouseClicked(e -> System.out.println(GridPane.getColumnIndex(node) + " " + GridPane.getRowIndex(node)));
        }
    }

    private void setImageOnDrag(SeaBattle seaBattle) {
        ImageView battleshipCopy = new ImageView(seaBattle.getImageView().getImage());
        battleshipCopy.setFitWidth(30 * seaBattle.getLength());
        battleshipCopy.setFitHeight(30);

        seaBattle.getImageView().setOnMousePressed(e -> {
            if (seaBattle.getLeft() > 0) {
                battleshipCopy.setLayoutX(seaBattle.getImageView().getLayoutX());
                battleshipCopy.setLayoutY(seaBattle.getImageView().getLayoutY());
                window.getChildren().add(battleshipCopy);
                startDragX = e.getSceneX();
                startDragY = e.getSceneY();
            }
        });

        seaBattle.getImageView().setOnMouseDragged(e -> {
            battleshipCopy.setTranslateX(e.getSceneX() - startDragX);
            battleshipCopy.setTranslateY(e.getSceneY() - startDragY);
        });

        seaBattle.getImageView().setOnMouseReleased(e -> {
            if (seaBattle.getLeft() > 0) {
                int i = -1;
                for (Rectangle rect : rectangles) {
                    if (rect.contains(e.getSceneX(), e.getSceneY())) {
                        i = rectangles.indexOf(rect);
                    }
                }
                if (i != -1) {
                    if (i + (seaBattle.getLength() - 1) * 10 <= 100) {
                        if (isPlaceable(i + 1, seaBattle.getLength())) {
                            for (int j = 0; j < seaBattle.getLength(); j++) {
                                gridPane.getChildren().get(i + 1 + j * 10).setStyle("-fx-background-image: url('/images/battleship1.jpg' ); -fx-background-size: stretch; -fx-background-position: center center; ");
                            }
                            seaBattle.used();
                            labels.get(seaBattles.indexOf(seaBattle)).setText(seaBattle.getLeft() + "x");
                            if (allShipsSet()) {
                                readyButton.setText("Готово. В бой!");
                                readyButton.setDisable(false);
                            }
                        }
                    }
                }
                window.getChildren().remove(battleshipCopy);
            }
        });
    }

    private boolean isPlaceable(int i, int length) {
        for (int j = i; j < i + length * 10; j += 10) {
            if (j % 10 != 0 && j % 10 != 1) {
                if (!((j - 1 <= 0 || !gridPane.getChildren().get(j - 1).getStyle().contains("battleship1")) &&
                        (j + 1 > 100 || !gridPane.getChildren().get(j + 1).getStyle().contains("battleship1")) &&
                        (j - 10 <= 0 || !gridPane.getChildren().get(j - 10).getStyle().contains("battleship1")) &&
                        (j + 10 > 100 || !gridPane.getChildren().get(j + 10).getStyle().contains("battleship1")) &&
                        (j + 11 > 100 || !gridPane.getChildren().get(j + 11).getStyle().contains("battleship1")) &&
                        (j + 9 > 100 || !gridPane.getChildren().get(j + 9).getStyle().contains("battleship1")) &&
                        (j - 11 <= 0 || !gridPane.getChildren().get(j - 11).getStyle().contains("battleship1")) &&
                        (j - 9 <= 0 || !gridPane.getChildren().get(j - 9).getStyle().contains("battleship1")))) {
                    return false;
                }
            } else if (j == 1) {
                if (!(!gridPane.getChildren().get(2).getStyle().contains("battleship1") &&
                        !gridPane.getChildren().get(11).getStyle().contains("battleship1") &&
                        !gridPane.getChildren().get(12).getStyle().contains("battleship1"))) {
                    return false;
                }
            } else if (j == 10) {
                if (!(!gridPane.getChildren().get(9).getStyle().contains("battleship1") &&
                        !gridPane.getChildren().get(19).getStyle().contains("battleship1") &&
                        !gridPane.getChildren().get(20).getStyle().contains("battleship1"))) {
                    return false;
                }
            } else if (j == 91) {
                if (!(!gridPane.getChildren().get(81).getStyle().contains("battleship1") &&
                        !gridPane.getChildren().get(82).getStyle().contains("battleship1") &&
                        !gridPane.getChildren().get(92).getStyle().contains("battleship1"))) {
                    return false;
                }
            } else if (j == 100) {
                if (!(!gridPane.getChildren().get(89).getStyle().contains("battleship1") &&
                        !gridPane.getChildren().get(99).getStyle().contains("battleship1") &&
                        !gridPane.getChildren().get(90).getStyle().contains("battleship1"))) {
                    return false;
                }
            } else if (j % 10 == 0) {
                if (!(!gridPane.getChildren().get(j - 1).getStyle().contains("battleship1") &&
                        !gridPane.getChildren().get(j - 10).getStyle().contains("battleship1") &&
                        !gridPane.getChildren().get(j + 10).getStyle().contains("battleship1") &&
                        !gridPane.getChildren().get(j - 11).getStyle().contains("battleship1") &&
                        !gridPane.getChildren().get(j + 9).getStyle().contains("battleship1"))) {
                    return false;
                }
            } else { // if j % 10 == 1
                if (!(!gridPane.getChildren().get(j + 1).getStyle().contains("battleship1") &&
                        !gridPane.getChildren().get(j - 10).getStyle().contains("battleship1") &&
                        !gridPane.getChildren().get(j + 10).getStyle().contains("battleship1") &&
                        !gridPane.getChildren().get(j + 11).getStyle().contains("battleship1") &&
                        !gridPane.getChildren().get(j - 9).getStyle().contains("battleship1"))) {
                    return false;
                }
            }
        }
        return true;
    }


    private void addCells() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Pane pane = new Pane();
                GridPane.setColumnIndex(pane, i);
                GridPane.setRowIndex(pane, j);
                gridPane.getChildren().add(pane);
            }
        }
    }

    private boolean allShipsSet() {
        return battleship1Label.getText().equals("0x") &&
                battleship2Label.getText().equals("0x") &&
                battleship3Label.getText().equals("0x") &&
                battleship4Label.getText().equals("0x");
    }

    private void setReadyButtonOnClick() {
        readyButton.setOnMouseClicked(e -> {
            Main.window = window.getScene().getWindow();
            String allReady = null;
            String field = "";
            for (int i = 1; i < gridPane.getChildren().size(); i++) {
                Node node = gridPane.getChildren().get(i);
                if (node.getStyle().contains("battleship1")) {
                    field = field.concat("1");
                }
                else {
                    field = field.concat("0");
                }
            }
            Main.field = field;
            try {
                Object input = Main.sendReturnableMessage((new NewField(Main.name, field)));
                System.out.println("Ups: " + input);
                allReady = (String) input;
            } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                ex.printStackTrace();
            }
            if (allReady.equals("not")) {
                readyButton.setDisable(true);
                readyButton.setText("Ожидаем противника...");
            }
        });
    }
}

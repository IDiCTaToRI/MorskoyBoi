package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ConnectController implements Initializable {
    @FXML
    private Button connectButton;
    @FXML
    private TextField ipField;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectButton.setOnMouseClicked(e -> {
            Main.ip = ipField.getText();
            System.out.println("setting " + ipField.getText());
            System.out.println("main.ip=" + Main.ip);
        });
    }
}

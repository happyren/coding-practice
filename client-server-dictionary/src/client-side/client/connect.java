package main.java.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static main.java.client.Main.serverOn;

public class connect {

    static List<String> params = new ArrayList<>();
    main.java.client.Controller cont = new main.java.client.Controller();

    @FXML
    private AnchorPane root;

    @FXML
    private TextField Host;

    @FXML
    private TextField Port;

    @FXML
    private Button connect;

    @FXML
    void enter(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            try {
                click(new ActionEvent());
            } catch (Exception e) {

            }
        }
    }

    @FXML
    void click(ActionEvent event) throws Exception {
        if (!Host.getCharacters().toString().matches(
                "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$")
                || !Port.getCharacters().toString().matches("^[1-9][0-9]{1,4}$")
                || !serverOn(Host.getCharacters().toString(),
                Integer.parseInt(Port.getCharacters().toString()))) {
            Parent again = FXMLLoader.load(getClass().getResource("/connect.fxml"));
            Stage curstg = (Stage) root.getScene().getWindow();
            curstg.setScene(new Scene(again, 465, 276));
        } else {
            params.add(Host.getCharacters().toString());
            params.add(Port.getCharacters().toString());
            Parent main = FXMLLoader.load(getClass().getResource("/sample.fxml"));
            cont.setParameter(params.get(0), Integer.parseInt(params.get(1)));
            Stage curstg = ((Stage) connect.getScene().getWindow());
            curstg.setTitle("Online Dictionary");
            curstg.setScene(new Scene(main, 800, 600));
        }
    }
}

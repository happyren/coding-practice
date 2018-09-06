package main.java.client;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

import static main.java.client.Main.infoScene;

public class logPrompt {

    private static String host;
    private static int port;
    private static boolean passed;

    static boolean access;

    void setParameters(String host, int port) {
        this.host = host;
        this.port = port;
    }

    boolean getPassed() {
        return passed;
    }

    void resetPassed() {
        passed = false;
    }

    @FXML
    private TextField logName;

    @FXML
    private PasswordField logPass;

    @FXML
    private Button logBut;

    @FXML
    void enter(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            sendAuth(new ActionEvent());
        }
    }

    @FXML
    void sendAuth(ActionEvent event) {
        String name = logName.getText();
        String pass = logPass.getText();
        logName.clear();
        logPass.clear();
        try {
            InetSocketAddress isa = new InetSocketAddress(host, port);
            Socket server = new Socket();
            server.connect(isa, 500);
            server.setSoTimeout(500);

            InputStream in = server.getInputStream();
            OutputStream out = server.getOutputStream();
            DataInputStream din = new DataInputStream(in);
            DataOutputStream dout = new DataOutputStream(out);

            JSONObject appProtocol = new JSONObject();
            JSONObject id = new JSONObject();
            id.put("%NAME", name);
            id.put("%PASS", pass);
            appProtocol.put("%CMD", "ADMINLOGIN");
            appProtocol.put("%ADMINLOGIN", id);
            dout.writeUTF(appProtocol.toJSONString());
            passed = din.readBoolean();
            Stage curstg = (Stage) logBut.getScene().getWindow();
            curstg.close();
        } catch (IOException ioe) {
            infoScene();
        }
    }
}

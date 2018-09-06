package main.java.client;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent notpas = FXMLLoader.load(getClass().getResource("/connect.fxml"));

        Parent passcen = FXMLLoader.load(getClass().getResource("/sample.fxml"));

        main.java.client.Controller cont = new main.java.client.Controller();
        List<String> param = new ArrayList<>();
        param = getParameters().getRaw();

        if(param.size()!=2 || !param.get(0).matches(
                "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$")
                || !param.get(1).matches("^[1-9][0-9]{1,4}$")
                || !serverOn(param.get(0), Integer.parseInt(param.get(1)))) {
            primaryStage.setTitle("Please input a accessible server socket.");
            primaryStage.setScene(new Scene(notpas, 465, 276));
            primaryStage.show();
        } else {
            cont.setParameter(param.get(0), Integer.parseInt(param.get(1)));
            primaryStage.setTitle("Online Dictionary");
            primaryStage.setScene(new Scene(passcen, 800, 600));
            primaryStage.show();
        }

    }

    public static boolean serverOn(String host, int port) {
        boolean on = true;
        try{
            InetSocketAddress sa = new InetSocketAddress(host, port);
            Socket server = new Socket();
            server.connect(sa, 500);
            server.close();
        } catch (UnknownHostException uhe) {
            on = false;
        } catch (IOException ioe) {
            on = false;
        }
        return on;
    }

    static void infoScene() {
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        try {
            Stage infoStg = new Stage();
            Parent info = FXMLLoader.load(Main.class.getResource("/offline.fxml"));
            infoStg.setScene(new Scene(info, 500, 328));
            infoStg.show();
            delay.setOnFinished(event -> System.exit(0));
            delay.play();
        } catch (IOException ioe) {
            ioe.getSuppressed();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

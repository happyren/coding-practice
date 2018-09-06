package main.java.client;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static main.java.client.Main.infoScene;
import static main.java.client.Main.serverOn;

public class Controller {

    private static String host;
    private static int port;
    JSONParser jp = new JSONParser();


    @FXML
    private MenuItem terminate;

    @FXML
    private MenuBar menubar;

    @FXML
    private Menu file;

    @FXML
    private MenuItem mods;

    @FXML
    private TextField queryIn;

    @FXML
    private TextArea printResult;

    @FXML
    void submit(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER) && !queryIn.getText().isEmpty()) {
            String target = queryIn.getCharacters().toString().trim();
            queryIn.clear();
            try {
                query(host, port, target);
            } catch (Exception e) {
                infoScene();
            }
        } else {
            return;
        }
    }

    private boolean prompt() throws Exception {
        main.java.client.logPrompt con = new main.java.client.logPrompt();
        con.setParameters(host, port);
        Stage prompt = new Stage();
        Parent log = FXMLLoader.load(getClass().getResource("/logPrompt.fxml"));
        prompt.setScene(new Scene(log, 351, 228));
        prompt.setTitle("Administrator Authentication");
        prompt.initModality(Modality.APPLICATION_MODAL);
        prompt.showAndWait();
        while (prompt.isShowing()) {
        } //maintain program running when prompt not closed
        boolean result = con.getPassed();
        con.resetPassed();
        return result;
    }

    void setParameter(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @FXML
    void terminate(ActionEvent event) {
        Stage curstg = (Stage) menubar.getScene().getWindow();
        curstg.close();
    }

    @FXML
    void mods(ActionEvent event) {
        try {
            boolean passed = prompt();
            if (passed) {
                main.java.client.mod con = new main.java.client.mod();
                con.setParameters(host, port);
                Stage modWindow = new Stage();
                Parent mod = FXMLLoader.load(getClass().getResource("/mod.fxml"));
                modWindow.setTitle("Administrator Modification");
                modWindow.setScene(new Scene(mod, 600, 400));
                modWindow.initModality(Modality.APPLICATION_MODAL);
                modWindow.showAndWait();
                while (modWindow.isShowing()) {
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * Independent Query Method, everyone can use it.
     *
     * @param hostname remote host name
     * @param port     remote host port
     * @param target   target word of query
     * @throws Exception don't worry about it
     */
    private void query(String hostname, int port, String target) {
        try {
            InetSocketAddress isa = new InetSocketAddress(hostname, port);
            Socket server = new Socket();
            server.connect(isa, 500);
            server.setSoTimeout(500);

            InputStream serverIn = server.getInputStream();
            DataInputStream din = new DataInputStream(serverIn);
            OutputStream serverOut = server.getOutputStream();
            DataOutputStream dout = new DataOutputStream(serverOut);

            //Forge query message
            JSONObject appProtocol = new JSONObject();
            appProtocol.put("%CMD", "QUERY");
            appProtocol.put("%QUERY", target);

            dout.writeUTF(appProtocol.toJSONString());
            dout.flush();
            JSONObject result = (JSONObject) jp.parse(din.readUTF());

            server.close();

            if (result.containsKey("No such word")) {
                printResult.setText("This word does not exist in current dictionary.");
                return;
            }

            JSONArray meanings = null;
            Iterator<JSONObject> it;
            List<main.java.client.explain> means = new ArrayList<>();
            meanings = (JSONArray) result.get("meanings");
            it = meanings.iterator();
            String print = "";
            while (it.hasNext()) {
                JSONObject mean = it.next();
                String def = (String) mean.get("def");
                String sample = (String) mean.get("example");
                String part = (String) mean.get("speech_part");
                main.java.client.explain exp = new main.java.client.explain(def, sample, part);
                print += exp.toString() + "\n\n";
            }
            printResult.setText(print);
        } catch (IOException ioe) {
            infoScene();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



}

package main.java.client;


import main.java.client.modResult;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

import static main.java.client.Main.infoScene;

public class mod {

    private static String host;
    private static int port;
    JSONObject wordMod = new JSONObject();
    JSONObject wordBody = new JSONObject();
    JSONArray meanings = new JSONArray();
    String toCheck;
    int counter = 0;

    void setParameters(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @FXML
    private TabPane modPane;

    @FXML
    private Tab addPane;

    @FXML
    private TextField toAdd;

    @FXML
    private TextField definition;

    @FXML
    private TextField speechpart;

    @FXML
    private TextField example;

    @FXML
    private Button checkIfExist;

    @FXML
    private Button plusDef;

    @FXML
    private Button submiWord;

    @FXML
    private Button minusDef;

    @FXML
    private Tab deletePane;

    @FXML
    private TextField toDelete;

    @FXML
    private Button checkIfExist2;

    @FXML
    private Button deleteConfirm;

    @FXML
    void add(ActionEvent event) {
        if (!toAdd.getText().trim().equals(toCheck)) {
            resetAll();
            toAdd.setPromptText("Come on...");
        }
        wordBody.put("meanings", meanings);
        wordBody.put("word", toCheck);
        wordMod.put("%%WORD", toCheck);
        wordMod.put("%%BODY", wordBody);
        JSONObject CMD = new JSONObject();
        CMD.put("%CMD", "ADD");
        CMD.put("%WORD", wordMod);
        try {
            InetSocketAddress isa = new InetSocketAddress(host, port);
            Socket server = new Socket();
            server.connect(isa, 10000);
            server.setSoTimeout(10000);

            InputStream in = server.getInputStream();
            OutputStream out = server.getOutputStream();
            DataInputStream din = new DataInputStream(in);
            DataOutputStream dout = new DataOutputStream(out);
            dout.writeUTF(CMD.toJSONString());
            boolean done = din.readBoolean();
            if(done) {
                toAdd.setPromptText("Word Add Successfully!");
                successInfo(done);
            } else {
                successInfo(done);
            }
            resetAll();
            dout.flush();
            server.close();
        } catch (IOException e) {
            infoScene();
            e.getStackTrace();
        }
    }

    private void resetAll() {
        meanings.clear();
        counter = 0;
        submiWord.setDisable(true);
        definition.setDisable(true);
        speechpart.setDisable(true);
        example.setDisable(true);
        plusDef.setDisable(true);
        minusDef.setDisable(true);
        toAdd.clear();
    }

    @FXML
    void addDef(ActionEvent event) {
        if (definition.getText().isEmpty() || speechpart.getText().isEmpty()) {
            definition.setPromptText("Definition is must have");
            speechpart.setPromptText("Definition is must have");
            return;
        }
        String def = definition.getText();
        String sam = example.getText();
        String part = speechpart.getText();
        JSONObject meaning = new JSONObject();
        meaning.put("def", def);
        meaning.put("example", sam);
        meaning.put("speech_part", part);
        meanings.add(meaning);
        definition.setPromptText("Please give a definition here...");
        speechpart.setPromptText("Please give a speech part");
        submiWord.setDisable(false);
        counter += 1;
        definition.clear();
        example.clear();
        speechpart.clear();
        minusDef.setDisable(false);
    }

    @FXML
    void checkExist(ActionEvent event) throws IOException {
        TextField toPrint;
        if (addPane.isSelected()) {
            toCheck = toAdd.getText().trim();
            toPrint = toAdd;
        } else {
            toCheck = toDelete.getText().trim();
            toPrint = toDelete;
        }
        JSONObject tc = new JSONObject();
        tc.put("%CMD", "CHECK");
        tc.put("%WORD", toCheck);

        try {
            InetSocketAddress isa = new InetSocketAddress(host, port);
            Socket server = new Socket();
            server.connect(isa, 500);
            server.setSoTimeout(500);

            InputStream is = server.getInputStream();
            OutputStream os = server.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            DataInputStream din = new DataInputStream(is);

            dos.writeUTF(tc.toJSONString());
            boolean exist = din.readBoolean();

            server.close();

            if (toPrint.equals(toAdd) && exist == true) {
                toPrint.clear();
                toPrint.setPromptText("This word is already there!");
                submiWord.setDisable(true);
                definition.setDisable(true);
                speechpart.setDisable(true);
                example.setDisable(true);
                plusDef.setDisable(true);
            } else if (toPrint.equals(toDelete) && exist == false) {
                toPrint.clear();
                toPrint.setPromptText("This word does not exist!");
                deleteConfirm.setDisable(true);
            } else if (toPrint.equals(toAdd) && exist == false) {
                definition.setDisable(false);
                speechpart.setDisable(false);
                example.setDisable(false);
                plusDef.setDisable(false);
            } else {
                deleteConfirm.setDisable(false);
            }
        } catch (IOException ioe) {
            infoScene();
            ioe.getSuppressed();
        }
    }

    @FXML
    void delete(ActionEvent event) {
        if (!toDelete.getText().trim().equals(toCheck)) {
            toDelete.clear();
            deleteConfirm.setDisable(false);
            toDelete.setPromptText("Come on...");
        }
        JSONObject CMD = new JSONObject();
        CMD.put("%CMD", "DELETE");
        CMD.put("%WORD", toCheck);
        try {
            InetSocketAddress isa = new InetSocketAddress(host, port);
            Socket server = new Socket();
            server.connect(isa, 10000);
            server.setSoTimeout(10000);

            InputStream is = server.getInputStream();
            OutputStream os = server.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            DataInputStream din = new DataInputStream(is);

            dos.writeUTF(CMD.toJSONString());
            boolean done = din.readBoolean();
            if(done) {
                toDelete.setPromptText("Word Delete Successfully");
                successInfo(done);
            } else {
                successInfo(done);
            }
            server.close();
            toDelete.clear();
            deleteConfirm.setDisable(true);
            toDelete.setPromptText(null);
        } catch (IOException ioe) {
            infoScene();
            ioe.getSuppressed();
        }

    }

    @FXML
    void deleteDef(ActionEvent event) {
        meanings.remove(counter - 1);
        counter -= 1;
        if (counter == 0) {
            minusDef.setDisable(true);
            submiWord.setDisable(true);
        }
    }

    /**
     * This is a method when a add or delete method is submit to the server, result would print on it and it would auto
     * close after 3 seconds.
     * @param success result of a operation.
     */
    void successInfo(boolean success) {
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        try {
            Stage infoStg = new Stage();
            FXMLLoader loader = new FXMLLoader(main.java.client.Main.class.getResource("/modResult.fxml"));
            Parent info = loader.load();
            infoStg.setScene(new Scene(info, 310, 200));
            modResult cont = loader.getController();
            infoStg.setOnShowing(e -> cont.initialize(success));
            infoStg.show();
            delay.setOnFinished(event -> infoStg.close());
            delay.play();
        } catch (IOException ioe) {
            ioe.getSuppressed();
        }
    }

}

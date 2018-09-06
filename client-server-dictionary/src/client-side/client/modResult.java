package main.java.client;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.util.EventListener;

public class modResult {

    @FXML
    private Text result;

    public void initialize(boolean result){
        if(result) {
            this.result.setText("Succeed");
        } else {
            this.result.setText("Failed");
        }
    }

}

package com.example.discordgui.controller;

import com.example.discordgui.UI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class DynamicFormController {

    private UI ui;
    private TextField last;

    @FXML
    private VBox box;

    @FXML
    private Text header;


    @FXML
    void exit(ActionEvent event) throws IOException {
        ui.methodWrite("exit");
    }

    public void init (String header,String req){
        this.header.setText(header);

        TextField textField = new TextField();
        textField.setPromptText(req);
        textField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)){
                try {
                    ui.methodWrite(textField.getText());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        textField.getStyleClass().add("DynamicFormField");
        Platform.runLater(() -> box.getChildren().add(textField));
        last = textField;
    }

    public void next (String header,String req){
        this.header.setText(header);
        this.header.setStyle("");

        last.setDisable(true);

        TextField textField = new TextField();
        textField.setPromptText(req);
        textField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)){
                try {
                    ui.methodWrite(textField.getText());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        textField.getStyleClass().add("DynamicFormField");
        Platform.runLater(() -> box.getChildren().add(textField));
        last = textField;
    }

    public void again (String header) {
        this.header.setText(header);
        this.header.setStyle("-fx-fill: #770000");
        last.setText("");
    }




    public void setUi(UI ui) {
        this.ui = ui;
    }

}

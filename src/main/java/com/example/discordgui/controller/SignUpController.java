package com.example.discordgui.controller;

import com.example.discordgui.Command;
import com.example.discordgui.UI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;

public class SignUpController {

    private UI ui;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField passwordConfirm;

    @FXML
    private TextField phone;

    @FXML
    private TextField userName;

    @FXML
    void login(ActionEvent event) throws IOException {
        ui.methodWrite(Command.LOGIN.getStr());
    }

    @FXML
    void signUp(ActionEvent event) throws IOException {
        if (!(userName.getText().isEmpty() || password.getText().isEmpty() || email.getText().isEmpty())){
            if (password.getText().equals(passwordConfirm.getText())){
                ui.methodWrite("Info");
                ui.methodWrite(userName.getText());
                ui.methodWrite(password.getText());
                ui.methodWrite(email.getText());
                ui.methodWrite((phone.getText().isEmpty()) ? "NO PHONE NUMBER" : phone.getText());
            }else {
                ui.sendPopUp("ERROR","Password and Password confirm don't match");
            }
        }else {
            ui.sendPopUp("ERROR","Please fill out the required fields");
        }
    }

    public void init(UI ui){
        this.ui=ui;

        EventHandler<KeyEvent> e = keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)){
                try {
                    signUp(null);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };

        email.setOnKeyPressed(e);
        password.setOnKeyPressed(e);
        passwordConfirm.setOnKeyPressed(e);
        userName.setOnKeyPressed(e);
        phone.setOnKeyPressed(e);
    }
}

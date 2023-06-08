package com.example.discordgui.controller;

import com.example.discordgui.Client;
import com.example.discordgui.UI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;

public class ProfilePageController {

    private UI ui;

    private final Image online = new Image(String.valueOf(Client.class.getResource("PNG/online.png")));
    private final Image offline = new Image(String.valueOf(Client.class.getResource("PNG/offline.png")));
    private final Image idle = new Image(String.valueOf(Client.class.getResource("PNG/idle.png")));
    private final Image dnd = new Image(String.valueOf(Client.class.getResource("PNG/dnd.png")));
    private final Image invisible = new Image(String.valueOf(Client.class.getResource("PNG/invisible.png")));
    private final Image defProf = new Image(String.valueOf(Client.class.getResource("PNG/ico.png")));

    @FXML
    private ImageView avatar;

    @FXML
    private Text email;

    @FXML
    private Text phone;

    @FXML
    private ImageView status;

    @FXML
    private Text userName;

    public void Init (UI ui, String userName, String email, String phoneNumber, String status, File pic){
        this.ui = ui;
        this.userName.setText(userName);
        this.email.setText(email);
        this.phone.setText(phoneNumber);

        switch (status) {
            case "Online":
                this.status.setImage(online);
                break;
            case "Idle":
                this.status.setImage(idle);
                break;
            case "Do Not Disturb":
                this.status.setImage(dnd);
                break;
            case "Invisible":
                this.status.setImage(invisible);
                break;
            case "offline":
                this.status.setImage(offline);
                break;
        }

        if (pic != null) {
            this.avatar.setImage(new Image(pic.toURI().toString()));
        } else {
            this.avatar.setImage(defProf);
        }

    }

    @FXML
    void userName(ActionEvent event) throws IOException {
        ui.methodWrite(String.valueOf(1));
    }

    @FXML
    void password(ActionEvent event) throws IOException {
        ui.methodWrite(String.valueOf(2));
    }

    @FXML
    void email(ActionEvent event) throws IOException {
        ui.methodWrite(String.valueOf(3));
    }

    @FXML
    void phone(ActionEvent event) throws IOException {
        ui.methodWrite(String.valueOf(4));
    }

    @FXML
    void status(ActionEvent event) throws IOException {
        ui.methodWrite(String.valueOf(5));
    }

    @FXML
    void picture(ActionEvent event) throws IOException {
        ui.methodWrite(String.valueOf(6));
    }

    @FXML
    void exit(ActionEvent event) throws IOException {
        ui.methodWrite(String.valueOf(7));
    }

}

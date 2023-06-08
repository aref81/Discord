package com.example.discordgui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class MessageController {

    private ChatPageController chat;
    private Long id;

    public void init(String message, ChatPageController chat) {
        message = message.substring(8, message.length() - 1);
        String[] tokens = message.split(",");
        if ((tokens[0].length() - 7) > 46) {
            pane.setPrefHeight(((tokens[0].length() - 7)) + 80);
        }
        txt.setText(tokens[0].substring(6, tokens[0].length() - 1));
        aut.setText(tokens[1].substring(8));
        id = Long.parseLong(tokens[3].substring(4));
        if (!tokens[4].contains("[]")) {
            likes.setText(tokens[4].substring(8,tokens[4].length() - 1));
        }
        if (!tokens[5].contains("[]")) {
            dislikes.setText(tokens[5].substring(11,tokens[5].length() - 1));
        }
        if (!tokens[6].contains("[]")) {
            laughters.setText(tokens[6].substring(12,tokens[6].length() - 1));
        }
        this.chat = chat;
        if (txt.getText().contains("-- #download")) {
            txt.setText(txt.getText().substring(0, txt.getText().length() - 12));
            downBut.setVisible(true);
        }
    }


    @FXML
    private BorderPane pane;

    @FXML
    private Text aut;

    @FXML
    private Text txt;

    @FXML
    private Button downBut;

    @FXML
    private Tooltip laughters;

    @FXML
    private Tooltip likes;

    @FXML
    private Tooltip dislikes;

    @FXML
    void dislike(ActionEvent event) throws IOException {
        chat.sendCommand("#dislike-" + id);
    }

    @FXML
    void happy(ActionEvent event) throws IOException {
        chat.sendCommand("#laughter-" + id);
    }

    @FXML
    void like(ActionEvent event) throws IOException {
        chat.sendCommand("#like-" + id);
    }

    @FXML
    void pin(ActionEvent event) throws IOException {
        chat.sendCommand("#pin-" + id);
    }

    @FXML
    void download(ActionEvent event) throws IOException {
        chat.download("#download-" + id);
    }

    public Long getId() {
        return id;
    }

    public void addLike(String user) {
        if (likes.getText().equals("No Likes")){
            likes.setText(user);
        } else {
            likes.setText(likes.getText() + "," + user);
        }
    }

    public void addDislike(String user) {
        if (dislikes.getText().equals("No Dislikes")){
            dislikes.setText(user);
        } else {
            dislikes.setText(laughters.getText() + "," + user);
        }
    }

    public void addLaughter(String user) {
        if (laughters.getText().equals("No Laugh")){
            laughters.setText(user);
        } else {
            laughters.setText(laughters.getText() + "," + user);
        }
    }
}

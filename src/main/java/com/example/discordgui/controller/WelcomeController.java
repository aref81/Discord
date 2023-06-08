package com.example.discordgui.controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class WelcomeController {

    @FXML
    private Text text;


    public void setText (String str){
        text.setText(str);
    }

}

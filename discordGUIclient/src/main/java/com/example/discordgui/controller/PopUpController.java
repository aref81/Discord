package com.example.discordgui.controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class PopUpController {

    @FXML
    private Text desc;

    @FXML
    private Text title;

    public void setDesc(String desc) {
        this.desc.setText(desc);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }
}

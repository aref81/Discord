package com.example.discordgui.controller;

import com.example.discordgui.UI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.w3c.dom.events.Event;

import java.io.IOException;
import java.util.ArrayList;


public class DinamicMenuController {

    private ArrayList<String> items;
    private UI ui;

    @FXML
    private VBox menu;



    public void setItems(ArrayList<String> items) {
        if (items.size() > 10) {
            menu.setPrefHeight(items.size() * 80);
        }
        this.items = items;
        for (int i = 0; i < items.size(); i++) {
            Button button=new Button();
            String str=items.get(i).split("\\.")[1];
            if (str.contains(":")){
                String[] strings = str.split(":");
                str=strings[0];

                StackPane stackPane=new StackPane();
                stackPane.getStyleClass().add("notifArea");

                Text text=new Text(strings[1]);
                text.getStyleClass().add("notif");

                stackPane.getChildren().add(text);
                button.setGraphic(stackPane);
            }
            button.setText(str);
            button.setId(String.valueOf(i+1));
            button.setOnAction(actionEvent -> {
                Button button1=(Button) actionEvent.getSource();
                try {
                    ui.methodWrite(button1.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            button.getStyleClass().add("DynamicMenuItem");
            menu.getChildren().add(button);
        }
    }

    public void setUi(UI ui) {
        this.ui = ui;
    }


}

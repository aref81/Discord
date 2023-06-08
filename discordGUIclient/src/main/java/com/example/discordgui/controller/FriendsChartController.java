package com.example.discordgui.controller;

import com.example.discordgui.Client;
import com.example.discordgui.UI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FriendsChartController {


    @FXML
    private VBox box;

    @FXML
    private ToggleGroup tg;

    @FXML
    private ToggleButton all;

    @FXML
    private ToggleButton blocked;

    @FXML
    private ToggleButton online;

    @FXML
    private ToggleButton pending;


    @FXML
    void exit(ActionEvent event) throws IOException {
        ui.methodWrite("exit");
    }



    private final Image onlineP = new Image(String.valueOf(Client.class.getResource("PNG/online.png")));
    private final Image offline = new Image(String.valueOf(Client.class.getResource("PNG/offline.png")));
    private final Image idle = new Image(String.valueOf(Client.class.getResource("PNG/idle.png")));
    private final Image dnd = new Image(String.valueOf(Client.class.getResource("PNG/dnd.png")));
    private final Image invisible = new Image(String.valueOf(Client.class.getResource("PNG/invisible.png")));
    private final Image defProf = new Image(String.valueOf(Client.class.getResource("PNG/ico.png")));

    private UI ui;

    public void init(UI ui) {
        this.ui = ui;
        tg.selectToggle(online);

        EventHandler<ActionEvent> e = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ToggleButton t = (ToggleButton) actionEvent.getSource();
                if (t.isSelected()){
                    try {
                        ui.methodWrite(t.getText());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        };

        online.setOnAction(e);
        all.setOnAction(e);
        pending.setOnAction(e);
        blocked.setOnAction(e);
    }

    public void setChart(ArrayList<String> strings, HashMap<String, File> pics){
        Platform.runLater(() -> box.getChildren().clear());
        for (int i = 0; i < strings.size(); i++) {
            String[] strs = strings.get(i).split(" : ");
            String s = strings.get(i);
            String user = strs[0];
            String status = strs[1];

            Platform.runLater(() -> box.getChildren().add(userCreateBox(user, status,pics.get(s))));
        }
    }

    private Pane userCreateBox (String user, String status , File pic){
        Pane box = new Pane();

        box.setMinHeight(Region.USE_PREF_SIZE);
        box.setMaxHeight(Region.USE_PREF_SIZE);
        box.setMinWidth(Region.USE_PREF_SIZE);
        box.setMaxWidth(Region.USE_PREF_SIZE);

        box.setPadding(new Insets(10,10,10,10));

        box.getStyleClass().add("tableItem");

        Text text = new Text(user);
        text.getStyleClass().add("tableText");

        ImageView imageView = new ImageView();

        switch (status) {
            case "Online":
                imageView.setImage(onlineP);
                break;
            case "Idle":
                imageView.setImage(idle);
                break;
            case "Do Not Disturb":
                imageView.setImage(dnd);
                break;
            case "Invisible":
                imageView.setImage(invisible);
                break;
            case "offline":
                imageView.setImage(offline);
                break;
        }

        ImageView prof = new ImageView();
        if (pic != null) {
            prof.setImage(new Image(pic.toURI().toString()));
        } else {
            prof.setImage(defProf);
        }

        prof.setFitWidth(40);
        prof.setFitHeight(40);

        imageView.setFitHeight(15);
        imageView.setFitWidth(15);

        box.getChildren().add(text);
        box.getChildren().add(imageView);
        box.getChildren().add(prof);

        text.setLayoutX(60);
        text.setLayoutY(50);

        imageView.setLayoutX(350);
        imageView.setLayoutY(35);

        prof.setLayoutX(10);
        prof.setLayoutY(25);

        return box;
    }
}

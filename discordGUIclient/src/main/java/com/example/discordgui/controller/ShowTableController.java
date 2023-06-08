package com.example.discordgui.controller;

import com.example.discordgui.Client;
import com.example.discordgui.UI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ShowTableController {
    private UI ui;
    private final Image online = new Image(String.valueOf(Client.class.getResource("PNG/online.png")));
    private final Image offline = new Image(String.valueOf(Client.class.getResource("PNG/offline.png")));
    private final Image idle = new Image(String.valueOf(Client.class.getResource("PNG/idle.png")));
    private final Image dnd = new Image(String.valueOf(Client.class.getResource("PNG/dnd.png")));
    private final Image invisible = new Image(String.valueOf(Client.class.getResource("PNG/invisible.png")));
    private final Image accepted = new Image(String.valueOf(Client.class.getResource("PNG/accepted.png")));
    private final Image rejected = new Image(String.valueOf(Client.class.getResource("PNG/rejected.png")));
    private final Image pending = new Image(String.valueOf(Client.class.getResource("PNG/pending.png")));
    private final Image defProf = new Image(String.valueOf(Client.class.getResource("PNG/ico.png")));

    @FXML
    private Text header;

    @FXML
    private VBox table;

    @FXML
    void exit(ActionEvent event) throws IOException {
        ui.methodWrite("exit");
    }

    public void init (ArrayList<String> strings, String mode,HashMap<String, File> pics) {
        if (mode.equals("mems")) {
            header.setText("Server Members");
            for (int i = 0; i < strings.size(); i++) {
                String[] strs = strings.get(i).split(" : ");
                String s = strings.get(i);
                String user = strs[0];
                String status = strs[1];

                table.getChildren().add(userCreateBox(user, status,pics.get(s)));
            }
        } else if (mode.equals("reqs")) {
            header.setText("Friend Requests");
            for (int i = 0; i < strings.size(); i++) {
                String[] strs = strings.get(i).split(" : ");
                String s = strings.get(i);
                String user = strs[0];
                String status = strs[1];

                table.getChildren().add(reqCreateBox(user, status,pics.get(s)));
            }
        } else if (mode.equals("inv")){
            header.setText("Invite your Friends");
            for (int i = 0; i < strings.size(); i++) {
                String[] strs = strings.get(i).split(" : ");
                String s = strings.get(i);
                String user = strs[0];
                String status = strs[1];

                table.getChildren().add(invCreateBox(user, status,pics.get(s)));
            }
        }
    }

    private Pane invCreateBox (String user,String status,File pic){
        Pane box = new Pane();

        box.setMinHeight(Region.USE_PREF_SIZE);
        box.setMaxHeight(Region.USE_PREF_SIZE);
        box.setMinWidth(Region.USE_PREF_SIZE);
        box.setMaxWidth(Region.USE_PREF_SIZE);

        box.setPadding(new Insets(10,10,10,10));

        box.getStyleClass().add("tableItem");

        Text text = new Text(user);
        text.getStyleClass().add("tableText");

        Button button = new Button(status);

        switch (status) {
            case "Sent":
            case "Member":
                button.getStyleClass().add("InviteDis");
                button.setDisable(true);
                break;
            case "Invite":
                button.getStyleClass().add("invite");
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

        box.getChildren().add(text);
        box.getChildren().add(button);
        box.getChildren().add(prof);

        text.setLayoutX(60);
        text.setLayoutY(50);

        button.setLayoutX(320);
        button.setLayoutY(30);

        prof.setLayoutX(10);
        prof.setLayoutY(25);

        if (status.equals("Invite")) {
            button.setOnAction(actionEvent -> {
                try {
                    ui.methodWrite(user);
                    button.setText("Sent");
                    button.getStyleClass().clear();
                    button.getStyleClass().add("InviteDis");
                    button.setDisable(true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return box;
    }

    private Pane userCreateBox (String user, String status,File pic){
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
                imageView.setImage(online);
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

    private Pane reqCreateBox(String req,String status , File pic){
        Pane box = new Pane();

        box.setMinHeight(Region.USE_PREF_SIZE);
        box.setMaxHeight(Region.USE_PREF_SIZE);
        box.setMinWidth(Region.USE_PREF_SIZE);
        box.setMaxWidth(Region.USE_PREF_SIZE);

        box.setPadding(new Insets(10,10,10,10));

        box.getStyleClass().add("tableItem");

        Text text = new Text(req);
        text.getStyleClass().add("tableText");

        ImageView imageView = new ImageView();

        switch (status) {
            case "Accepted":
                imageView.setImage(accepted);
                break;
            case "Rejected":
                imageView.setImage(rejected);
                break;
            case "Pending":
                imageView.setImage(pending);
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

        imageView.setFitHeight(30);
        imageView.setFitWidth(30);

        box.getChildren().add(text);
        box.getChildren().add(imageView);
        box.getChildren().add(prof);

        text.setLayoutX(60);
        text.setLayoutY(50);

        imageView.setLayoutX(350);
        imageView.setLayoutY(35);

        prof.setLayoutX(10);
        prof.setLayoutY(25);

        if (status.equals("Pending")) {
            box.setOnMouseClicked(mouseEvent -> {
                try {
                    ui.methodWrite(req);
                    table.getChildren().remove(box);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return box;
    }

    public void setUi(UI ui) {
        this.ui = ui;
    }
}

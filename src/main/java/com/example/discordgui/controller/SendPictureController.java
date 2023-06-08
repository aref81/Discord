package com.example.discordgui.controller;

import com.example.discordgui.UI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class SendPictureController {

    private UI ui;

    public void init (UI ui, File pic){
        this.ui = ui;
        loading.setVisible(false);
        path.setText("");
        if (pic != null){
            prof.setImage(new Image(pic.toURI().toString()));
        }
    }

    @FXML
    private ImageView loading;

    @FXML
    private TextField path;

    @FXML
    private ImageView prof;

    @FXML
    void back(ActionEvent event) {
        try {
            ui.methodWrite("#exit");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    void file(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter ef = new FileChooser.ExtensionFilter("Picture","*.png","*.jpg","*.svg");
        fileChooser.getExtensionFilters().add(ef);
        fileChooser.setSelectedExtensionFilter(ef);

        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null && file.exists()){
            path.setText(file.getAbsolutePath());
        }
    }

    @FXML
    void send(ActionEvent event) throws IOException {
        if (!path.getText().isEmpty()){
            File file = new File(path.getText());
            if (file.exists()){
                loading.setVisible(true);
                Thread t = new Thread(() -> {
                    try {
                        ui.methodWrite(file.getName());
                        ui.sendFile(file);
                        Platform.runLater(() -> loading.setVisible(false));
                        ui.sendPopUp("File Sent",file.getName() + " has been Sent Successfully.");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                t.start();
            }
        } else {
            ui.sendPopUp("Error","File not Found.");
        }
    }
}

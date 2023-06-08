package com.example.discordgui.controller;

import com.example.discordgui.Client;
import com.example.discordgui.UI;
import com.example.discordgui.chat.ReadThread;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class ChatPageController {

    private UI ui;
    private ReadThread readThread;
    private HashMap<Long,MessageController> messages;
    private HashSet<String> isTypings;


    @FXML
    private VBox chat;

    @FXML
    private ImageView avatar;

    @FXML
    private TextField field;

    @FXML
    private Text name;

    @FXML
    private HBox istyping;

    @FXML
    void attach(ActionEvent event) throws IOException {
        ui.methodWrite("#sendFile");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter ef = new FileChooser.ExtensionFilter("All Files","*.*");
        fileChooser.getExtensionFilters().add(ef);
        fileChooser.setSelectedExtensionFilter(ef);

        File file = fileChooser.showOpenDialog(new Stage());

        if (file.exists()){
            Thread t = new Thread(() -> {
                try {
                    ui.methodWrite(file.getName());
                    ui.sendFile(file);
                    ui.sendPopUp("File Sent",file.getName() + " has been Sent Successfully.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            t.start();
        } else {
            ui.sendPopUp("Error","File not Found.");
        }
    }

    @FXML
    void send(ActionEvent event) throws IOException {
        String str = field.getText();
        ui.methodWrite(str);
        field.setText("");
    }

    @FXML
    void back(ActionEvent event) throws IOException {
        sendCommand("#exit");
    }

    @FXML
    void getpin(ActionEvent event) throws IOException {
        sendCommand("#getpm");
    }

    @FXML
    void typing(KeyEvent event) throws IOException {
        if (event.getCode().equals(KeyCode.ENTER)){
            send(null);
        } else {
            ui.methodWrite("$");
        }
    }

    public void like (Long id,String user){
        messages.get(id).addLike(user);
    }

    public void dislike (Long id,String user){
        messages.get(id).addDislike(user);
    }

    public void laughter (Long id,String user){
        messages.get(id).addLaughter(user);
    }

    public void sendCommand(String str) throws IOException {
        ui.methodWrite(str);
    }

    public void download (String str) throws IOException {
        ui.methodWrite(str);
        Thread t = new Thread(() -> {
            final String name;
            try {
                name = ui.receiveFileName();
                File file = new File("./ClientContent/" + name);
                ui.receiveFile(file);
                ui.sendPopUp("File Received",file.getName() + " has been Received Successfully.");

                ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", file.getAbsolutePath());
                builder.redirectErrorStream(true);
                Process p = builder.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
    }


    public void init (File avatar,String name){
        if (avatar != null && avatar.exists()){
            this.avatar.setImage(new Image(avatar.toURI().toString()));
        }
        this.name.setText(name);
        readThread = new ReadThread(ui,this);
        messages = new HashMap<>();
        isTypings = new HashSet<>();
    }

    public void start() {
        readThread.run();
    }

    public void newMessage (String str) throws IOException {
        if (str.charAt(0) == '|'){
            TextFlow f = new TextFlow();
            f.setMinHeight(Region.USE_PREF_SIZE);
            f.setMaxHeight(Region.USE_PREF_SIZE);
            f.setMinWidth(Region.USE_PREF_SIZE);
            f.setMaxWidth(Region.USE_PREF_SIZE);

            f.prefHeight(50);
            f.prefWidth(100);

            Text text = new Text(str.substring(1));
            text.setStyle("-fx-font-size: 18; -fx-fill: GREY");

            f.getChildren().add(text);
            Platform.runLater(() -> chat.getChildren().add(f));
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("FXML/Message.fxml"));
            BorderPane pane = fxmlLoader.load();
            MessageController messageController = fxmlLoader.getController();
            messageController.init(str, this);
            messages.put(messageController.getId(), messageController);
            Platform.runLater(() -> chat.getChildren().add(pane));
        }
    }


    public void addIsTyping (String str) {
        if (!isTypings.contains(str)) {
            Text text = new Text(str + ",");
            text.getStyleClass().add("notif");
            Thread thread = new Thread(() -> {
                try {
                    isTypings.add(str);
                    Platform.runLater(() -> istyping.getChildren().add(text));
                    Thread.sleep(1000);
                    Platform.runLater(() -> istyping.getChildren().remove(text));
                    isTypings.remove(str);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
        }
    }


    public void setUi(UI ui) {
        this.ui = ui;
    }
}

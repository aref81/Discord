package com.example.discordgui;

import com.example.discordgui.controller.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class UI {
    private Stage stage;
    private ClientController clientController;
    private DynamicFormController dynamicFormController;
    private FriendsChartController friendsChartController;

    private Pane root;
    private BorderPane current;

    public UI(Stage stage,ClientController clientController) {
        this.stage = stage;
        this.clientController=clientController;
        root = new Pane();

        root.setMaxHeight(Region.USE_PREF_SIZE);
        root.setMinHeight(Region.USE_PREF_SIZE);
        root.setPrefHeight(600);

        root.setMaxWidth(Region.USE_PREF_SIZE);
        root.setMinWidth(Region.USE_PREF_SIZE);
        root.setPrefWidth(450);

        Scene scene = new Scene(root,450,600);

        stage.setScene(scene);
    }

    public void showMenu(ArrayList<String> items) throws IOException {
        dynamicFormController = null;
        friendsChartController = null;
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("FXML/DinamicMenu.fxml"));
        BorderPane borderPane = fxmlLoader.load();
        DinamicMenuController dinamicMenuController = fxmlLoader.getController();
        dinamicMenuController.setUi(this);
        dinamicMenuController.setItems(items);
        changeStage(borderPane);
    }

    public void welcome(String str) throws IOException {
        dynamicFormController = null;
        friendsChartController = null;
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("FXML/Welcome.fxml"));
        BorderPane borderPane = fxmlLoader.load();
        WelcomeController welcomeController = fxmlLoader.getController();
        welcomeController.setText(str);
        Platform.runLater(() -> {
            root.getChildren().add(borderPane);
            borderPane.setLayoutX(0);
            borderPane.setLayoutY(0);
            current = borderPane;
            if (!stage.isShowing()) {
                stage.show();
            }
        });
    }

    public void getInfo (String header,String req) throws IOException {
        if (dynamicFormController == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("FXML/DynamicForm.fxml"));
            BorderPane borderPane = fxmlLoader.load();
            dynamicFormController = fxmlLoader.getController();
            dynamicFormController.setUi(this);
            dynamicFormController.init(header,req);
            changeStage(borderPane);
        } else {
            dynamicFormController.next(header,req);
        }
    }

    public void getAgain (String str) {
        dynamicFormController.again(str);
    }

    public void resetMenu () {
        dynamicFormController = null;
    }

    public void chatMode () throws IOException {
        dynamicFormController = null;
        friendsChartController = null;
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("FXML/ChatPage.fxml"));
        BorderPane borderPane = fxmlLoader.load();
        ChatPageController chatPageController = fxmlLoader.getController();
        chatPageController.setUi(this);
        String header = methodRead();
        File file = null;
        if (methodRead().equals("pic")){
            file = new File("./Profiles/" + receiveFileName());
            receiveFile(file);
        }
        chatPageController.init(file,header);
        changeStage(borderPane);
        chatPageController.start();
    }

    public void Table (int num) throws IOException {
        String mode = methodRead();
        dynamicFormController = null;
        friendsChartController = null;
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("FXML/ShowTable.fxml"));
        BorderPane borderPane = fxmlLoader.load();
        ShowTableController showTableController = fxmlLoader.getController();
        showTableController.setUi(this);
        ArrayList<String> strings = new ArrayList<>(num);
        HashMap<String,File> pics = new HashMap<>();
        for (int i = 0; i < num; i++) {
            String str = methodRead();
            strings.add(str);
            if (methodRead().equals("pic")){
                File file = new File("./Profiles/" + receiveFileName());
                receiveFile(file);
                pics.put(str,file);
            }
        }
        showTableController.init(strings,mode,pics);
        changeStage(borderPane);
    }

    public void getProfilePicture() throws IOException {
        dynamicFormController = null;
        friendsChartController = null;
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("FXML/SendPicture.fxml"));
        BorderPane borderPane = fxmlLoader.load();
        SendPictureController sendPictureController = fxmlLoader.getController();
        File file = null;
        if (methodRead().equals("pic")){
            file = new File("./Profiles/" + receiveFileName());
            receiveFile(file);
        }
        sendPictureController.init(this,file);
        changeStage(borderPane);
    }

    public void showFriendsChart() throws IOException{
        dynamicFormController = null;
        if (friendsChartController == null){
            FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("FXML/FriendsChart.fxml"));
            BorderPane borderPane = fxmlLoader.load();
            friendsChartController = fxmlLoader.getController();
            friendsChartController.init(this);
            changeStage(borderPane);
        }

        int num = Integer.parseInt(methodRead());
        ArrayList<String> strings = new ArrayList<>(num);
        HashMap<String,File> pics = new HashMap<>();
        for (int i = 0; i < num; i++) {
            String str = methodRead();
            strings.add(str);
            if (methodRead().equals("pic")){
                File file = new File("./Profiles/" + receiveFileName());
                receiveFile(file);
                pics.put(str,file);
            }
        }
        friendsChartController.setChart(strings,pics);
    }

    public void ProfilePage () throws IOException {
        dynamicFormController = null;
        friendsChartController = null;
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("FXML/ProfilePage.fxml"));
        BorderPane borderPane = fxmlLoader.load();
        ProfilePageController profilePageController = fxmlLoader.getController();
        String userName = methodRead();
        String email = methodRead();
        String phone = methodRead();
        String status = methodRead();
        File file = null;
        if (methodRead().equals("pic")){
            file = new File("./Profiles/" + receiveFileName());
            receiveFile(file);
        }

        profilePageController.Init(this,userName,email,phone,status,file);
        changeStage(borderPane);
    }

    public void login() throws IOException {
        dynamicFormController=null;
        friendsChartController=null;
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("FXML/Login.fxml"));
        BorderPane borderPane = fxmlLoader.load();
        LoginController loginController = fxmlLoader.getController();
        loginController.init(this);
        changeStage(borderPane);
    }
    public void signUp() throws IOException {
        dynamicFormController=null;
        friendsChartController=null;
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("FXML/SignUp.fxml"));
        BorderPane borderPane = fxmlLoader.load();
        SignUpController signUpController= fxmlLoader.getController();
        signUpController.init(this);
        changeStage(borderPane);
    }

    private void changeStage (BorderPane borderPane){
        Platform.runLater(() -> {
            WritableImage wi = new WritableImage(450, 600);
            ImageView imageView = new ImageView(root.snapshot(new SnapshotParameters(), wi));
            root.getChildren().add(imageView);
            imageView.setLayoutX(0);
            imageView.setLayoutY(0);

            root.getChildren().add(borderPane);
            borderPane.setTranslateY(current.getTranslateY() + 600);
            borderPane.toFront();

            TranslateTransition t = new TranslateTransition(Duration.seconds(0.4),borderPane);
            t.setToY(current.getTranslateY());
            t.setOnFinished(actionEvent -> {
                root.getChildren().remove(imageView);
                root.getChildren().remove(current);
                current = borderPane;
            });
            t.play();
        });
    }



    public void sendFile (File file) {
        clientController.getFileStreamThread().sendFile(file);
    }

    public void receiveFile(File file){
        clientController.getFileStreamThread().receiveFile(file);
    }

    public String receiveFileName() throws IOException {
        return clientController.getFileStreamThread().methodRead();
    }

    public void sendPopUp (String title,String desc){
        try {
            clientController.getNotificationThread().showPopup(title, desc);
        } catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
    public String methodRead () throws IOException {
        return clientController.methodRead();
    }

    public void methodWrite (String str) throws IOException {
        clientController.methodWrite(str);
    }

}

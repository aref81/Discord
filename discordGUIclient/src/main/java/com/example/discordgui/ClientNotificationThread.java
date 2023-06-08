package com.example.discordgui;

import com.example.discordgui.controller.PopUpController;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class ClientNotificationThread implements Runnable{
    private Socket socket;
    private BufferedReader in;

    public ClientNotificationThread() throws IOException {
        socket = new Socket("localhost",8787);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            String com = methodRead();
            while (com!= null && !com.equals("exit")){
                showPopup(methodRead(),methodRead());
                methodRead();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String methodRead () throws IOException {
        String str = in.readLine();
        while (str != null && str.equals("")){
            str = in.readLine();
        }
        return str;
    }


    public synchronized void showPopup (String title,String desc) throws IOException, InterruptedException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("FXML/PopUpDialoge.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),400,150);
        PopUpController controller = fxmlLoader.getController();
        controller.setTitle(title);
        controller.setDesc(desc);
        AtomicReference<Stage> stage = new AtomicReference<>();
        Platform.runLater(() -> {
            stage.set(new Stage());
            stage.get().setX(Screen.getPrimary().getVisualBounds().getMinX() + Screen.getPrimary().getVisualBounds().getWidth());
            stage.get().setY(Screen.getPrimary().getVisualBounds().getMinY() + Screen.getPrimary().getVisualBounds().getHeight() - 150);
            stage.get().initStyle(StageStyle.UNDECORATED);
            stage.get().setAlwaysOnTop(true);
            stage.get().setScene(scene);
            stage.get().show();
        });

        Animation transition = new Transition() {
            @Override
            protected void interpolate(double v) {
                stage.get().setX(stage.get().getX() - 9);
            }

            {
                setCycleDuration(Duration.millis(400));
                setInterpolator(Interpolator.EASE_IN);
            }
        };
        transition.play();

        Thread.sleep(5*1000);
        Platform.runLater(() -> {
            stage.get().close();
        });
    }
}

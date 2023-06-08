package com.example.discordgui;

import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class ClientController {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private UI ui;
    private ClientNotificationThread notificationThread;
    private ClientFileStreamThread fileStreamThread;

    public ClientController() throws IOException {
        this.socket = new Socket("localhost",8989);
        this.notificationThread = new ClientNotificationThread();
        this.fileStreamThread = new ClientFileStreamThread();
        reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void start(){
        Thread thread = new Thread(notificationThread);
        thread.start();

        System.out.println("Connected");
        try {
            String str = methodRead();
            ui.welcome(str);
            boolean end=false;
            while (!end) {
                Command command = Command.valueOfLabel(methodRead());
                if (command == null) {
                    continue;
             }

                switch (command) {
                    case GETUSERNAME: {
                        ui.getInfo("Please enter your user name","User Name");
                        break;
                    }
                    case GETUSERNAMEAGAIN:
                    case GETPASSWORDAGAIN:
                    case GETEMAILAGAIN:
                    case GETROLENAMEAGAIN:
                    case GETCHANNELNAMEAGAIN:
                    case GETSERVERNAMEAGAIN:
                    case GETPHONEAGAIN: {
                        ui.getAgain(methodRead());
                        break;
                    }
                    case GETPASSWORD: {
                        ui.getInfo("Please enter your password","Password");
                        break;
                    }
                    case GETEMAIL: {
                        ui.getInfo("Please enter your e-mail","E-Mail");
                        break;
                    }
                    case PRINT: {
                        ui.sendPopUp("INFO",methodRead());
                        break;
                    }
                    case SHOWMENU: {
                        int num = Integer.parseInt(methodRead());
                        ArrayList<String> items = new ArrayList<>(num);
                        for (int i = 0; i < num; i++) {
                            items.add(methodRead());
                        }
                        ui.showMenu(items);
                        break;
                    } case PRINTWELLCOME:{
                        ui.welcome(methodRead());
                        break;
                    }
                    case RESETMENU:{
                        ui.resetMenu();
                        break;
                    }
                    case CREATEFRIEND: {
                        ui.getInfo("Please enter desired user name","User Name");
                        break;
                    }
                    case ENTERCHATMODE: {
                        ui.chatMode();
                        break;
                    }
                    case GETSERVERNAME: {
                        ui.getInfo("Please enter your server name","Server Name");
                        break;
                    }
                    case GETWELLCOME:{
                        ui.getInfo("Please enter your welcome message","Message");
                        break;
                    }
                    case GETROLENAME: {
                        ui.getInfo("Please enter your Role name","Role Name");
                        break;
                    }
                    case GETCHANNELNAME: {
                        ui.getInfo("Please enter your Channel name","Channel Name");
                        break;
                    }
                    case GETPHONE: {
                        ui.getInfo("Please enter your Phone number","Phone Number");
                        break;
                    }
                    case GETPROFILEPICTURE: {
                    ui.getProfilePicture();
                        break;
                    }
                    case GETTABLE:{
                        ui.Table(Integer.parseInt(methodRead()));
                        break;
                    }
                    case PROFILEPAGE:{
                        ui.ProfilePage();
                        break;
                    }
                    case SHOWFRIENDSCHART_ALL:
                    case SHOWFRIENDSCHART_ONLINE:
                    case SHOWFRIENDSCHART_BLOCKED:
                    case SHOWFRIENDSCHART_PENDING:{
                        ui.showFriendsChart();
                        break;
                    }
                    case LOGIN:{
                        ui.login();
                        break;
                    }
                    case SIGNUP:{
                        ui.signUp();
                        break;
                    }
                    case EXIT: {
                        System.exit(0);
                        break;
                    }

                }
            }
        } catch (IOException e){
            System.err.println("Server Unreachable!");
            System.exit(-1);
        }
    }

    public ClientNotificationThread getNotificationThread() {
        return notificationThread;
    }

    public ClientFileStreamThread getFileStreamThread() {
        return fileStreamThread;
    }

    public String methodRead () throws IOException {
        String str = reader.readLine();
        while (str.equals("")){
            str = reader.readLine();
        }
        return str;
    }

    public void methodWrite (String str) throws IOException {
        writer.write(str);
        writer.newLine();
        writer.flush();
    }

    public void setStage(Stage stage){
        this.ui=new UI(stage,this);
    }

}


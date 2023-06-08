package com.discord.client;

import com.discord.Command;
import com.discord.client.chat.ReadThread;
import com.discord.client.chat.WriteThread;
import com.discord.server.UserThread;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private static BufferedReader reader;
    private static BufferedWriter writer;
    private static Socket socket;
    private static ClientFileStream fileStream;

    public static void main(String[] args) {
        try {
            socket=new Socket("localhost",8989);
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            fileStream = new ClientFileStream();
            System.out.println("Connected");
            String str = methodRead();
            UI.welcome(str);
            boolean end=false;
            while (!end) {
                Command command=Command.valueOfLabel(methodRead());
                String respond = null;
                if (command == null){
                    continue;
                }

                switch (command){
                    case GETUSERNAME:{
                        respond = UI.getUserName();
                        break;
                    }
                    case GETUSERNAMEAGAIN:{
                        respond=UI.getUserName(methodRead());
                        break;
                    }
                    case GETPASSWORD:{
                        respond=UI.getPassword();
                        break;
                    }
                    case GETPASSWORDAGAIN:{
                        respond=UI.getPassword(methodRead());
                        break;
                    }
                    case GETEMAIL:{
                        respond=UI.getEmail();
                        break;
                    }
                    case GETEMAILAGAIN:{
                        respond=UI.getEmail(methodRead());
                        break;
                    }
                    case PRINT:{
                        UI.print(methodRead());
                        break;
                    }
                    case SHOWMENU:{
                        int num=Integer.parseInt(methodRead());
                        StringBuilder sb=new StringBuilder();
                        for (int i = 0; i < num; i++) {
                            sb.append(methodRead());
                            sb.append("\n");
                        }
                        respond=UI.ShowMenu(sb.toString());
                        break;
                    }
                    case CREATEFRIEND:{
                        respond=UI.getCreateFriend();
                        break;
                    }
                    case ENTERCHATMODE:{
                        chatMode();
                        break;
                    }
                    case GETSERVERNAME:{
                        methodWrite(UI.getServerName());
                        respond = UI.getWelcome();
                        break;
                    }
                    case GETSERVERNAMEAGAIN:{
                        methodWrite(UI.getServerName(methodRead()));
                        respond = UI.getWelcome();
                        break;
                    }
                    case GETROLENAME:{
                        respond=UI.getRoleName();
                        break;
                    }
                    case GETROLENAMEAGAIN:{
                        respond=UI.getRoleName(methodRead());
                        break;
                    }
                    case GETCHANNELNAME:{
                        respond=UI.getChannelName();
                        break;
                    }
                    case GETCHANNELNAMEAGAIN:{
                        respond=UI.getChannelName(methodRead());
                        break;
                    }
                    case GETPHONE:{
                        respond=UI.getPhone();
                        break;
                    }
                    case GETPHONEAGAIN:{
                        respond=UI.getPhone(methodRead());
                        break;
                    }
                    case GETPROFILEPICTURE:{
                        fileStream.sendFile(UI.getProfilePicture());
                        break;
                    }
                    case EXIT:{
                        UI.exit();
                        System.exit(0);
                        break;
                    }

                }


                if (respond != null) {
                    writer.write(respond);
                    writer.newLine();
                    writer.flush();
                }
            }
        } catch (IOException e){
            System.err.println("Server Unreachable!");
            System.exit(-1);
        }
    }

    private static void chatMode(){
        WriteThread writeThread = new WriteThread(writer,fileStream);
        Thread thread = new Thread(writeThread);
        thread.start();

        ReadThread readThread = new ReadThread(reader,fileStream);
        readThread.run();

        if (thread.isAlive()){
            thread.interrupt();
        }
    }

    private static String methodRead () throws IOException {
        String str = reader.readLine();
        while (str.equals("")){
            str = reader.readLine();
        }
        return str;
    }

    private static void methodWrite (String str) throws IOException {
        writer.write(str);
        writer.newLine();
        writer.flush();
    }
}


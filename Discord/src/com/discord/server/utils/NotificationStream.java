package com.discord.server.utils;

import java.io.*;
import java.net.Socket;

public class NotificationStream {

    private Socket socket;

    private ObjectOutputStream out;

    public NotificationStream(Socket socket) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//        writer.write("created");
//        writer.flush();
    }

    public void sendPopUp (String title,String desc){
        try {
            out.writeUTF("popUp");
            out.flush();
            out.writeUTF(title);
            out.flush();
            out.writeUTF(desc);
            out.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void close () {
        try {
            out.writeUTF("exit");
            out.flush();
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

package com.example.discordgui;

import java.io.*;
import java.net.Socket;

public class ClientFileStreamThread{

    private Socket socket;
    private DataOutputStream out;

    private DataInputStream in;


    public ClientFileStreamThread() throws IOException {
        socket = new Socket("localhost",8888);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public synchronized void sendFile (File file) {
        try {
            if (file != null && file.exists()) {
                int bytes = 0;

                FileInputStream fileInputStream = new FileInputStream(file);
                out.writeLong(file.length());

                byte[] buf = new byte[8*1024];
                while ((bytes=fileInputStream.read(buf))!=-1){
                    out.write(buf,0,bytes);
                    out.flush();
                }
                fileInputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void receiveFile (File file) {
        try {
            if (file.exists()){
                file.delete();
            }
            file.createNewFile();
            int bytes = 0;
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            long size = in.readLong();
            byte[] buf = new byte[8*1024];
            while (size > 0 && (bytes = in.read(buf, 0, (int) Math.min(buf.length, size))) != -1) {
                fileOutputStream.write(buf, 0, bytes);
                size -= bytes;
            }
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String methodRead () throws IOException {
        return in.readUTF();
    }
}

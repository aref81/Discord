package com.discord.client;

import com.discord.server.utils.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientFileStream {

    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;


    public ClientFileStream() {
        try {
            socket = new Socket("localhost",8888);
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(File file) throws IOException {
        DataOutputStream out=new DataOutputStream(socket.getOutputStream());
        FileInputStream in=new FileInputStream(file);
        try {
            methodWrite(file.getName());
            byte [] buf = new byte[10 * 1024];
            int count;
            long size = in.available();
            methodWrite(String.valueOf(size));
            methodRead();
            while ((count = in.read(buf)) != -1){
                out.write(buf,0,count);
                out.flush();
            }
            System.out.println("File sent");
            out.flush();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveFile () throws IOException {
        File file = new File(methodRead());
        file.createNewFile();

        FileOutputStream out = new FileOutputStream(file);
        DataInputStream in = new DataInputStream(socket.getInputStream());

        try {
            byte[] buf = new byte[10 * 1024];
            int size = Integer.parseInt(methodRead());
            methodWrite("a");
            int f = 0;
            int count;
            while (size > 0 && (count = in.read(buf, 0, (int)Math.min(buf.length,size))) != -1)
            {
                out.write(buf,0,count);
                f += count;
                size -= count;
            }
            System.out.println(f);
            System.out.println(file.getAbsolutePath() + " was Received.");
            out.flush();
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private String methodRead () throws IOException {
        String str = reader.readLine();
        while (str.equals("")){
            str = reader.readLine();
        }
        return str;
    }

    private void methodWrite (String str) throws IOException {
        writer.write(str);
        writer.newLine();
        writer.flush();
    }
}

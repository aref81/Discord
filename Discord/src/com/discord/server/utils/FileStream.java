package com.discord.server.utils;

import com.discord.server.utils.discordServer.Member;
import com.discord.server.utils.discordServer.channels.TextChannel;
import com.discord.server.utils.discordServer.channels.VoiceChannel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileStream implements Serializable{
    private User user;
    private transient Socket socket;
    private transient BufferedWriter writer;
    private transient BufferedReader reader;


    public FileStream(ServerSocket serverSocket) {
        try {
            socket = serverSocket.accept();
            this.writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public File receiveFile(TextChannel textChannel, Member member) throws IOException {
        String [] name = methodRead().split("\\.");
        File file = new File("./Data/" + textChannel.getName() + "_" + member.getUser().getUserName() + "_" + (textChannel.getHistory() + 1) + "." + name[name.length - 1]);

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
        return file;
    }

    public File receiveFile(VoiceChannel voiceChannel, Member member) throws IOException {
        String [] name = methodRead().split("\\.");
        String type = name[name.length - 1];
        if (!type.equals("mp3")){
            return null;
        }
        File file = new File("./Data/" + voiceChannel.getName() + "_" + member.getUser().getUserName() + "_" + (voiceChannel.getHistory() + 1) + "." + type);

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
        return file;
    }

    public File receiveFilePChat(PrivateChat privateChat) throws IOException {
        String [] name = methodRead().split("\\.");
        File file = new File("./Data/pChat/" + "_" + privateChat.getUser1().getUserName() + privateChat.getUser2().getUserName() + "_" + (privateChat.getHis() + 1) + "." + name[name.length - 1]);

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
        return file;
    }

    public File receiveFile() throws IOException {
        String [] name = methodRead().split("\\.");
        File file=new File("./ProfilePics/" + user.getUserName() + "." +name[name.length - 1]);
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
        return file;
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

    private String methodRead () throws IOException {
        String str = reader.readLine();
        while (str.equals("")){
            str = reader.readLine();
        }
        return str;
    }

    private void methodWrite (String str) throws IOException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        writer.write(str);
        writer.newLine();
        writer.flush();
    }
}

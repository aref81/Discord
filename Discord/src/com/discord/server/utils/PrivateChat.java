package com.discord.server.utils;

import com.discord.Command;
import com.discord.server.utils.chat.privateChat.PrivateChatReader;
import com.discord.server.utils.chat.privateChat.PrivateChatWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class PrivateChat implements Serializable {
    private User user1;
    private User user2;
    private HashMap<Long,Massage> massages;
    private HashMap<Massage, File> files;
    private transient ArrayList<PrivateChatWriter> observers;
    private  long keepId;
    private FileStream fileStream;

    public void sendMassage(Massage massage){
        massages.put(massage.getId(),massage);
        Thread t = new Thread(() -> update(massage));
        t.start();
    }

    public PrivateChat(User user1, User user2,FileStream fileStream) {
        this.user1 = user1;
        this.user2 = user2;
        this.massages = new HashMap<>();
        observers = new ArrayList<>();
        keepId = 0L;
        this.fileStream = fileStream;
        this.files = new HashMap<>();
    }

    public Long getId () {
        return keepId++;
    }

    public Long getHis(){
        return keepId;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public void startChat (BufferedWriter writer, BufferedReader reader,User user){
        try {
            writer.write(Command.ENTERCHATMODE.getStr());
            writer.newLine();
            writer.flush();

            User u = user.equals(user1) ? user2 : user1;

            writer.write(u.getUserName());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
        PrivateChatWriter privateChatWriter = new PrivateChatWriter(writer,user);
        addObserver(privateChatWriter);
        for (Long l: massages.keySet()) {
            privateChatWriter.Initbroadcast(massages.get(l));
        }
        PrivateChatReader privateChatReader = new PrivateChatReader(fileStream,reader,this,user);
        privateChatReader.run();
    }

    private void addObserver (PrivateChatWriter p){
        observers.add(p);
    }

    public void endUserChat (User user) {
        for (PrivateChatWriter w: observers) {
            w.end(user);
        }
        observers.removeIf(w -> w.getUser().equals(user));
    }

    private void update(Massage massage) {
        for (PrivateChatWriter w: observers) {
            w.broadcast(massage);
        }
    }

    public HashMap<Massage, File> getFiles() {
        return files;
    }

    public File getFileMessage (long id){
        Massage massage = massages.get(id);
        if (massage != null){
            File file = files.get(massage);
            if ((file != null) && (file.exists())){
                return file;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}

package com.discord.server.utils.chat.privateChat;

import com.discord.Command;
import com.discord.server.utils.FileStream;
import com.discord.server.utils.Massage;
import com.discord.server.utils.PrivateChat;
import com.discord.server.utils.User;
import com.discord.server.utils.exceptions.WrongFormatException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class PrivateChatReader implements Runnable, Serializable {
    private transient BufferedReader reader;
    private transient PrivateChat privateChat;
    private FileStream fileStream;
    private User user;


    public PrivateChatReader(FileStream fileStream, BufferedReader reader, PrivateChat privateChat, User user) {
        this.reader = reader;
        this.privateChat = privateChat;
        this.user = user;
        this.fileStream = fileStream;
    }

    @Override
    public void run() {
        try {
            String str = methodRead();
            while (!str.equals("#exit")){
                try {
                    if (str.equals("#sendFile")) {
                        File file = fileStream.receiveFilePChat(privateChat);
                        str = user.getUserName() + " has sent a file to chat -- #download";
                        Massage massage = new Massage(str, user, privateChat.getId());
                        privateChat.sendMassage(massage);
                        privateChat.getFiles().put(massage, file);
                    } else if (str.contains("#download")) {
                        String[] strings = str.split("-");
                        long id = Long.parseLong(strings[1]);
                        File file = privateChat.getFileMessage(id);
                        fileStream.sendFile(file);
                    } else {
                        privateChat.sendMassage(new Massage(str, user, privateChat.getId()));
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
                    System.err.println(user.getUserName() + " sent an unacceptable command");
                }
                str = methodRead();
            }
            privateChat.endUserChat(user);
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String methodRead () throws IOException {
        String str = reader.readLine();
        while (str.equals("")){
            str = reader.readLine();
        }
        return str;
    }
}

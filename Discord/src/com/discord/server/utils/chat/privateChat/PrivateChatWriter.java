package com.discord.server.utils.chat.privateChat;

import com.discord.Command;
import com.discord.server.utils.Massage;
import com.discord.server.utils.PrivateChat;
import com.discord.server.utils.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;

public class PrivateChatWriter implements Serializable {
    private transient BufferedWriter writer;
    private User user;


    public PrivateChatWriter(BufferedWriter writer, User user) {
        this.writer = writer;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void broadcast (Massage massage) {
        try {
            methodWrite(massage.toString());
        } catch (IOException e){
            System.err.println("Couldn't Send Message");
        }
    }

    public void Initbroadcast (Massage massage) {
        try {
            methodWrite(massage.toString());
        } catch (IOException e){
            System.err.println("Couldn't Send Message");
        }
    }

    public void end (User user) {
        try {
            if (this.user.equals(user)) {
                methodWrite(Command.EXITCHATMODE.getStr());
            }
        } catch (IOException e){
            System.err.println("Couldn't Send Message");
        }
    }

    private void methodWrite (String str) throws IOException {
        writer.write(str);
        writer.newLine();
        writer.flush();
    }
}

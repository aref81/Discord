package com.discord.server.utils.chat.channel;

import com.discord.Command;
import com.discord.server.utils.FileStream;
import com.discord.server.utils.Massage;
import com.discord.server.utils.User;
import com.discord.server.utils.discordServer.Member;
import com.discord.server.utils.discordServer.channels.TextChannel;
import com.discord.server.utils.exceptions.WrongFormatException;

import java.io.*;
import java.util.ArrayList;

public class ChannelChatIO implements Runnable, Serializable {
    private transient BufferedWriter writer;
    private transient BufferedReader reader;
    private Member member;
    private TextChannel textChannel;
    private FileStream fileStream;

    public ChannelChatIO(FileStream fileStream,BufferedWriter writer, BufferedReader reader, Member member, TextChannel textChannel) {
        this.writer = writer;
        this.reader = reader;
        this.member = member;
        this.textChannel = textChannel;
        this.fileStream = fileStream;
    }

    @Override
    public void run() {
        try {
            String str = methodRead();
            while (!str.equals("#exit")){
                try {
                    if (str.contains("#like")) {
                        String[] strings = str.split("-");
                        long id = Long.parseLong(strings[1]);
                        textChannel.like(id,member.getUser());
                    } else if (str.contains("#dislike")){
                        String[] strings = str.split("-");
                        long id = Long.parseLong(strings[1]);
                        textChannel.dislike(id,member.getUser());
                    }else if (str.contains("#laughter")){
                        String[] strings = str.split("-");
                        long id = Long.parseLong(strings[1]);
                        textChannel.laughter(id,member.getUser());
                    }else if (str.contains("#pin")){
                        String[] strings = str.split("-");
                        long id = Long.parseLong(strings[1]);
                        textChannel.pin(id,member.getUser());
                    } else if (str.contains("#getpm")){
                        textChannel.getPinned(this);
                    } else if (str.equals("#sendFile")){
                        Thread t = new Thread(new Runnable() {
                            final String name = methodRead();
                            @Override
                            public void run() {
                                File file = new File("./ChatContent/" + member.getUser().getUserName() + "_T_" + (textChannel.getHistory()) + "_" + name);
                                fileStream.receiveFile(file);
                                String s = member.getUser().getUserName() + " has sent a file"+ file.getName() + " to chat -- #download";
                                Massage massage = new Massage(s, member.getUser(), textChannel.getId());
                                textChannel.sendMassage(massage);
                                textChannel.getFiles().put(massage, file);
                            }
                        });
                        t.start();
                    } else if (str.contains("#download")){
                        String[] strings = str.split("-");
                        long id = Long.parseLong(strings[1]);
                        File file = textChannel.getFileMessage(id);
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    fileStream.methodWrite(file.getName());
                                    fileStream.sendFile(file);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                        t.start();
                    }else if (str.contains("@")){
                        String userName=str.substring(str.indexOf("@")+1,(str.indexOf(" ",str.indexOf("@"))==-1) ? str.length() : str.indexOf(" ",str.indexOf("@")));
                        ArrayList<Member> members=new ArrayList<>(textChannel.getTags().keySet());
                        for (Member m:members) {
                            if (m.getUser().getUserName().equals(userName)){
                                textChannel.getTags().replace(m,textChannel.getTags().get(m)+1);
                                if (m.getUser().getUserThread()!=null){
                                    m.getUser().getUserThread().sendPopUp("You Were Tagged",member.getUser().getUserName()+" tagged you in "+textChannel.getName());
                                }
                                break;
                            }
                        }
                        textChannel.sendMassage(new Massage(str,member.getUser(),textChannel.getId()));
                    } else if (str.equals("$")){
                        textChannel.sendIsTyping(member.getUser());
                    }
                    else {
                        textChannel.sendMassage(new Massage(str,member.getUser(),textChannel.getId()));
                    }
                } catch (NumberFormatException | WrongFormatException | ArrayIndexOutOfBoundsException e){
                    System.err.println(member.getUser().getUserName() + " sent an unacceptable command");
                }
                str = methodRead();
            }
            textChannel.endUserChat(member.getUser());
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void broadcast (String str,User user) {
        try {
            if (!member.getUser().equals(user)){
                methodWrite(str);
            }
        } catch (IOException e){
            System.err.println("Couldn't Send Message");
            textChannel.getObservers().remove(this);
        }
    }

    public void broadcast (Massage massage) {
        try {
            if (!massage.getAuthor().equals(member.getUser())) {
                methodWrite(massage.toString());
            }
        } catch (IOException e){
            System.err.println("Couldn't Send Message");
            textChannel.getObservers().remove(this);
        }
    }

    public void broadcast (String str) {
        try {
            methodWrite(str);
        } catch (IOException e){
            System.err.println("Couldn't Send Message");
            textChannel.getObservers().remove(this);
        }
    }

    public void Initbroadcast (Massage massage) {
        try {
            methodWrite(massage.toString());
        } catch (IOException e){
            System.err.println("Couldn't Send Message");
            textChannel.getObservers().remove(this);
        }
    }

    public void end (User user) {
        try {
            if (this.member.getUser().equals(user)) {
                methodWrite(Command.EXITCHATMODE.getStr());
            }
        } catch (IOException e){
            System.err.println("Couldn't Send Message");
            textChannel.getObservers().remove(this);
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

    public Member getMember() {
        return member;
    }
}

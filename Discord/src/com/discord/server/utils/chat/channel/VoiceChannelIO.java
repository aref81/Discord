package com.discord.server.utils.chat.channel;

import com.discord.Command;
import com.discord.server.utils.FileStream;
import com.discord.server.utils.Massage;
import com.discord.server.utils.User;
import com.discord.server.utils.discordServer.Member;
import com.discord.server.utils.discordServer.channels.VoiceChannel;
import com.discord.server.utils.exceptions.WrongFormatException;

import java.io.*;

public class VoiceChannelIO implements Runnable, Serializable {
    private transient BufferedWriter writer;
    private transient BufferedReader reader;
    private Member member;
    private VoiceChannel voiceChannel;
    private FileStream fileStream;

    public VoiceChannelIO(FileStream fileStream,BufferedWriter writer, BufferedReader reader, Member member, VoiceChannel voiceChannel) {
        this.writer = writer;
        this.reader = reader;
        this.member = member;
        this.voiceChannel = voiceChannel;
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
                        voiceChannel.like(id,member.getUser());
                    } else if (str.contains("#dislike")){
                        String[] strings = str.split("-");
                        long id = Long.parseLong(strings[1]);
                        voiceChannel.dislike(id,member.getUser());
                    }else if (str.contains("#laughter")){
                        String[] strings = str.split("-");
                        long id = Long.parseLong(strings[1]);
                        voiceChannel.laughter(id,member.getUser());
                    }else if (str.contains("#pin")){
                        String[] strings = str.split("-");
                        long id = Long.parseLong(strings[1]);
                        voiceChannel.pin(id,member.getUser());
                    } else if (str.contains("#getpm")){
                        voiceChannel.getPinned(this);
                    } else if (str.equals("#sendFile")){
                        File file = fileStream.receiveFile(voiceChannel,member);
                        if (file != null) {
                            str = member.getUser().getUserName() + " has sent a file to chat -- #download";
                            Massage massage = new Massage(str, member.getUser(), voiceChannel.getId());
                            voiceChannel.sendMassage(massage);
                            voiceChannel.getFiles().put(massage, file);
                        }
                    } else if (str.contains("#download")){
                        String[] strings = str.split("-");
                        long id = Long.parseLong(strings[1]);
                        File file = voiceChannel.getFileMessage(id);
                        fileStream.sendFile(file);
                    }
                } catch (NumberFormatException | WrongFormatException | ArrayIndexOutOfBoundsException e){
                    System.err.println(member.getUser().getUserName() + " sent an unacceptable command");
                }
                str = methodRead();
            }
            voiceChannel.endUserChat(member.getUser());
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void broadcast (Massage massage) {
        try {
            if (!massage.getAuthor().equals(member.getUser())) {
                methodWrite(massage.toString());
            }
        } catch (IOException e){
            System.err.println("Couldn't Send Message");
        }
    }

    public void broadcast (String str) {
        try {
            methodWrite(str);
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
            if (this.member.getUser().equals(user)) {
                methodWrite(Command.EXITCHATMODE.getStr());
            }
        } catch (IOException e){
            System.err.println("Couldn't Send Message");
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

package com.discord.server.utils.discordServer;

import com.discord.Command;
import com.discord.server.utils.FileStream;
import com.discord.server.utils.User;
import com.discord.server.utils.discordServer.channels.Channel;
import com.discord.server.utils.discordServer.channels.TextChannel;
import com.discord.server.utils.discordServer.channels.VoiceChannel;
import com.discord.server.utils.exceptions.DuplicateException;
import com.discord.server.utils.exceptions.WrongFormatException;

import javax.management.relation.Role;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Member implements Serializable {
    private User user;
    private DiscordServer discordServer;
    private HashSet<String> roles;
    private HashMap<Channel,Long> historyKeeper;
    private transient BufferedWriter writer;
    private transient BufferedReader reader;
    private boolean block;
    private boolean firstTime;
    private FileStream fileStream;
    public Member(FileStream fileStream,User user,DiscordServer discordServer) {
        this.user = user;
        this.discordServer=discordServer;
        roles=new HashSet<>();
         block=false;
         firstTime = true;
         historyKeeper = new HashMap<>();
         this.fileStream = fileStream;
    }

    public HashSet<String> getRoles() {
        return roles;
    }

    public void setWriter(BufferedWriter writer) {
        this.writer = writer;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public void start() throws IOException {
        if (firstTime) {
            methodWrite(Command.PRINTWELLCOME.getStr());
            methodWrite(discordServer.getWelcome());
            firstTime = false;
            for (Channel c : discordServer.getChannels(this).values()) {
                if (c.isHistory()) {
                    historyKeeper.put(c, c.getHistory());
                } else {
                    historyKeeper.put(c, 0L);
                }
            }
        }
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        boolean loop=true;
        while (loop){
            try {
                ArrayList<Channel> channels = new ArrayList<>(discordServer.getChannels(this).values());
                StringBuilder sb=new StringBuilder("1.exit\n2.Settings\n3.members\n");
                int num=3+channels.size();
                int i=4;
                for (Channel channel:channels) {
                    sb.append(i++).append(".").append(channel.getName()).append("\n");
                    if (!historyKeeper.containsKey(channel)){
                        historyKeeper.put(channel,0L);
                    }
                }
                int choose=showMenu(sb.toString(),num);
                switch (choose){
                    case 1:{
                        loop=false;
                        break;
                    }
                    case 2:{
                        setting();
                        break;
                    }
                    case 3:{
                        showMembers();
                        break;
                    }
                    default:{
                        channel(channels.get(choose-4));
                        break;
                    }
                }
            }catch (IOException e){
                Thread.currentThread().interrupt();
                if (user.getStatus()== User.Status.ONLINE){
                    user.setStatus(User.Status.OFFLINE);
                }

            }
        }
    }

    private void showMembers () throws IOException {
        methodWrite(Command.GETTABLE.getStr());
        methodWrite(String.valueOf(discordServer.getMembers().size()));
        for (User u:discordServer.getMembers().keySet()) {
            methodWrite(u.getUserName()+" : "+u.getStatus().getName());
        }
        methodRead();
    }

    private void setting() throws IOException {
        boolean loop=true;
        while (loop) {
            HashSet<DiscordServer.Access> acset = new HashSet<>();
            for (String str : roles) {
                Set<DiscordServer.Access> access = discordServer.getARoleAccesses(str);
                acset.addAll(access);
            }
            ArrayList<DiscordServer.Access> accesses = new ArrayList<>(acset);
            accesses.removeIf(w -> w.equals(DiscordServer.Access.MASSAGEPINNER));
            StringBuilder sb = new StringBuilder("1.exit\n");
            int i = 2;
            int num = 1 + accesses.size();
            for (DiscordServer.Access access : accesses) {
                sb.append(i++).append(".").append(access.getMenu()).append("\n");
            }
            int choose = showMenu(sb.toString(), num);
            if (choose == 1) {
                loop = false;
                break;
            } else {
                DiscordServer.Access access = accesses.get(choose - 2);
                switch (access) {
                    case NAMECHANGER: {
                        nameChanger();
                        break;
                    }
                    case USERADDER:{
                        userAdder();
                        break;
                    }
                    case ROLECREATOR: {
                        roleCreator();
                        break;
                    }
                    case ROLEASSIGNER: {
                        roleAssigner();
                        break;
                    }
                    case USERBLOCKER: {
                        userBlocker();
                        break;
                    }
                    case USERLIMITER: {
                        userLimiter();
                        break;
                    }
                    case USERREMOVER: {
                        userRemover();
                        break;
                    }
                    case CHANELCREATOR: {
                        chanelCreator();
                        break;
                    }
                    case CHANELREMOVER: {
                        chanelRemover();
                        break;
                    }
                    case HISTORYVIEWER:{
                        historyViewer();
                        break;
                    }
                    case SERVERREMOVER: {
                        serverRemover();
                        break;
                    }

                }
            }
        }
    }

    private void historyViewer () throws IOException {
        HashMap<String,Channel> channels = getInputChannels();
        for (Channel channel : channels.values()){
            channel.setHistory(true);
        }
    }

    private void userAdder () throws IOException {
        boolean loop = true;
        methodWrite(Command.GETUSERNAME.getStr());
        String userName = null;
        while (loop) {
            userName = methodRead();
            if (discordServer.addUser(userName)){
                loop = false;
            } else {
                loop = true;
                methodWrite(Command.GETUSERNAMEAGAIN.getStr());
                methodWrite("couldn't add user");
            }
        }
    }

    private void nameChanger() throws IOException {
        boolean loop=true;
        methodWrite(Command.GETSERVERNAME.getStr());
        String serverName = null;
        while (loop) {
            try {
                serverName = methodRead();
                if (discordServer.changeServerName(serverName)){
                    loop=false;
                }
            }catch (DuplicateException e){
                loop = true;
                methodWrite(Command.GETSERVERNAMEAGAIN.getStr());
                methodWrite(e.toString());
            }
        }
    }
    private void roleCreator() throws IOException {
        boolean loop=true;
        methodWrite(Command.GETROLENAME.getStr());
        String roleName=null;
        while (loop){
            try {
                roleName = methodRead();
                if (!discordServer.checkRoleName(roleName)) {
                    throw new DuplicateException("This role name already exists");
                }
                loop = false;
            } catch (DuplicateException e){
                loop = true;
                methodWrite(Command.GETUSERNAMEAGAIN.getStr());
                methodWrite(e.toString());
            }
        }

        loop = true;
        ArrayList<DiscordServer.Access> accesses = new ArrayList<>(List.of(DiscordServer.Access.values()));
        accesses.removeIf(w -> (w.equals(DiscordServer.Access.ROLECREATOR) || w.equals(DiscordServer.Access.SERVERREMOVER) || w.equals(DiscordServer.Access.ROLEASSIGNER)));
        StringBuilder sb = new StringBuilder("1.confirm\n");
        HashSet<DiscordServer.Access> roleAccesses = new HashSet<>();
        while (loop) {
            int num = 1 + accesses.size();
            int i = 2;
            for (DiscordServer.Access a : accesses) {
                sb.append(i++).append(".").append(a.getMenu()).append("\n");
            }
            int choose = showMenu(sb.toString(), num);

            switch (choose) {
                case (1) : {
                    loop = false;
                    break;
                }
                default:{
                    roleAccesses.add(accesses.get( choose - 2));
                    break;
                }
            }
        }
        discordServer.addRole(roleName,roleAccesses);
    }
    private void roleAssigner() throws IOException {
        boolean loop =true;
        boolean exit=true;
        Member member = null;
        ArrayList<Member> members = new ArrayList<>(discordServer.getMembers().values());
        while (loop){
            StringBuilder sb= new StringBuilder("1.exit\n");
            int num=1+members.size();
            int i = 2;
            for (Member m : members){
                sb.append(i++).append(".").append(m.getUserName()).append("\n");
            }

            int choose = showMenu(sb.toString(),num);

            switch (choose) {
                case 1 :{
                    loop = false;
                    exit = false;
                    break;
                }
                default:{
                    member = members.get(choose - 2);
                    break;
                }
            }

            while (exit){
                sb = new StringBuilder("1.exit\n");
                i = 2;
                ArrayList<String> roles = new ArrayList<>(discordServer.getRoleAccesses().keySet());
                num = 1 + roles.size();
                for (String role : roles){
                    sb.append(i++).append(".").append(role).append("\n");
                }

                choose = showMenu(sb.toString(),num);
                String role = null;

                switch (choose){
                    case 1:{
                        exit = false;
                        break;
                    }
                    default:{
                        role = roles.get(choose - 2);
                        member.getRoles().add(role);
                        discordServer.getUserRoles().get(user).add(role);
                        exit = false;
                        break;
                    }
                }
            }
        }


    }
    private void userBlocker() throws IOException {
        boolean loop =true;
        boolean exit=true;
        Member member = null;
        ArrayList<Member> members = new ArrayList<>(discordServer.getMembers().values());
        while (loop){
            StringBuilder sb= new StringBuilder("1.exit\n");
            int num=1+members.size();
            int i = 2;
            for (Member m : members){
                sb.append(i++).append(".").append(m.getUserName()).append("\n");
            }

            int choose = showMenu(sb.toString(),num);

            switch (choose) {
                case 1 :{
                    loop = false;
                    exit = false;
                    break;
                }
                default:{
                    member = members.get(choose - 2);
                    break;
                }
            }

            while (exit){
                sb = new StringBuilder("1.Block\n2.Unblock\n");

                choose = showMenu(sb.toString(),2);

                switch (choose){
                    case 1:{
                        member.setBlock(true);
                        HashMap<String,Channel> channels = getInputChannels();
                        discordServer.getBlockedMembers().put(member,channels);
                        exit = false;
                        break;
                    }
                    case 2:{
                        member.setBlock(false);
                        discordServer.getBlockedMembers().remove(member);
                        exit =false;
                        break;
                    }
                }
            }
        }
    }

    private void userLimiter() throws IOException {
        Channel channel = getInputChannel();
        channel.setLimited(true);
        HashMap<String,Member> members = getInputMembers();
        discordServer.limitChanel(members,channel);
    }

    private void userRemover() throws IOException {
        boolean loop =true;
        boolean exit=true;
        Member member = null;
        ArrayList<Member> members = new ArrayList<>(discordServer.getMembers().values());
        while (loop){
            StringBuilder sb= new StringBuilder("1.exit\n");
            int num=1+members.size();
            int i = 2;
            for (Member m : members){
                sb.append(i++).append(".").append(m.getUserName()).append("\n");
            }

            int choose = showMenu(sb.toString(),num);

            switch (choose) {
                case 1 :{
                    loop = false;
                    exit = false;
                    break;
                }
                default:{
                    member = members.get(choose - 2);
                    break;
                }
            }

            while (exit){
//                methodWrite(Command.PRINT.getStr());
//                methodWrite("Are you sure?");
                sb = new StringBuilder("1.YES\n2.NO\n");
                choose = showMenu(sb.toString(),2);

                switch (choose){
                    case 1:{
                        member.getUser().getDiscordServers().remove(discordServer);
                        discordServer.getBlockedMembers().remove(member);
                        discordServer.getMembers().remove(member.user);
                        discordServer.getUserRoles().remove(member.user);
                        break;
                    }
                    case 2:{
                        exit = false;
                        break;
                    }
                }
            }
        }
    }

    private void chanelCreator() throws IOException {
        boolean loop = true;
        String name = null;
        methodWrite(Command.GETCHANNELNAME.getStr());
        while (loop) {
            try {
                name = methodRead();
                if (!discordServer.checkChannelName(name)) {
                    throw new DuplicateException("This Channel Name is in use.");
                }
                loop = false;

            } catch (DuplicateException e) {
                loop = true;
                methodWrite(Command.GETCHANNELNAMEAGAIN.getStr());
                methodWrite(e.toString());
            }
        }

        String str = "1.text channel\n2.voice channel\n";
        int choose = showMenu(str,2);
        Channel channel = null;
        switch (choose){
            case 1:{
                channel = new TextChannel(fileStream,name,discordServer,false);
                break;
            }
            case 2:{
                channel = new VoiceChannel(fileStream,name,discordServer,false);
                break;
            }
        }

        discordServer.addChannel(channel);
    }
    private void chanelRemover() throws IOException {
        Channel channel = getInputChannel();
        discordServer.removeChannel(channel.getName());
    }

    private void serverRemover(){
        discordServer.removeServer();
    }



    private void channel(Channel channel){
        channel.start(this,historyKeeper.get(channel));
    }


    public Channel getInputChannel () throws IOException {
        boolean loop = true;
        Channel channel = null;
        ArrayList<Channel> channels = new ArrayList<>(discordServer.getChannels(null).values());
        while (loop) {
            StringBuilder sb = new StringBuilder("1.exit\n");
            int num = 1 + channels.size();
            int i = 2;
            for (Channel m : channels) {
                sb.append(i++).append(".").append(m.getName()).append("\n");
            }

            int choose = showMenu(sb.toString(), num);

            switch (choose) {
                case 1: {
                    loop = false;
                    break;
                }
                default: {
                    channel = channels.get(choose - 2);
                    loop = false;
                    break;
                }
            }
        }

        return channel;
    }

    public HashMap<String,Channel> getInputChannels () throws IOException {
        boolean loop = true;
        HashMap<String,Channel> rChannels = new HashMap<>();
        ArrayList<Channel> channels = new ArrayList<>(discordServer.getChannels(null).values());
        while (loop) {
            StringBuilder sb = new StringBuilder("1.confirm\n");
            int num = 1 + channels.size();
            int i = 2;
            for (Channel m : channels) {
                sb.append(i++).append(".").append(m.getName()).append("\n");
            }

            int choose = showMenu(sb.toString(), num);

            switch (choose) {
                case 1: {
                    loop = false;
                    break;
                }
                default: {
                    rChannels.put(channels.get(choose - 2).getName(),channels.get(choose - 2));
                    break;
                }
            }
        }

        return rChannels;
    }

    public Member getInputMember () throws IOException {
        boolean loop = true;
        Member member = null;
        ArrayList<Member> members = new ArrayList<>(discordServer.getMembers().values());
        while (loop) {
            StringBuilder sb = new StringBuilder("1.exit\n");
            int num = 1 + members.size();
            int i = 2;
            for (Member m : members) {
                sb.append(i++).append(".").append(m.getUserName()).append("\n");
            }

            int choose = showMenu(sb.toString(), num);

            switch (choose) {
                case 1: {
                    loop = false;
                    break;
                }
                default: {
                    member = members.get(choose - 2);
                    break;
                }
            }
        }

        return member;
    }

    public HashMap<String,Member> getInputMembers () throws IOException {
        boolean loop = true;
        HashMap<String,Member> rMembers = new HashMap<>();
        ArrayList<Member> members = new ArrayList<>(discordServer.getMembers().values());
        while (loop) {
            StringBuilder sb = new StringBuilder("1.confirm\n");
            int num = 1 + members.size();
            int i = 2;
            for (Member m : members) {
                sb.append(i++).append(".").append(m.getUserName()).append("\n");
            }

            int choose = showMenu(sb.toString(), num);

            switch (choose) {
                case 1: {
                    loop = false;
                    break;
                }
                default: {
                    rMembers.put(members.get(choose - 2).getUserName(),members.get(choose - 2));
                    break;
                }
            }
        }

        return rMembers;
    }



    private int showMenu (String menu,int n) throws IOException {
        int choose=0;
        methodWrite(Command.SHOWMENU.getStr());
        methodWrite(String.valueOf(n));
        methodWrite(menu);
        boolean loop=true;
        while (loop){
            try {
                choose=Integer.parseInt(methodRead());
                if (!((choose <= n) && (choose > 0))){
                    throw new WrongFormatException(null);
                }
                loop=false;
            }
            catch (NumberFormatException | WrongFormatException e){
                methodWrite(Command.SHOWMENU.getStr());
                methodWrite("1");
                methodWrite("Your input is not valid");
            }
        }
        return choose;
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

    String getUserName(){
        return user.getUserName();
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public boolean isBlock() {
        return block;
    }

    public User getUser () {
        return user;
    }

    public BufferedWriter getWriter() {
        return writer;
    }

    public BufferedReader getReader() {
        return reader;
    }
}

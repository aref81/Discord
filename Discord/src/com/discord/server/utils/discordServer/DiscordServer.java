package com.discord.server.utils.discordServer;

import com.discord.server.ControllCenter;
import com.discord.server.utils.FileStream;
import com.discord.server.utils.User;
import com.discord.server.utils.discordServer.channels.Channel;
import com.discord.server.utils.exceptions.DuplicateException;

import java.awt.desktop.PreferencesEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class DiscordServer implements Serializable {

    public enum Access{
        USERADDER("add a user"),
        ROLEASSIGNER("Assign a Role"),
        CHANELCREATOR("Create a Channel"),
        CHANELREMOVER("Remove a Channel"),
        USERREMOVER("Remove a User"),
        USERLIMITER("Limit a User"),
        USERBLOCKER("Block a User"),
        NAMECHANGER("Change Server Name"),
        HISTORYVIEWER("History View"),
        MASSAGEPINNER("Pin a Message"),
        SERVERREMOVER("Remove the Server"),
        ROLECREATOR("Create a Role");

        private String menu;

        Access(String menu) {
            this.menu = menu;
        }

        public String getMenu() {
            return menu;
        }
    }

    private String serverName;
    private User serverOwner;
    private HashMap<User,Member> members;
    private HashMap<String,Set<Access>> roleAccesses;
    private HashMap<User,HashSet<String>> userRoles;
    private HashMap<String, Channel> channels;
    private ControllCenter controllCenter;
    private HashMap<Member,HashMap<String,Channel>> blockedMembers;
    private HashMap<Member,HashMap<String,Channel>> limitedChannels;
    private String welcome;
    private FileStream fileStream;

    public DiscordServer(FileStream fileStream,String serverName, User serverOwner,ControllCenter controllCenter,String welcome) {
        this.serverName = serverName;
        this.serverOwner = serverOwner;
        this.controllCenter=controllCenter;
        members=new HashMap<>();
        Member member=new Member(fileStream,serverOwner,this);
        HashSet<Access> ownerAccess = new HashSet<>();
        ownerAccess.addAll(Arrays.asList(Access.values()));
        member.getRoles().add("Owner");
        this.fileStream = fileStream;
        members.put(serverOwner,member);
        userRoles=new HashMap<>();
        roleAccesses = new HashMap<>();
        channels=new HashMap<>();

        HashSet<Access> userAccess = new HashSet<>();
        userAccess.add(Access.USERADDER);
        roleAccesses.put("User",userAccess);

        roleAccesses.put("Owner",ownerAccess);
        member.getRoles().add("Owner");
        userRoles.put(serverOwner,member.getRoles());
        blockedMembers=new HashMap<>();
        limitedChannels = new HashMap<>();

        this.welcome = welcome;
    }

    public String getServerName() {
        return serverName;
    }

    public void enterMember(User user, BufferedWriter writer, BufferedReader reader) throws IOException {
        Member member=members.get(user);
        member.setWriter(writer);
        member.setReader(reader);
        member.start();
    }

    public boolean changeServerName(String newName) throws DuplicateException {
        if (controllCenter.checkServerName(newName)) {
            controllCenter.changeServerName(serverName, newName, this);
            serverName = newName;
            return true;
        } else {
            return false;
        }
    }

    public String getWelcome() {
        return welcome;
    }

    public Set<Access> getARoleAccesses (String str) {
        return roleAccesses.get(str);
    }

    public boolean checkRoleName (String s) {
        return !roleAccesses.containsKey(s);
    }

    public boolean checkChannelName (String s){
        return !channels.containsKey(s);
    }

    public void addRole (String roleName,HashSet<Access> accesses){
        roleAccesses.put(roleName,accesses);
    }

    public HashMap<User, Member> getMembers() {
        return members;
    }

    public HashMap<String, Set<Access>> getRoleAccesses() {
        return roleAccesses;
    }

    public HashMap<User, HashSet<String>> getUserRoles() {
        return userRoles;
    }

    public HashMap<Member,HashMap<String,Channel>> getBlockedMembers() {
        return blockedMembers;
    }

    public HashMap<String,Channel> getChannels(Member member) {
        if (member != null) {
            if (member.isBlock()) {
                return blockedMembers.get(member);
            } else if (limitedChannels.containsKey(member)){
                return limitedChannels.get(member);
            }else {
                return channels;
            }
        } else {
            return channels;
        }
    }

    public void addChannel(Channel channel){
        channels.put(channel.getName(),channel);
    }

    public void limitChanel(HashMap<String,Member> members,Channel channel){
        channels.remove(channel.getName());
        for (Member m : members.values()) {
            if (limitedChannels.containsKey(m)) {
                limitedChannels.get(m).put(channel.getName(),channel);
            } else {
                HashMap<String,Channel> channels = new HashMap<>(this.channels);
                channels.put(channel.getName(),channel);
                limitedChannels.put(m,channels);
            }
        }
    }

    public void removeChannel(String name){
        channels.remove(name);
    }

    public void removeServer (){
        for (User user : members.keySet()){
            user.getDiscordServers().remove(this);
        }

        controllCenter.removeServer(this);
    }

    public boolean addUser (String userName){
        User user = controllCenter.findUser(userName);
        if (user != null){
            Member member = new Member(fileStream,user,this);
            member.getRoles().add("User");
            userRoles.put(user,member.getRoles());
            members.put(user,member);
            user.getDiscordServers().add(this);
            return true;
        } else {
            return false;
        }
    }
}

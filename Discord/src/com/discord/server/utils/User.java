package com.discord.server.utils;

import com.discord.server.utils.discordServer.DiscordServer;

import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {

    public enum Status{
        ONLINE("Online"),
        IDLE("Idle"),
        DND("Do Not Disturb"),
        INVISIBLE("Invisible"),
        OFFLINE("offline");

        private String name;

        Status(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    private String userName;
    private String password;
    private String email;
    private String phoneNumber;
    private File imageFile;
    private Status status;
    private ArrayList<User> requests;
    private ArrayList<User> friends;
    private HashMap<String,PrivateChat> privateChats;
    private ArrayList<User> blockUsers;
    private ArrayList<DiscordServer> discordServers;
    public User(String userName, String password, String email, String phoneNumber,File imageFile){
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.imageFile=imageFile;
        requests = new ArrayList<>();
        friends=new ArrayList<>();
        privateChats=new HashMap<>();
        discordServers=new ArrayList<>();
        status = Status.ONLINE;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public File getImageFile() {
        return imageFile;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void addRequest(User request){
        requests.add(request);
    }

    public ArrayList<User> getRequests(){
        return requests;
    }


    public void addFriend(User friend){
        friends.add(friend);
    }

    public ArrayList<User> getFriends(){
        return friends;
    }

    public void removeFriend(User user){
        friends.remove(user);
    }

    public HashMap<String,PrivateChat> getPrivateChats(){
        return privateChats;
    }

    public void addPrivateChat (User user, PrivateChat privateChat){
        privateChats.put(user.getUserName(),privateChat);
    }

    public void removePrivateChat(User user){
        privateChats.remove(user.getUserName());
    }

    public ArrayList<User> getBlockUsers(){
        return blockUsers;
    }

    public void addBlockUser(User user){
        blockUsers.add(user);
    }

    public void removeBlockUser(User user){
        blockUsers.remove(user);
    }

    public ArrayList<DiscordServer> getDiscordServers() {
        return discordServers;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                '}';
    }
}

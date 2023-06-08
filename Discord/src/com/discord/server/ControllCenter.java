package com.discord.server;

import com.discord.server.utils.FileStream;
import com.discord.server.utils.discordServer.DiscordServer;
import com.discord.server.utils.User;
import com.discord.server.utils.exceptions.DuplicateException;
import com.discord.server.utils.exceptions.WrongFormatException;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class ControllCenter implements Serializable {

    private HashMap<String, User> users;
    private HashMap<String,DiscordServer> discordServers;
    private transient ArrayList<UserThread> threads;
    private transient ExecutorService pool;

    public ControllCenter() throws IOException {
        users=new HashMap<>();
        discordServers=new HashMap<>();
        threads=new ArrayList<>();
        pool= Executors.newCachedThreadPool();
    }

    public ControllCenter(ControllCenter controllCenter){
        users=controllCenter.users;
        discordServers=controllCenter.discordServers;
        threads=new ArrayList<>();
        pool=Executors.newCachedThreadPool();
    }
    public void init(Socket socket,Socket notifSocket,Socket fileSocket) throws IOException {
        try {
            UserThread userThread=new UserThread(fileSocket,this,socket,notifSocket);
            threads.add(userThread);
            pool.execute(userThread);
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    public User createUser(String userName,String password,String email,String phone){
        User user=new User(userName,password,email,phone,null);
        users.put(user.getUserName(),user);
        return user;
    }

    public void changeUserName(String oldName,String newName){
        User user = users.get(oldName);
        users.remove(oldName);
        user.setUserName(newName);
        users.put(newName,user);
    }

    public User findUser(String userName,String password){
        User user=users.get(userName);
        if (user!=null){
            if (password.equals(user.getPassword())){
                return user;
            }else {
                return null;
            }
        }
        return null;
    }
    public User findUser(String userName){
        User user=users.get(userName);
        if (user!=null){
            return user;
        }
        return null;
    }

    public boolean checkUserName(String str) throws WrongFormatException, DuplicateException {
        if (Pattern.matches("(\\S[a-zA-z0-9]{5,})",str)){
            if (users.containsKey(str)){
                throw new DuplicateException("Your user name is in use");
            }
            else {
                return true;
            }
        } else {
            throw new WrongFormatException("Your user name is not valid");
        }
    }

    public boolean checkUserNameFormat(String str) throws WrongFormatException {
        if (Pattern.matches("(\\S[a-zA-z0-9]{5,})",str)){
            return true;
        } else {
            throw new WrongFormatException("Your user name is not valid");
        }
    }

    public boolean checkPassword(String str) throws WrongFormatException {
        if (Pattern.matches("\\w{7,}",str)) {
            return true;
        }else {
            throw new WrongFormatException("Your password is not valid");
        }

    }

    public boolean checkPhone(String str) throws WrongFormatException {
        if (Pattern.matches("[0-9]{10}",str)) {
            return true;
        }else {
            throw new WrongFormatException("Your phone number is not valid");
        }

    }

    public boolean checkEmail(String str) throws WrongFormatException {
        if (Pattern.matches("(\\S.*\\S)(@)(\\S.*\\S)(.\\S[a-z]{2,3})",str)) {
            return true;
        }else {
            throw new WrongFormatException("Your e-mail is not valid");
        }

    }

    public boolean checkServerName(String name) throws DuplicateException {
        if (discordServers.containsKey(name)){
            throw new DuplicateException("Your server name is in use");
        }
        else {
            return true;
        }
    }

    public DiscordServer createServer(FileStream fileStream, String serverName, User owner, String welcome){
        DiscordServer discordServer=new DiscordServer(serverName,owner,this,welcome);
        discordServers.put(discordServer.getServerName(),discordServer);
        owner.getDiscordServers().add(discordServer);
        return discordServer;
    }

    public void changeServerName(String oldName,String newName,DiscordServer server){
        discordServers.remove(oldName);
        discordServers.put(newName,server);
    }

    public void removeServer (DiscordServer discordServer){
        discordServers.remove(discordServer.getServerName());
    }

    public HashMap<String, User> getUsers() {
        return users;
    }
}

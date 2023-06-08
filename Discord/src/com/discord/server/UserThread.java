package com.discord.server;

import com.discord.Command;
import com.discord.server.utils.FileStream;
import com.discord.server.utils.NotificationStream;
import com.discord.server.utils.discordServer.DiscordServer;
import com.discord.server.utils.PrivateChat;
import com.discord.server.utils.User;
import com.discord.server.utils.exceptions.DuplicateException;
import com.discord.server.utils.exceptions.WrongFormatException;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UserThread extends Thread{

    private final ControllCenter controllCenter;
    private final Socket socket;
    private final BufferedWriter writer;
    private final BufferedReader reader;
    private User user;
    private transient FileStream fileStream;

    private transient NotificationStream notificationStream;

    public UserThread(Socket fileserverSocket, ControllCenter controllCenter, Socket socket,Socket notifSocket) throws IOException {
        this.controllCenter = controllCenter;
        this.socket = socket;
        writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        notificationStream = new NotificationStream(notifSocket);
        fileStream = new FileStream(fileserverSocket);
    }



    @Override
    public void run() {
        try {
            boolean app = true;
            System.out.println("Wellcome Sent");
            methodWrite("Welcome to discord.\n");
            Thread.sleep(5*1000);
            while (app) {
                login();


                if (user != null && user.getStatus() == User.Status.OFFLINE) {
                    user.setStatus(User.Status.ONLINE);
                }

                if (user != null && notificationStream != null) {
                    notificationStream.sendPopUp("WellCome", user.getUserName());
                }

                if (user!=null){
                    user.setUserThread(this);
                }

                boolean loop = true;
                int choose ;
                while (loop) {
                    choose = showMenu("1.Friends\n2.Private chat\n3.Discord servers\n4.Profile\n5.logout", 5);

                    switch (choose) {
                        case 1: {
                            fMenu();
                            break;
                        }
                        case 2: {
                            pChatMenu();
                            break;
                        }
                        case 3: {
                            serverMenu();
                            break;
                        }
                        case 4: {
                            profile();
                            break;
                        }
                        case 5: {
                            logOut();
                            loop = false;
                            break;
                        }
                    }
                }
            }

        } catch (IOException e) {
            Thread.currentThread().interrupt();
            if (user != null && user.getStatus()== User.Status.ONLINE){
                user.setStatus(User.Status.OFFLINE);
            }
            notificationStream.close();
            if (user!=null){
                user.setUserThread(null);
            }
        } catch (InterruptedException e) {
            System.out.println("Connection Reset");
        }
    }

    private void fMenu() throws IOException {
        boolean loop = true;
        while (loop) {
            int choose = showMenu("1.exit\n2.create new friend\n3.show friends\n4.friend requests\n5.Requests status\n6.Block Users\n7.Unblock Users", 7);
            switch (choose) {
                case 1: {
                    loop=false;
                    break;
                }
                case 2: {
                    createNewFriend();
                    break;
                }
                case 3: {
                    showFriends();
                    break;
                }
                case 4: {
                    friendRequest();
                    break;
                }
                case 5: {
                    reqs();
                    break;
                }
                case 6: {
                    blockFriends();
                    break;
                }
                case 7: {
                    unBlockFriends();
                    break;
                }
            }
        }
    }

    private void reqs() throws IOException {
        methodWrite(Command.GETTABLE.getStr());
        methodWrite(String.valueOf(user.getSendedReqs().size()));
        methodWrite("reqs");
        for (User u:user.getSendedReqs().keySet()) {
            methodWrite(u.getUserName()+" : "+user.getSendedReqs().get(u).getStr());
            if (u.getImageFile() != null){
                methodWrite("pic");
                fileStream.methodWrite(u.getImageFile().getName());
                fileStream.sendFile(u.getImageFile());
            } else {
                methodWrite("null");
            }
        }
        String str = methodRead();
        while (!str.equals("exit")){
            HashMap<User,User.Reqs> reqs = user.getSendedReqs();
            User is = null;
            for (User u : reqs.keySet()) {
                if (u.getUserName().equals(str)){
                    if (reqs.get(u).equals(User.Reqs.PENDING)) {
                        u.getRequests().remove(user);
                        is = u;
                        break;
                    }
                }
            }
            if (is != null){
                user.getSendedReqs().remove(is);
            }
            str = methodRead();
        }
    }

    private void createNewFriend() throws IOException {
        boolean loop = true;
        methodWrite(Command.CREATEFRIEND.getStr());
        while (loop) {
            try {
                String userName = methodRead();
                if (!userName.equals("exit")) {
                    if (controllCenter.checkUserNameFormat(userName)) {
                        User user = controllCenter.findUser(userName);
                        if (user == null) {
                            notificationStream.sendPopUp("Friend Request Failed", "Hm,didn't work, Double check that capitalization,Spelling and etc. are correct.");
                            methodWrite(Command.GETUSERNAMEAGAIN.getStr());
                            methodWrite("didn't work, Double check it.");
                        } else {
                            String str;
                            if (user.getRequests().contains(this.user)) {
                                str = "You already have a pending request";
                            } else if (user.equals(this.user) || this.user.getFriends().contains(user)) {
                                str = "You can't send user request to this user";
                            } else {
                                this.user.sendRequest(user);
                                user.addRequest(this.user);
                                str = "Your Friend Request has been sent.";
                            }
                            notificationStream.sendPopUp("Friend Request Status", str);
                        }
                    }
                } else {
                    loop = false;
                }
            } catch (WrongFormatException e) {
                methodWrite(Command.GETUSERNAMEAGAIN.getStr());
                methodWrite("User Name format is not valid");
            }
        }
    }

    private void showFriends() throws IOException {
        methodWrite(Command.SHOWFRIENDSCHART_ONLINE.getStr());
        int i = 0;
        for (User u:user.getFriends()) {
            if (u.getStatus().equals(User.Status.ONLINE)) {
                i++;
            }
        }
        methodWrite(String.valueOf(i));
        for (User u:user.getFriends()) {
            if (u.getStatus().equals(User.Status.ONLINE)) {
                methodWrite(u.getUserName() + " : " + u.getStatus().getName());
                if (u.getImageFile() != null){
                    methodWrite("pic");
                    fileStream.methodWrite(u.getImageFile().getName());
                    fileStream.sendFile(u.getImageFile());
                } else {
                    methodWrite("null");
                }
            }
        }

        String str = methodRead();
        while (!str.equals("exit")){
            switch (str) {
                case "Online" : {
                    methodWrite(Command.SHOWFRIENDSCHART_ONLINE.getStr());
                    i = 0;
                    for (User u:user.getFriends()) {
                        if (u.getStatus().equals(User.Status.ONLINE)) {
                            i++;
                        }
                    }
                    methodWrite(String.valueOf(i));
                    for (User u:user.getFriends()) {
                        if (u.getStatus().equals(User.Status.ONLINE)) {
                            methodWrite(u.getUserName() + " : " + u.getStatus().getName());
                            if (u.getImageFile() != null){
                                methodWrite("pic");
                                fileStream.methodWrite(u.getImageFile().getName());
                                fileStream.sendFile(u.getImageFile());
                            } else {
                                methodWrite("null");
                            }
                        }
                    }

                    str = methodRead();
                    break;
                }
                case "All" : {
                    methodWrite(Command.SHOWFRIENDSCHART_ALL.getStr());
                    methodWrite(String.valueOf(user.getFriends().size()));
                    for (User u:user.getFriends()) {
                        methodWrite(u.getUserName() + " : " + u.getStatus().getName());
                        if (u.getImageFile() != null){
                            methodWrite("pic");
                            fileStream.methodWrite(u.getImageFile().getName());
                            fileStream.sendFile(u.getImageFile());
                        } else {
                            methodWrite("null");
                        }
                    }

                    str = methodRead();
                    break;
                }
                case "Pending" : {
                    methodWrite(Command.SHOWFRIENDSCHART_PENDING.getStr());
                    methodWrite(String.valueOf(user.getRequests().size()));
                    for (User u:user.getRequests()) {
                        methodWrite(u.getUserName() + " : " + u.getStatus().getName());
                        if (u.getImageFile() != null){
                            methodWrite("pic");
                            fileStream.methodWrite(u.getImageFile().getName());
                            fileStream.sendFile(u.getImageFile());
                        } else {
                            methodWrite("null");
                        }
                    }

                    str = methodRead();
                    break;
                }
                case "Blocked" : {
                    methodWrite(Command.SHOWFRIENDSCHART_BLOCKED.getStr());
                    methodWrite(String.valueOf(user.getBlockUsers().size()));
                    for (User u:user.getBlockUsers()) {
                        methodWrite(u.getUserName() + " : " + u.getStatus().getName());
                        if (u.getImageFile() != null){
                            methodWrite("pic");
                            fileStream.methodWrite(u.getImageFile().getName());
                            fileStream.sendFile(u.getImageFile());
                        } else {
                            methodWrite("null");
                        }
                    }

                    str = methodRead();
                    break;
                }
            }
        }
    }

    private void unBlockFriends() throws IOException {
        boolean loop=true;
        while (loop){
            StringBuilder sb=new StringBuilder();
            sb.append("1.exit\n");
            int index=2;
            for (User u:user.getBlockUsers()) {
                sb.append(index++);
                sb.append('.');
                sb.append(u.getUserName());
                sb.append("\n");
            }
            int choose=showMenu(sb.toString(),index - 1);
            if (choose==1){
                loop=false;
            }else {
                User u = user.getBlockUsers().get(choose - 2);
                int choice=showMenu("1.YES\n2.NO",2);
                if (choice==1){
                    user.removeBlockUser(u);
                    notificationStream.sendPopUp("User UnBlocked",u.getUserName() + " has been successfully unblocked");
                }
            }
        }
    }

    private void blockFriends() throws IOException {
        boolean loop=true;
        ArrayList<User> ubs = new ArrayList<>();
        while (loop){
            StringBuilder sb=new StringBuilder();
            sb.append("1.exit\n");
            int index=2;
            for (User u:user.getFriends()) {
                if (!user.getBlockUsers().contains(u)) {
                    sb.append(index++);
                    sb.append('.');
                    sb.append(u.getUserName());
                    sb.append("\n");
                    ubs.add(u);
                }
            }
            int choose=showMenu(sb.toString(),index - 1);
            if (choose==1){
                loop=false;
            }else {
                User u = ubs.get(choose - 2);
                int choice=showMenu("1.YES\n2.NO",2);
                if (choice==1){
                    user.addBlockUser(u);
                    notificationStream.sendPopUp("User Blocked",u.getUserName() + " has been successfully blocked");
                }
            }
        }
    }
    private void friendRequest() throws IOException {
        boolean loop=true;
        while (loop){
            StringBuilder sb=new StringBuilder();
            sb.append("1.exit\n");
            int index=2;
            for (User u:user.getRequests()) {
                sb.append(index++);
                sb.append('.');
                sb.append(u.getUserName());
                sb.append("\n");
            }
            int choose=showMenu(sb.toString(),index - 1);
            if (choose==1){
                loop=false;
            }else {
                User u = user.getRequests().get(choose - 2);
                int choice=showMenu("1.YES\n2.NO",2);
                if (choice==1){
                    u.addFriend(user);
                    user.addFriend(u);
                    u.answerReq(user, User.Reqs.ACCEPTED);
                } else {
                    u.answerReq(user, User.Reqs.REJECTED);
                }
                user.getRequests().remove(u);
            }
        }
    }




    private void signUp () throws IOException {
        boolean loop=true;
        String userName = null;
        String password = null;
        String email = null;
        String phone=null;

        while (loop){
            methodWrite(Command.SIGNUP.getStr());
            String str=methodRead();
            switch (str){
                case "Info":{
                    try {
                        userName=methodRead();
                        password=methodRead();
                        email=methodRead();
                        phone=methodRead();
                        if (controllCenter.checkUserName(userName) && controllCenter.checkPassword(password) && controllCenter.checkEmail(email)){
                            if (!(phone.equals("NO PHONE NUMBER"))){
                                controllCenter.checkPhone(phone);
                                user=controllCenter.createUser(userName,password,email,phone);
                            }else {
                                user = controllCenter.createUser(userName, password, email,null);
                            }
                            loop=false;
                        }
                    } catch (DuplicateException | WrongFormatException e) {
                        notificationStream.sendPopUp("ERROR",e.toString());
                    }
                    break;
                }
                case "login":{
                    loop=false;
                    break;
                }
            }
        }

        methodWrite(Command.GETUSERNAME.getStr());
    }

    private void login() throws IOException {
        boolean loop=true;
        String userName;
        String password;
        while (loop){
            try {
                methodWrite(Command.LOGIN.getStr());
                String str;
                str=methodRead();

                switch (str){
                    case "Info":{
                        userName=methodRead();
                        password=methodRead();

                        user = controllCenter.findUser(userName,password);
                        if (user!=null){
                            loop=false;
                            notificationStream.sendPopUp("Logged in",user.getUserName() + " is logged in");
                        }else {
                            notificationStream.sendPopUp("ERROR","User name or Password is incorrect");
                        }
                        break;
                    }
                    case "SignUp" :{
                        signUp();
                        break;
                    }
                    case "exit" : {
                        exit();
                        loop=false;
                        break;
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
                loop=false;
            }
        }


    }

    private void pChatMenu() throws IOException {
        boolean loop = true;
        ArrayList<String> pc = new ArrayList<String>();
        pc.addAll(user.getPrivateChats().keySet());
        ArrayList<PrivateChat> pcs= new ArrayList<>();
        while (loop) {
            StringBuilder sb = new StringBuilder("1.exit\n2.create new chat\n");
            int i = 3;
            for (String p : pc) {
                if (!controllCenter.getUsers().get(p).getBlockUsers().contains(user)) {
                    sb.append(i++);
                    sb.append('.');
                    sb.append(p);
                    sb.append("\n");
                    pcs.add(user.getPrivateChats().get(p));
                }
            }

            int choose = showMenu(sb.toString(),i - 1);

            switch (choose){
                case (1): {
                    loop = false;
                    break;
                } case (2) : {
                    createNewChat();
                    break;
                }
                default:{
                    pChat(pcs.get(choose - 3));
                    break;
                }
            }
        }

    }

    private void createNewChat () throws IOException {
        boolean loop=true;
        while (loop) {
            StringBuilder sb = new StringBuilder();
            sb.append("1.exit\n");
            int i=2;
            for (User u:user.getFriends()) {
                if (!u.getBlockUsers().contains(user)) {
                    sb.append(i++).append('.').append(u.getUserName()).append("\n");
                }
            }
            int choose = showMenu(sb.toString(),i - 1);

            if (choose == 1){
                loop = false;
            } else {
                if (!user.getPrivateChats().containsKey(user.getFriends().get(choose - 2).getUserName())){
                    PrivateChat privateChat = new PrivateChat(user,user.getFriends().get(choose - 2));
                    user.addPrivateChat(user.getFriends().get(choose - 2),privateChat);
                    user.getFriends().get(choose - 2).addPrivateChat(user,privateChat);
                }
                pChat(user.getPrivateChats().get(user.getFriends().get(choose - 2).getUserName()));
            }
        }
    }

    private void pChat(PrivateChat pc) throws IOException {
        pc.startChat(writer,reader,user,fileStream);
    }

    private void serverMenu() throws IOException {
        boolean loop = true;
        ArrayList<DiscordServer> se =user.getDiscordServers();

        while (loop) {
            int num = 3 + se.size();
            StringBuilder sb = new StringBuilder("1.exit\n2.create new chat\n3.Invites\n");
            int i = 4;
            for (DiscordServer s : se) {
                sb.append(i++);
                sb.append('.');
                sb.append(s.getServerName());
                sb.append("\n");
            }

            int choose=showMenu(sb.toString(),num);
            switch (choose){
                case 1:{
                    loop=false;
                    break;
                }
                case 2:{
                    creatNewServer();
                    break;
                }
                case 3 : {
                    invites();
                    break;
                }
                default:{
                    server(se.get(choose-4));
                    break;
                }
            }
        }
    }

    private void invites () throws IOException {
        boolean loop=true;
        while (loop){
            StringBuilder sb=new StringBuilder();
            sb.append("1.exit\n");
            int index=2;
            for (DiscordServer d:user.getInvitations()) {
                sb.append(index++);
                sb.append('.');
                sb.append(d.getServerName());
                sb.append("\n");
            }
            int choose = showMenu(sb.toString(),index - 1);
            if (choose==1){
                loop=false;
            }else {
                DiscordServer d = user.getInvitations().get(choose - 2);
                int choice=showMenu("1.YES\n2.NO",2);
                if (choice==1){
                    d.addUser(user);
                }
                user.getInvitations().remove(d);
            }
        }
    }

    private void creatNewServer() throws IOException {
        boolean loop=true;
        methodWrite(Command.GETSERVERNAME.getStr());
        String serverName = null;
        String welcome = null;
        while (loop) {
            try {
                serverName = methodRead();
                if (!serverName.equals("exit")) {
                    if (controllCenter.checkServerName(serverName)) {
                        loop = false;
                    }
                } else {
                    loop = false;
                }
            }catch (DuplicateException e){
                loop = true;
                methodWrite(Command.GETSERVERNAMEAGAIN.getStr());
                methodWrite(e.toString());
            }
        }
        methodWrite(Command.GETWELLCOME.getStr());
        welcome = methodRead();

        DiscordServer server= controllCenter.createServer(fileStream,serverName,this.user,welcome);
        server(server);
    }

    private void server(DiscordServer server) throws IOException {
        server.enterMember(user,writer,reader,fileStream);
    }

    private void profile() throws IOException {
        boolean loop = true;
        while (loop){
            methodWrite(Command.PROFILEPAGE.getStr());

            methodWrite(user.getUserName());
            methodWrite(user.getEmail());
            methodWrite((user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty())? user.getPhoneNumber() : "No Phone Number");
            methodWrite(user.getStatus().getName());
            if (user.getImageFile() != null){
                methodWrite("pic");
                fileStream.methodWrite(user.getImageFile().getName());
                fileStream.sendFile(user.getImageFile());
            } else {
                methodWrite("null");
            }

            int choose = Integer.parseInt(methodRead());

            switch (choose) {
                case 1 :{
                    userName();
                    break;
                }
                case 2:{
                    password();
                    break;
                }
                case 3:{
                    email();
                    break;
                }
                case 4:{
                    phone();
                    break;
                }
                case 5:{
                    status();
                    break;
                }
                case 6:{
                    picture();
                    break;
                }
                case 7:{
                    loop=false;
                    break;
                }
            }
        }
    }

    private void userName () throws IOException {
        boolean loop = true;
        while (loop) {
            int choose = showMenu("1.change\n2.exit",2);

            switch (choose){
                case 1 : {
                    methodWrite(Command.GETUSERNAME.getStr());
                    boolean l = true;
                    String userName = null;
                    while (l) {
                        try {
                            userName = reader.readLine();
                            if (!userName.equals("exit")) {
                                l = !controllCenter.checkUserName(userName);
                            } else {
                                l = false;
                            }
                        } catch (DuplicateException | WrongFormatException e) {
                            methodWrite(Command.GETUSERNAMEAGAIN.getStr());
                            methodWrite(e.toString());
                            l = true;
                        }
                    }
                    controllCenter.changeUserName(user.getUserName(),userName);
                    break;
                }
                case 2:{
                    loop = false;
                    break;
                }
            }
        }
    }

    private void password() throws IOException {
        boolean loop = true;
        while (loop){
            int choose = showMenu("1.change\n2.exit\n",2);
            switch (choose){
                case 1:{
                    String password = null;
                    methodWrite(Command.GETPASSWORD.getStr());
                    boolean l = true;
                    while (l) {
                        try {
                            password = reader.readLine();
                            if (!password.equals("exit")) {
                                l = !controllCenter.checkPassword(password);
                            } else {
                                l = false;
                            }
                        } catch (WrongFormatException e) {
                            methodWrite(Command.GETPASSWORDAGAIN.getStr());
                            methodWrite(e.toString());
                            l = true;
                        }
                    }

                    user.setPassword(password);
                    break;
                }
                case 2:{
                    loop = false;
                    break;
                }
            }

        }
    }

    private void email() throws IOException {
        boolean loop = true;
        while (loop){
            int choose = showMenu("1.change\n2.exit\n",2);
            switch (choose){
                case 1:{
                    methodWrite(Command.GETEMAIL.getStr());
                    String email = null;
                    boolean l = true;

                    while (l) {
                        try {
                            email = reader.readLine();
                            if (!email.equals("exit")) {
                                l = !controllCenter.checkEmail(email);
                            } else {
                                l = false;
                            }
                        } catch (WrongFormatException e) {
                            methodWrite(Command.GETEMAILAGAIN.getStr());
                            methodWrite(e.toString());
                            l = true;
                        }
                    }
                    user.setEmail(email);
                    break;
                }
                case 2:{
                    loop = false;
                    break;
                }
            }

        }
    }

    private void phone() throws IOException {
        boolean loop = true;
        while (loop){
            if (user.getPhoneNumber()!=null){
            }
            int choose = showMenu("1.change\n2.remove\n3.exit\n",3);
            switch (choose){
                case 1:{
                    String phone = null;
                    methodWrite(Command.GETPHONE.getStr());
                    boolean l = true;
                    while (l) {
                        try {
                            phone = reader.readLine();
                            if (!phone.equals("exit")) {
                                l = !controllCenter.checkPhone(phone);
                            } else {
                                l = false;
                            }
                        } catch (WrongFormatException e) {
                            methodWrite(Command.GETPHONEAGAIN.getStr());
                            methodWrite(e.toString());
                            l = true;
                        }
                    }
                    user.setPhoneNumber(phone);
                    break;
                }

                case 2: {
                    user.setPhoneNumber(null);
                    notificationStream.sendPopUp("Phone Number","Your Phone Number has been Deleted Successfully");
                    loop = false;
                    break;
                }
                case 3:{
                    loop = false;
                    break;
                }
            }

        }
    }

    private void status() throws IOException {
        boolean loop = true;
        while (loop){
            int choose = showMenu("1.change\n2.exit\n",2);
            switch (choose){
                case 1: {
                    ArrayList<User.Status> statuses = new ArrayList<>(List.of(User.Status.values()));
                    statuses.removeIf(w -> w.equals(User.Status.OFFLINE));

                    StringBuilder sb = new StringBuilder();
                    int i = 1;
                    for (User.Status status : statuses) {
                        sb.append(i++).append(".").append(status.getName()).append("\n");
                    }
                    int choice = showMenu(sb.toString(), 4);
                    user.setStatus(statuses.get(choice - 1));
                }
                case 2:{
                    loop = false;
                    break;
                }
            }

        }
    }

    private void picture() throws IOException {
        methodWrite(Command.GETPROFILEPICTURE.getStr());
        if (user.getImageFile() != null){
            methodWrite("pic");
            fileStream.methodWrite(user.getImageFile().getName());
            fileStream.sendFile(user.getImageFile());
        } else {
            methodWrite("null");
        }
        String input = methodRead();
        if (!input.equals("#exit")) {
            String[] fname = input.split("\\.");
            File file = new File("./ProfilePics/" + user.getUserName() + "." + fname[fname.length - 1]);
            fileStream.receiveFile(file);
            user.setImageFile(file);
        }
    }



    private void logOut () {
        if (user.getStatus()== User.Status.ONLINE){
            user.setStatus(User.Status.OFFLINE);
        }
        user.setUserThread(null);
        System.out.println(user.getUserName() + " Logged Out.");
    }

    private void exit() throws IOException {
        methodWrite(Command.EXIT.getStr());
        System.out.println("a Client Left.");
        notificationStream.close();
        Thread.currentThread().interrupt();
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

    public void sendPopUp(String title, String description){
        notificationStream.sendPopUp(title,description);
    }
}

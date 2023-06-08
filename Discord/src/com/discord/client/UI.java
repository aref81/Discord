package com.discord.client;

import java.io.File;
import java.util.Scanner;

public class UI {
    public static void welcome(String str){
        System.out.println(str);
    }

    public static String getUserName(){
        System.out.println("Please enter your user name");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }

    public static String getUserName(String str){
        System.out.println(str);
        System.out.println("Please enter your user name again");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }

    public static String getPassword(){
        System.out.println("Please enter your password");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }

    public static String getPassword(String str){
        System.out.println(str);
        System.out.println("Please enter your password again");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }

    public static String getEmail(){
        System.out.println("Please enter your e-mail");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }

    public static String getEmail(String str){
        System.out.println(str);
        System.out.println("Please enter your e-mail again");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }

    public static void print(String str){
        System.out.println(str);
    }

    public static String ShowMenu (String str) {
        System.out.println(str);
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }
    public static void exit (){
        System.out.println("Exiting The Application");
    }

    public static String getCreateFriend(){
        System.out.println("Please enter desired user name");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }
    public static String getServerName(){
        System.out.println("Please enter your server name");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }
    public static String getServerName(String str){
        System.out.println(str);
        System.out.println("Please enter your server name again");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }

    public static String getWelcome(){
        System.out.println("Please enter your welcome message");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }
    public static String getRoleName(){
        System.out.println("Please enter your Role name");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }
    public static String getRoleName(String str){
        System.out.println(str);
        System.out.println("Please enter your Role name again");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }
    public static String getChannelName(){
        System.out.println("Please enter your Channel name");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }
    public static String getChannelName(String str){
        System.out.println(str);
        System.out.println("Please enter your Channel name again");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }
    public static String getPhone(){
        System.out.println("Please enter your Phone number");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }
    public static String getPhone(String str){
        System.out.println(str);
        System.out.println("Please enter your Phone number again");
        Scanner sc=new Scanner(System.in);
        return sc.nextLine();
    }

    public static File getProfilePicture(){
        System.out.println("Please enter your Profile picture address");
        Scanner sc=new Scanner(System.in);
        File file = null;
        boolean loop = true;
        while (loop) {
            file = new File(sc.nextLine());
            if (file.exists()){
                loop = false;
            } else {
                System.out.println("Address is not valid.");
                file = new File(sc.nextLine());
            }
        }
        return file;
    }


}

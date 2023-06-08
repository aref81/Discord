package com.discord.client.chat;

import com.discord.client.Client;
import com.discord.client.ClientFileStream;
import com.discord.server.utils.FileStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class WriteThread implements Runnable{

    private BufferedWriter writer;
    private ClientFileStream fileStream;

    public WriteThread(BufferedWriter writer, ClientFileStream fileStream) {
        this.writer = writer;
        this.fileStream = fileStream;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();
        while (!str.equals("#exit")){
            try {
                if (str.equals("")){
                    str = scanner.nextLine();
                    continue;
                }else if (str.contains("#sendFile")){
                    File file = new File(str.split("-")[1]);
                    if (file.exists()) {
                        str = "#sendFile";
                        writer.write(str);
                        writer.newLine();
                        writer.flush();
                        fileStream.sendFile(file);
                        str = "";
                        continue;
                    } else {
                        System.out.println("Address is not valid");
                        continue;
                    }
                } else if (str.contains("#download")){
                    writer.write(str);
                    writer.newLine();
                    writer.flush();
                    fileStream.receiveFile();
                    str ="";
                    continue;
                }
                writer.write(str);
                writer.newLine();
                writer.flush();
                str = scanner.nextLine();
            } catch (IOException e) {
                Thread.currentThread().interrupt();
            }
        }
        try {
            writer.write(str);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
    }
}

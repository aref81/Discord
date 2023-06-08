package com.discord.client.chat;

import com.discord.Command;
import com.discord.client.ClientFileStream;
import com.discord.server.utils.FileStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadThread implements Runnable{
    private BufferedReader reader;
    private ClientFileStream fileStream;

    public ReadThread(BufferedReader reader, ClientFileStream fileStream) {
        this.reader = reader;
        this.fileStream = fileStream;
    }

    @Override
    public void run() {
        try {
            String str = methodRead();
            while (!str.equals(Command.EXITCHATMODE.getStr())){
                System.out.println(str);
                str = methodRead();
            }
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String methodRead () throws IOException {
        String str = reader.readLine();
        while (str.equals("")){
            str = reader.readLine();
        }
        return str;
    }
}

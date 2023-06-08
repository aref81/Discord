package com.discord.server.utils.exceptions;

public class WrongFormatException extends Exception{
    private String message;

    public WrongFormatException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message+"\n";
    }

}

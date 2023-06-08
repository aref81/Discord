package com.discord.server.utils.exceptions;

public class DuplicateException extends Exception{
    private String message;

    public DuplicateException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message + "\n" ;
    }
}

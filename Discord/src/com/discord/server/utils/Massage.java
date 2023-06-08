package com.discord.server.utils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Date;


public class Massage implements Serializable {

    public enum reaction{
        LIKE,
        DISLIKE,
        LAUGHTER
    }

    private String text;
    private User author;
    private LocalDate date;
    private long id;
    private ArrayList<User> likes;
    private ArrayList<User> dislikes;
    private ArrayList<User> laughters;
    public Massage(String text, User author,long id) {
        this.text = text;
        this.author = author;
        this.date = LocalDate.now();
        this.id= id;
        likes=new ArrayList<>();
        dislikes=new ArrayList<>();
        laughters=new ArrayList<>();
    }

    public String getText() {
        return text;
    }

    public User getAuthor() {
        return author;
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getId() {
        return id;
    }
    public void addLike(User user){
        likes.add(user);
    }

    public void addDislike(User user){
        dislikes.add(user);
    }

    public void addLaughter(User user){
        laughters.add(user);
    }

    @Override
    public String toString() {
        return "Massage{" +
                "text='" + text + '\'' +
                ", author=" + author +
                ", date=" + date +
                ", id=" + id +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", laughters=" + laughters +
                '}';
    }
}

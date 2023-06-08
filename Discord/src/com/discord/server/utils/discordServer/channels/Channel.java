package com.discord.server.utils.discordServer.channels;

import com.discord.server.utils.FileStream;
import com.discord.server.utils.Massage;
import com.discord.server.utils.User;
import com.discord.server.utils.discordServer.Member;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Channel implements Serializable {
    private String name;
    protected boolean history;
    protected boolean limited;
    protected HashMap<Member,ArrayList<Massage>> active;
    protected long keepId;
    protected FileStream fileStream;

    public Channel(FileStream fileStream,String name,boolean history) {
        this.history = history;
        this.name = name;
        active = new HashMap<>();
        keepId = 0L;
        limited = false;
        this.fileStream = fileStream;
    }

    public Long getId () {
        return keepId++;
    }

    public Long getHistory () {
        return keepId;
    }

    public String getName() {
        return name;
    }

    public abstract void start(Member member,Long history);

    public boolean isHistory() {
        return history;
    }

    public void setHistory(boolean history) {
        this.history = history;
    }

    public boolean isLimited() {
        return limited;
    }

    public void setLimited(boolean limited) {
        this.limited = limited;
    }
}

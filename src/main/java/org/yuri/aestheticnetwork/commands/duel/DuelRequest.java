package org.yuri.aestheticnetwork.commands.duel;

import org.bukkit.entity.Player;

public class DuelRequest {
    private final Player sender;
    private final Player reciever;
    private final String type;
    private final int maxrounds;
    private final long starttime;
    private final int arena;
    private final boolean legacy;
    private int rounds;
    private int red;
    private int blue;

    public DuelRequest(Player sender,
                       Player reciever,
                       String type,
                       int maxrounds,
                       int rounds,
                       int sr,
                       int sb,
                       long starttime,
                       int arena,
                       boolean legacy) {
        this.sender = sender;
        this.reciever = reciever;
        this.type = type;
        this.rounds = rounds;
        this.red = sr;
        this.blue = sb;
        this.maxrounds = maxrounds;
        this.starttime = starttime;
        this.arena = arena;
        this.legacy = legacy;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReciever() {
        return reciever;
    }

    public String getType() {
        return type;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int i) {
        this.rounds = i;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int i) {
        this.red = i;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int i) {
        this.blue = i;
    }

    public int getMaxrounds() {
        return maxrounds;
    }

    public long getStart() {
        return starttime;
    }

    public int getArena() {
        return arena;
    }

    public boolean IsLegacy() {
        return legacy;
    }
}
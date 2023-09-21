package org.yuri.aestheticnetwork.commands.duel;

import org.bukkit.entity.Player;
import org.eclipse.sisu.inject.Weak;

import java.lang.ref.WeakReference;

public class DuelRequest {
    private final WeakReference<Player> sender;
    private final WeakReference<Player> reciever;
    private final String type;
    private final int maxrounds;
    private final long starttime;
    private final int arena;
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
                       int arena) {
        this.sender = new WeakReference<>(sender);
        this.reciever = new WeakReference<>(reciever);
        this.type = type;
        this.rounds = rounds;
        this.red = sr;
        this.blue = sb;
        this.maxrounds = maxrounds;
        this.starttime = starttime;
        this.arena = arena;
    }

    public Player getSender() {
        return sender.get();
    }

    public Player getReciever() {
        return reciever.get();
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
}
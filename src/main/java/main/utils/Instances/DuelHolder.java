package main.utils.Instances;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DuelHolder {
    private String sender;
    private String receiver;
    private int type;
    private int maxrounds;
    private long starttime;
    private int arena;
    private int maxPlayers;
    private int rounds;
    private int red;
    private int blue;

    public DuelHolder(String sender, String reciever, int type, int maxrounds, int rounds, int sr, int sb, long starttime, int arena, int maxPlayers) {
        this.sender = sender;
        this.receiver = reciever;
        this.type = type;
        this.rounds = rounds;
        this.red = sr;
        this.blue = sb;
        this.maxrounds = maxrounds;
        this.starttime = starttime;
        this.arena = arena;
        this.maxPlayers = maxPlayers;
    }

    public Player getSender() {
        return Bukkit.getPlayer(sender);
    }

    public String getReceiver() {
        return receiver;
    }

    public int getType() {
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

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
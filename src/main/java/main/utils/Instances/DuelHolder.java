package main.utils.Instances;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DuelHolder {
    private final String sender;
    @Getter
    private final String receiver;
    @Getter
    private final int type;
    @Getter
    private final int maxrounds;
    @Getter
    private final long start;
    @Getter
    private final int arena;
    @Getter
    private final int maxPlayers;
    @Getter
    @Setter
    private int rounds;
    @Getter
    @Setter
    private int red;
    @Getter
    @Setter
    private int blue;

    public DuelHolder(String sender, String reciever, int type, int maxrounds, int rounds, int sr, int sb, long start, int arena, int maxPlayers) {
        this.sender = sender;
        this.receiver = reciever;
        this.type = type;
        this.rounds = rounds;
        this.red = sr;
        this.blue = sb;
        this.maxrounds = maxrounds;
        this.start = start;
        this.arena = arena;
        this.maxPlayers = maxPlayers;
    }

    public Player getSender() {
        return Bukkit.getPlayer(sender);
    }

    public String getSenderF() {
        return sender;
    }
}
package main.utils.Instances;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TpaRequest {
    private final String sender;
    private final String receiver;
    private final boolean type;

    public TpaRequest(String sender, String reciever, boolean type) {
        this.sender = sender;
        this.receiver = reciever;
        this.type = type;
    }

    public Player getSender() {
        return Bukkit.getPlayer(sender);
    }

    public Player getReceiver() {
        return Bukkit.getPlayer(receiver);
    }

    public boolean isHere() {
        return type;
    }
}
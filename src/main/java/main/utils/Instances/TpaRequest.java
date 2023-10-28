package main.utils.Instances;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TpaRequest {
    private String sender;
    private String receiver;
    private boolean type;

    public TpaRequest(String sender, String reciever, boolean type) {
        this.sender = sender;
        this.receiver = reciever;
        this.type = type;
    }

    public Player getSender() {
        return Bukkit.getPlayer(sender);
    }

    public String getSenderF() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public boolean isHere() {
        return type;
    }
}
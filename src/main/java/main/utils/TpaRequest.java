package main.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TpaRequest {
    private final String sender;
    private final String reciever;
    private final boolean type;
    private final boolean showacceptmsg;

    public TpaRequest(String sender, String receiver, boolean type, boolean showacceptmsg) {
        this.sender = sender;
        this.reciever = receiver;
        this.type = type;
        this.showacceptmsg = showacceptmsg;
    }

    public Player getSender() {
        return Bukkit.getPlayer(sender);
    }

    public boolean getTpaAll() {
        return !showacceptmsg;
    }

    public Player getReceiver() {
        return Bukkit.getPlayer(reciever);
    }

    public boolean isHere() {
        return type;
    }
}
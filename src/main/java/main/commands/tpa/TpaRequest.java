package main.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

public class TpaRequest {
    private WeakReference<String> sender;
    private WeakReference<String> reciever;
    private boolean type;

    public TpaRequest(String sender, String reciever, boolean type) {
        this.sender = new WeakReference<>(sender);
        this.reciever = new WeakReference<>(reciever);
        this.type = type;
    }

    public Player getSender() {
        return Bukkit.getPlayer(sender.get());
    }

    public Player getReciever() {
        return Bukkit.getPlayer(reciever.get());
    }

    public boolean isHere() {
        return type;
    }
}
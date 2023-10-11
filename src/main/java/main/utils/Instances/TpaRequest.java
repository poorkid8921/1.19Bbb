package main.utils.Instances;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

public class TpaRequest {
    private final WeakReference<String> sender;
    private final WeakReference<String> reciever;
    private final boolean type;

    public TpaRequest(String sender, String reciever, boolean type) {
        this.sender = new WeakReference<>(sender);
        this.reciever = new WeakReference<>(reciever);
        this.type = type;
    }

    public Player getSender() {
        return Bukkit.getPlayer(sender.get());
    }

    public Player getReceiver() {
        return Bukkit.getPlayer(reciever.get());
    }

    public boolean isHere() {
        return type;
    }
}
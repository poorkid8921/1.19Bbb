package commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

public class TpaRequest {
    private final WeakReference<String> sender;
    private final WeakReference<String> reciever;
    private final int type;
    private final boolean showacceptmsg;

    public TpaRequest(String sender, String reciever, int type, boolean showacceptmsg) {
        this.sender = new WeakReference<>(sender);
        this.reciever = new WeakReference<>(reciever);
        this.type = type;
        this.showacceptmsg = showacceptmsg;
    }

    public Player getSender() {
        return Bukkit.getPlayer(sender.get());
    }

    public boolean getTpaAll() {
        return !showacceptmsg;
    }

    public Player getReceiver() {
        return Bukkit.getPlayer(reciever.get());
    }

    public int getType() {
        return type;
    }
}
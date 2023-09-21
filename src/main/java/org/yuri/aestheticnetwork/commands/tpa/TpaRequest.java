package org.yuri.aestheticnetwork.commands.tpa;

import org.bukkit.entity.Player;
import org.yuri.aestheticnetwork.utils.Instances.Type;

import java.lang.ref.WeakReference;

public class TpaRequest {
    private final WeakReference<Player> sender;
    private final WeakReference<Player> reciever;
    private final Type type;

    public TpaRequest(Player sender, Player reciever, Type type) {
        this.sender = new WeakReference<>(sender);
        this.reciever = new WeakReference<>(reciever);
        this.type = type;
    }

    public Player getSender() {
        return sender.get();
    }

    public Player getReciever() {
        return reciever.get();
    }

    public Type getType() {
        return type;
    }
}
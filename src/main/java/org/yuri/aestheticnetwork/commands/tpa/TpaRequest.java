package org.yuri.aestheticnetwork.commands.tpa;

import org.bukkit.entity.Player;
import org.yuri.aestheticnetwork.utils.Type;

public class TpaRequest {
    private final Player sender;
    private final Player reciever;
    private final Type type;

    public TpaRequest(Player sender, Player reciever, Type type) {
        this.sender = sender;
        this.reciever = reciever;
        this.type = type;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReciever() {
        return reciever;
    }

    public Type getType() {
        return type;
    }
}
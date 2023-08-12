package org.yuri.aestheticnetwork.tpa;

import org.bukkit.entity.Player;
import org.yuri.aestheticnetwork.Type;

public class TpaRequest {
    private final Player sender;
    private final Player reciever;
    private final Type type;
    private final boolean showacceptmsg;

    public TpaRequest(Player sender, Player reciever, Type type, boolean showacceptmsg) {
        this.sender = sender;
        this.reciever = reciever;
        this.type = type;
        this.showacceptmsg = showacceptmsg;
    }

    public Player getSender() {
        return sender;
    }

    public boolean getTpaAll()
    {
        return showacceptmsg;
    }

    public Player getReciever()
    {
        return reciever;
    }

    public Type getType() {
        return type;
    }
}
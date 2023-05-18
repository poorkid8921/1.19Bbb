package bab.bbb.tpa;

import bab.bbb.utils.Type;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class TpaRequest {
    private final Player sender;
    private final Player reciever;
    private final Type type;
    public ArrayList<UUID> recievers = new ArrayList<>();
    public ArrayList<UUID> senders = new ArrayList<>();

    public TpaRequest(Player sender, Player reciever, Type type) {
        this.sender = sender;
        this.reciever = reciever;
        this.type = type;
        senders.add(sender.getUniqueId());
        recievers.add(reciever.getUniqueId());
    }

    public Player getSender() {
        return sender;
    }

    public Player getReciever()
    {
        return reciever;
    }

    public Type getType() {
        return type;
    }
}
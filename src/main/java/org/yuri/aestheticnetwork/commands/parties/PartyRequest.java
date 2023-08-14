package org.yuri.aestheticnetwork.commands.parties;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PartyRequest {
    private final Player sender;
    private final Player receiver;

    public PartyRequest(Player sender,
                        Player receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public Player getSender() {
        return sender;
    }
    public Player getReceiver() {
        return receiver;
    }
}

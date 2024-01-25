package main.utils.Instances;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TpaRequest {
    private final String sender;
    @Getter
    private final String receiver;
    @Getter
    private final boolean here;
    @Getter
    @Setter
    private int runnableid;

    public TpaRequest(String sender, String reciever, boolean here) {
        this.sender = sender;
        this.receiver = reciever;
        this.here = here;
    }

    public Player getSender() {
        return Bukkit.getPlayer(sender);
    }

    public String getSenderF() {
        return sender;
    }
}
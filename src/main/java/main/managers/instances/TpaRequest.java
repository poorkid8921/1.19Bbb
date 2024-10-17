package main.managers.instances;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
@Setter
public class TpaRequest {
    private final String sender, receiver;
    private final boolean here, showacceptmsg;
    private int runnableid;

    public TpaRequest(String sender, String receiver, boolean here, boolean showacceptmsg) {
        this.sender = sender;
        this.receiver = receiver;
        this.here = here;
        this.showacceptmsg = showacceptmsg;
    }

    public boolean getTpaAll() {
        return showacceptmsg;
    }

    public Player getSender() {
        return Bukkit.getPlayer(sender);
    }

    public String getSenderF() {
        return sender;
    }
}
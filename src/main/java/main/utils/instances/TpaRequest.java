package main.utils.instances;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TpaRequest {
    private final String sender;
    @Getter
    private final String receiver;
    @Getter
    private final boolean here;
    @Getter
    private final boolean showacceptmsg;

    public TpaRequest(String sender, String receiver, boolean here, boolean showacceptmsg) {
        this.sender = sender;
        this.receiver = receiver;
        this.here = here;
        this.showacceptmsg = showacceptmsg;
    }

    public boolean getTpaAll() {
        return !showacceptmsg;
    }

    public Player getSender() {
        return Bukkit.getPlayer(sender);
    }

    public String getSenderF() {
        return sender;
    }
}
package common.commands.tpa;

import org.bukkit.entity.Player;
import org.yuri.eco.utils.Utils;

public class TpaRequest {
    private final Player sender;
    private final Player reciever;
    private final Utils.Type type;
    private final boolean showacceptmsg;

    public TpaRequest(Player sender, Player reciever, Utils.Type type, boolean showacceptmsg) {
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

    public Utils.Type getType() {
        return type;
    }
}
package common.commands.tpa;

import org.bukkit.entity.Player;
import org.yuri.eco.utils.Utils;

import java.lang.ref.WeakReference;

public class TpaRequest {
    private final WeakReference<Player> sender;
    private final WeakReference<Player> reciever;
    private final Utils.Type type;
    private final boolean showacceptmsg;

    public TpaRequest(Player sender, Player reciever, Utils.Type type, boolean showacceptmsg) {
        this.sender = new WeakReference<>(sender);
        this.reciever = new WeakReference<>(reciever);
        this.type = type;
        this.showacceptmsg = showacceptmsg;
    }

    public Player getSender() {
        return sender.get();
    }

    public boolean getTpaAll()
    {
        return showacceptmsg;
    }

    public Player getReciever()
    {
        return reciever.get();
    }

    public Utils.Type getType() {
        return type;
    }
}
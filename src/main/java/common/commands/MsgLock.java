package common.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.eco.utils.Initializer;
import org.yuri.eco.utils.Utils;

import static org.yuri.eco.utils.Initializer.msg;

public class MsgLock implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String p = sender.getName();
        if (Utils.manager().get("r." + p + ".m") != null) {
            sender.sendMessage(Utils.translateo("&7You can receive messages from players again."));
            Utils.manager().set("r." + p + (Utils.manager().get("r." + p + ".t") == null ? "" : ".m"), null);
            Initializer.p.saveCustomConfig();
            msg.add(p);
        }
        else
        {
            sender.sendMessage(Utils.translateo("&7You will no longer receive messages from players."));
            Utils.manager().set("r." + p + ".m", "");
            Initializer.p.saveCustomConfig();
            msg.remove(p);
        }
        return true;
    }
}

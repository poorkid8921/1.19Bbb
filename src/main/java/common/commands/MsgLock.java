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
        Player p = ((Player) sender);
        if (Utils.manager().get("r." + p.getUniqueId() + ".m") != null) {
            p.sendMessage(Utils.translateo("&7You can receive messages from players again"));
            Utils.manager().set("r." + p.getUniqueId() + (Utils.manager().get("r." + p.getUniqueId() + ".t") == null ? "" : ".m"), null);
            Initializer.p.saveCustomConfig();
            msg.add(p.getName());
        }
        else
        {
            p.sendMessage(Utils.translateo("&7You will no longer receive messages from players"));
            Utils.manager().set("r." + p.getUniqueId() + ".m", "");
            Initializer.p.saveCustomConfig();
            msg.remove(p.getName());
        }
        return true;
    }
}

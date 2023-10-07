package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import main.utils.Initializer;
import main.utils.Utils;

import static main.utils.Initializer.msg;

public class MsgLock implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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

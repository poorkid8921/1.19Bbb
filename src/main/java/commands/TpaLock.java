package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import main.utils.Initializer;
import main.utils.Utils;

import static main.utils.Initializer.tpa;

public class TpaLock implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = sender.getName();
        if (Utils.manager().get("r." + p + ".t") != null) {
            sender.sendMessage(Utils.translateo("&7You can receive tp requests again."));
            Utils.manager().set("r." + p + (Utils.manager().get("r." + p + ".m") == null ? "" : ".t"), null);
            Initializer.p.saveCustomConfig();
            tpa.add(p);
        } else {
            sender.sendMessage(Utils.translateo("&7You will no longer receive tp requests."));
            Utils.manager().set("r." + p + ".t", "");
            Initializer.p.saveCustomConfig();
            tpa.remove(p);
        }
        return true;
    }
}

package common.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.eco.utils.Initializer;
import org.yuri.eco.utils.Utils;

import static org.yuri.eco.utils.Initializer.tpa;

public class TpaLock implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = ((Player) sender);
        if (Utils.manager().get("r." + p.getUniqueId() + ".t") != null) {
            p.sendMessage(Utils.translateo("&7You can receive tp requests again"));
            Utils.manager().set("r." + p.getUniqueId() + (Utils.manager().get("r." + p.getUniqueId() + ".m") == null ? "" : ".t"), null);
            Initializer.p.saveCustomConfig();
            tpa.add(p.getName());
        } else {
            p.sendMessage(Utils.translateo("&7You will no longer receive tp requests"));
            Utils.manager().set("r." + p.getUniqueId() + ".t", "");
            Initializer.p.saveCustomConfig();
            tpa.remove(p.getName());
        }
        return true;
    }
}

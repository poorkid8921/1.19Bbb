package org.yuri.aestheticnetwork;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TpaLock implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = ((Player) sender);
        if (Utils.manager().get("r." + p.getUniqueId() + ".t") != null) {
            p.sendMessage(Utils.translate("&7You can receive tpa requests again."));
            Utils.manager().set("r." + p.getUniqueId() + (Utils.manager().get("r." + p.getUniqueId() + ".m") == null ? "" : ".t"), null);
            AestheticNetwork.getInstance().saveCustomConfig();
            AestheticNetwork.tpa.add(p.getName());
        }
        else
        {
            p.sendMessage(Utils.translate("&7You will no longer receive tpa requests."));
            Utils.manager().set("r." + p.getUniqueId() + ".t", "");
            AestheticNetwork.getInstance().saveCustomConfig();
            AestheticNetwork.tpa.remove(p.getName());
        }
        return true;
    }
}

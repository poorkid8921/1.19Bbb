package org.yuri.aestheticnetwork.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.utils.Utils;

import static org.yuri.aestheticnetwork.utils.Initializer.tpa;

public class TpaLock implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = ((Player) sender);
        String name = p.getName();
        if (Utils.manager1().get("r." + name + ".t") != null) {
            p.sendMessage(Utils.translateo("&7You can receive tp requests again"));
            Utils.manager1().set("r." + name + (Utils.manager1().get("r." + name + ".m") == null ? "" : ".t"), null);
            AestheticNetwork.getInstance().saveCustomConfig1();
            tpa.add(p.getName());
        }
        else
        {
            p.sendMessage(Utils.translateo("&7You will no longer receive tp requests"));
            Utils.manager1().set("r." + name + ".t", "");
            AestheticNetwork.getInstance().saveCustomConfig1();
            tpa.remove(p.getName());
        }
        return true;
    }
}

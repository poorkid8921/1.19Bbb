package org.yuri.aestheticnetwork.commands;

import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yuri.aestheticnetwork.AestheticNetwork;

public class ffa implements CommandExecutor {
    AestheticNetwork plugin = AestheticNetwork.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player pp)
            PaperLib.teleportAsync(pp, new Location(plugin.ffa.getWorld(),
                    plugin.ffa.getX(),
                    plugin.ffa.getY(),
                    plugin.ffa.getZ(),
                    pp.getLocation().getYaw(),
                    pp.getLocation().getPitch()));

        return true;
    }
}
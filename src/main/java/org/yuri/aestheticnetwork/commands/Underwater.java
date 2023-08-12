package org.yuri.aestheticnetwork.commands;

import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yuri.aestheticnetwork.AestheticNetwork;

public class Underwater implements CommandExecutor {
    AestheticNetwork plugin = AestheticNetwork.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player pp)
            PaperLib.teleportAsync(pp, new Location(plugin.underwater.getWorld(),
                    plugin.underwater.getX(),
                    plugin.underwater.getY(),
                    plugin.underwater.getZ(),
                    pp.getLocation().getYaw(),
                    pp.getLocation().getPitch()));

        return true;
    }
}
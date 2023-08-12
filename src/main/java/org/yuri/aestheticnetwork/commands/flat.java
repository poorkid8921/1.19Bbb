package org.yuri.aestheticnetwork.commands;

import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yuri.aestheticnetwork.AestheticNetwork;

public class flat implements CommandExecutor {
    AestheticNetwork plugin = AestheticNetwork.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player pp)
            PaperLib.teleportAsync(pp, new Location(plugin.flat.getWorld(),
                    plugin.flat.getX(),
                    plugin.flat.getY(),
                    plugin.flat.getZ(),
                    pp.getLocation().getYaw(),
                    pp.getLocation().getPitch()));

        return true;
    }
}
package org.yuri.aestheticnetwork.commands;

import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.commands.duel.KitManager;

public class Nethpot implements CommandExecutor {
    AestheticNetwork plugin = AestheticNetwork.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player pp)
            PaperLib.teleportAsync(pp, new Location(plugin.nethpot.getWorld(),
                    plugin.nethpot.getX(),
                    plugin.nethpot.getY(),
                    plugin.nethpot.getZ(),
                    pp.getLocation().getYaw(),
                    pp.getLocation().getPitch())).thenAccept(reason -> KitManager.nethpot(pp));

        return true;
    }
}
package org.yuri.aestheticnetwork.commands;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.utils.Messages.Initializer;

public class ffa implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player pp)
            PaperLib.teleportAsync(pp, new Location(Bukkit.getWorld("world"),
                    Initializer.ffa.getX(),
                    Initializer.ffa.getY(),
                    Initializer.ffa.getZ(),
                    pp.getLocation().getYaw(),
                    pp.getLocation().getPitch())).thenAccept(reason -> Initializer.ffaconst.add(pp));

        return true;
    }
}
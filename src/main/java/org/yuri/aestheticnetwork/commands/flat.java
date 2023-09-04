package org.yuri.aestheticnetwork.commands;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.utils.Initializer;

public class flat implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player pp)
            PaperLib.teleportAsync(pp, new Location(Bukkit.getWorld("world"),
                    Initializer.flat.getX(),
                    Initializer.flat.getY(),
                    Initializer.flat.getZ(),
                    pp.getLocation().getYaw(),
                    pp.getLocation().getPitch()));

        return true;
    }
}
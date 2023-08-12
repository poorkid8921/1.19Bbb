package org.yuri.aestheticnetwork;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class spawn implements CommandExecutor {
    AestheticNetwork plugin = AestheticNetwork.getInstance();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player pp) {
            PaperLib.teleportAsync(pp, new Location(Bukkit.getWorld(plugin.getConfig().getString("Spawn.World")), plugin.getConfig().getDouble("Spawn.X"), plugin.getConfig().getDouble("Spawn.Y"), plugin.getConfig().getDouble("Spawn.Z")));
            return true;
        }

        return false;
    }
}
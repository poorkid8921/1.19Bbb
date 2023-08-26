package common.commands;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.eco.AestheticNetwork;
import org.yuri.eco.utils.Initializer;

public class spawn implements CommandExecutor {
    AestheticNetwork plugin = Initializer.p;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player pp) {
            PaperLib.teleportAsync(pp, new Location(Bukkit.getWorld("world"),
                    plugin.getConfig().getDouble("Spawn.X"),
                    plugin.getConfig().getDouble("Spawn.Y"),
                    plugin.getConfig().getDouble("Spawn.Z")));
            return true;
        }

        return false;
    }
}
package main.commands.ess;

import main.utils.Languages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportAll implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            Location d = ((Player) sender).getLocation();
            Bukkit.getOnlinePlayers().forEach(p -> p.teleportAsync(d));
        }
        return true;
    }
}
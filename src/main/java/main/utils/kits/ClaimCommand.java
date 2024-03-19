package main.utils.kits;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClaimCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String kit = command.getName();
        KitClaimer.claim((Player) sender, kit.substring(0, 1).toUpperCase() + kit.substring(1, 3) + " " + kit.charAt(3), true);
        return true;
    }
}

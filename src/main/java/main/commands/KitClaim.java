package main.commands;

import main.utils.KitClaimer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitClaim implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        KitClaimer.claim((Player) sender, command.getName().charAt(3), true);
        return true;
    }
}

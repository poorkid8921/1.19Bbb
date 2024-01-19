package main.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Clear implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ((Player) sender).getInventory().clear();
        sender.sendMessage("§7Your inventory has been successfully cleared.");
        return true;
    }
}

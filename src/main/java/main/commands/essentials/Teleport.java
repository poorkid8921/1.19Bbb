package main.commands.essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Initializer.playerData;

public class Teleport implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (playerData.get(sender.getName()).getRank() > 8) {
            if (args.length == 0) {
                sender.sendMessage("§7You must specify a player!");
                return true;
            }

            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage("§7You must specify a player!");
                return true;
            }

            ((Player) sender).teleport(p.getLocation());
        }
        return true;
    }
}

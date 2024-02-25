package main.commands.essentials;

import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Initializer.playerData;

public class Unban implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (playerData.get(sender.getName()).getRank() > 8) {
            if (args.length == 0) {
                sender.sendMessage("§7Invalid args.");
                return true;
            }

            Player pp = Bukkit.getPlayer(args[0]);
            if (pp == null) {
                sender.sendMessage("§7Failed to unban " + args[0] + ".");
                return true;
            }
            Initializer.bannedFromflat.remove(pp.getName());
            sender.sendMessage("§7Successfully unbanned " + args[0] + ".");
        }
        return true;
    }
}

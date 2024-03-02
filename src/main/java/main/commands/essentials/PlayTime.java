package main.commands.essentials;

import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayTime implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage("§7You must specify a valid player!");
                return true;
            }
            sender.sendMessage("§6Playtime of " + p.getName() + ": §c" + Utils.getTime(p));
            return true;
        }
        sender.sendMessage("§6Playtime: §c" + Utils.getTime((Player) sender));
        return true;
    }
}
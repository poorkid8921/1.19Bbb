package main.commands.essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

import static main.utils.Initializer.playerData;

public class Teleport implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (playerData.get(sender.getName()).getRank() > 6) {
            if (args.length == 0) {
                sender.sendMessage("§7You must specify a player!");
                return true;
            }
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage("§7You must specify a player!");
                return true;
            }
            ((Player) sender).teleport(player.getLocation());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length < 2 ? null : Collections.emptyList();
    }
}

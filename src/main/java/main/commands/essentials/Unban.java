package main.commands.essentials;

import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static main.utils.Initializer.playerData;

public class Unban implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (playerData.get(sender.getName()).getRank() > 6) {
            if (args.length == 0) {
                sender.sendMessage("§7Invalid arguments.");
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

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length < 2 ? null : Collections.emptyList();
    }
}

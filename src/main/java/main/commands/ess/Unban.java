package main.commands.ess;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.commands.ess.Ban.flatB;

public class Unban implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("has.staff")) {
            if (args.length < 1) {
                sender.sendMessage("§7Invalid args.");
                return true;
            }

            Player pp = Bukkit.getPlayer(args[0]);
            if (pp == null) {
                sender.sendMessage("§7Failed to unban " + args[0] + ".");
                return true;
            }
            flatB.remove(pp.getName());
            sender.sendMessage("§7Successfully unbanned " + args[0] + ".");
        }
        return true;
    }
}

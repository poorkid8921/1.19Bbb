package main.commands;

import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Initializer.spawn;

public class Ban implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("has.staff")) {
            if (args.length < 1) {
                sender.sendMessage("§7Invalid args.");
                return true;
            }

            Player pp = Bukkit.getPlayer(args[0]);
            if (pp == null) {
                sender.sendMessage("§7Failed to ban " + args[0] + ".");
                return true;
            }
            String d = args.length > 1 ? args[1] : null;
            Initializer.bannedFromflat.add(pp.getName());
            sender.sendMessage("§7Successfully banned " + args[0] + ".");
            pp.teleportAsync(spawn).thenAccept(r -> pp.sendMessage("§7You are now banned in flat for " + (d == null ? "breaking rules." : d + ".")));
        }
        return true;
    }
}

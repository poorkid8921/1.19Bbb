package main.commands.tpa;

import main.utils.instances.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Initializer.playerData;
import static main.utils.Utils.*;

public class Tpa implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7You must specify who you want to teleport to!");
            return true;
        }
        final Player recipient = Bukkit.getPlayer(args[0]);
        if (recipient == null) {
            sender.sendMessage("§7You can't send teleport requests to offline players.");
            return true;
        }
        final String targetName = recipient.getName();
        final String name = sender.getName();
        if (targetName == name) {
            sender.sendMessage("§7You can't teleport to yourself.");
            return true;
        }
        if (getRequest(targetName) != null && hasRequest(name, targetName)) {
            sender.sendMessage("§7You already have an ongoing request to this player.");
            return true;
        }
        if (playerData.get(targetName).getTptoggle() == 1) {
            sender.sendMessage("§7You can't request this player since they've locked their tp requests.");
            return true;
        }
        addRequest((Player) sender, recipient, false, false);
        return true;
    }
}
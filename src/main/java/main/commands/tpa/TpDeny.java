package main.commands.tpa;

import main.managers.instances.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Initializer.*;
import static main.utils.Utils.getRequest;

public class TpDeny implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String msg = "§7You got no active teleport request.";
        TpaRequest request;
        final String name = sender.getName();
        if (args.length == 0) {
            request = getRequest(name);
        } else {
            String targetName = args[0];
            final Player p = Bukkit.getPlayer(targetName);
            if (p == null) {
                sender.sendMessage("§7Couldn't find anyone online named " + MAIN_COLOR + args[0] + ".");
                return true;
            } else
                targetName = p.getName();
            request = getRequest(name, targetName);
            msg = "§7You got no active teleport request from " + MAIN_COLOR + targetName + ".";
        }
        if (request == null) {
            sender.sendMessage(msg);
            return true;
        }
        final Player recipient = request.getSender();
        final String recipientName = recipient.getName();
        recipient.sendMessage(MAIN_COLOR + playerData.get(name).getFRank(name) + " §7denied your teleportation request.");
        sender.sendMessage("§7You have successfully deny " + MAIN_COLOR + playerData.get(recipientName).getFRank(recipientName) + "§7's request.");
        Bukkit.getScheduler().cancelTask(request.getRunnableid());
        requests.remove(request);
        return true;
    }
}
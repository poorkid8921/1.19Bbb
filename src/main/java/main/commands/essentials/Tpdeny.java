package main.commands.essentials;

import main.utils.Instances.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Initializer.requests;
import static main.utils.Utils.getRequest;
import static main.utils.Utils.translate;

@SuppressWarnings("deprecation")
public class Tpdeny implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String msg = "§7You got no active teleport request.";
        TpaRequest request;

        if (args.length == 0) {
            request = getRequest(sender.getName());
        } else {
            String n = args[0];
            Player p = Bukkit.getPlayer(n);
            if (p == null) {
                sender.sendMessage("§7Couldn't find anyone online named " + MAIN_COLOR + args[0] + ".");
                return true;
            } else
                n = p.getName();
            request = getRequest(sender.getName(), n);
            msg = "§7You got no active teleport request from " + MAIN_COLOR + n + ".";
        }

        if (request == null) {
            sender.sendMessage(msg);
            return true;
        }

        Player recipient = request.getSender();
        recipient.sendMessage(MAIN_COLOR + ((Player) sender).getDisplayName() + " §7denied your teleportation request.");
        sender.sendMessage("§7You have successfully deny " + MAIN_COLOR + translate(recipient.getDisplayName()) + "§7's request.");
        Bukkit.getScheduler().cancelTask(request.getRunnableid());
        requests.remove(request);
        return true;
    }
}
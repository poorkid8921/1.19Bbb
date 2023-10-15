package main.commands.tpa;

import main.utils.Instances.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Languages.MAIN_COLOR;
import static main.utils.RequestManager.getTPArequest;
import static main.utils.RequestManager.tpa;
import static main.utils.Utils.translate;

@SuppressWarnings("deprecation")
public class TpdenyCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player user)) return true;

        String msg = "§7You got no active teleport request.";
        TpaRequest request;

        if (args.length == 0) {
            request = getTPArequest(user.getName());
        } else {
            String n = args[0];
            Player p = Bukkit.getPlayer(n);
            if (p == null) {
                user.sendMessage("§7Couldn't find anyone online named " + MAIN_COLOR + args[0] + ".");
                return true;
            } else
                n = p.getName();
            request = getTPArequest(user.getName(), n);
            msg = "§7You got no active teleport request from " + MAIN_COLOR + n + ".";
        }

        if (request == null) {
            user.sendMessage(msg);
            return true;
        }

        Player recipient = request.getSender();
        recipient.sendMessage(MAIN_COLOR + user.getDisplayName() + " §7denied your teleportation request.");
        user.sendMessage("§7You have successfully deny " + MAIN_COLOR + translate(recipient.getDisplayName()) + "§7's request.");
        tpa.remove(request);
        return true;
    }
}
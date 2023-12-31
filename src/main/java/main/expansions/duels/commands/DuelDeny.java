package main.expansions.duels.commands;

import main.utils.Constants;
import main.utils.Instances.DuelHolder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Constants.MAIN_COLOR;
import static main.utils.Constants.duel;
import static main.utils.RequestManager.getDUELrequest;
import static main.utils.Utils.translate;

@SuppressWarnings("deprecation")
public class DuelDeny implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String msg = Constants.EXCEPTION_NO_DUEL_REQ;
        DuelHolder request;

        if (args.length == 0) {
            request = getDUELrequest(sender.getName());
        } else {
            String n = args[0];
            Player p = Bukkit.getPlayer(n);
            if (p == null) {
                sender.sendMessage(Constants.EXCEPTION_NO_ACTIVE_DUELREQ + MAIN_COLOR + args[0] + ".");
                return true;
            } else n = p.getName();
            request = getDUELrequest(sender.getName(), n);
            msg = "§7You got no active teleport request from " + MAIN_COLOR + n + ".";
        }

        if (request == null) {
            sender.sendMessage(msg);
            return true;
        }

        Player recipient = request.getSender();
        recipient.sendMessage(MAIN_COLOR + ((Player) sender).getDisplayName() + " §7denied your teleportation request.");
        sender.sendMessage("§7You have successfully deny " + MAIN_COLOR + translate(recipient.getDisplayName()) + "§7's request.");
        duel.remove(request);
        return true;
    }
}
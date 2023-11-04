package main.expansions.duels.commands;

import main.utils.Instances.DuelHolder;
import main.utils.Languages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Initializer.duel;
import static main.utils.Languages.MAIN_COLOR;
import static main.utils.RequestManager.getDUELrequest;
import static main.utils.Utils.translate;

@SuppressWarnings("deprecation")
public class DuelDeny implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player user)) return true;

        String msg = Languages.EXCEPTION_NO_DUEL_REQ;
        DuelHolder request;

        if (args.length == 0) {
            request = getDUELrequest(user.getName());
        } else {
            String n = args[0];
            Player p = Bukkit.getPlayer(n);
            if (p == null) {
                user.sendMessage(Languages.EXCEPTION_NO_ACTIVE_DUELREQ + MAIN_COLOR + args[0] + ".");
                return true;
            } else n = p.getName();
            request = getDUELrequest(user.getName(), n);
            msg = "§7You got no active teleport request from " + MAIN_COLOR + n + ".";
        }

        if (request == null) {
            user.sendMessage(msg);
            return true;
        }

        Player recipient = request.getSender();
        recipient.sendMessage(MAIN_COLOR + user.getDisplayName() + " §7denied your teleportation request.");
        user.sendMessage("§7You have successfully deny " + MAIN_COLOR + translate(recipient.getDisplayName()) + "§7's request.");
        duel.remove(request);
        return true;
    }
}
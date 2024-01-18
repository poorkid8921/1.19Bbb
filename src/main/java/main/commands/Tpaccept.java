package main.commands;

import main.utils.Constants;
import main.utils.Instances.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Constants.MAIN_COLOR;
import static main.utils.RequestManager.*;
import static main.utils.Utils.translate;

public class Tpaccept implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String msg = Constants.EXCEPTION_NO_ACTIVE_TPAREQ;
        TpaRequest request;
        String n = "";
        String un = sender.getName();

        if (args.length == 0) {
            request = getTPArequest(un);
        } else {
            n = args[0];
            Player p = Bukkit.getPlayer(n);
            if (p == null) {
                sender.sendMessage(msg);
                return true;
            } else
                n = p.getName();
            request = getTPArequest(un, n);
            msg = Constants.EXCEPTION_NO_ACTIVE_TPAREQ1 +
                    MAIN_COLOR + args[0] + ".";
        }

        if (request == null) {
            sender.sendMessage(msg);
            return true;
        } else if (un.equals(n)) {
            sender.sendMessage(Constants.EXCEPTION_PLAYER_TPSELF);
            return true;
        }

        Player tempuser;
        Player temprecipient;

        if (!request.isHere()) {
            tempuser = request.getSender();
            temprecipient = (Player) sender;
            tempuser.sendMessage("§7You have accepted " + MAIN_COLOR + translate(tempuser.getDisplayName()) + "§7's teleport request",
                    "§7Teleporting...");
            temprecipient.sendMessage(MAIN_COLOR + translate(temprecipient.getDisplayName()) + " §7has accepted your teleport request");
        } else {
            tempuser = (Player) sender;
            temprecipient = request.getSender();
            tempuser.sendMessage("§7You have accepted " + MAIN_COLOR + translate(tempuser.getDisplayName()) + "§7's teleport request",
                    "§7Teleporting...");
            temprecipient.sendMessage(MAIN_COLOR + translate(temprecipient.getDisplayName()) + " §7has accepted your teleport request");
        }

        Bukkit.getScheduler().cancelTask(bukkitTasks.get(request.getSenderF()));
        tempuser.teleport(temprecipient.getLocation());
        tpa.remove(request);
        return true;
    }
}
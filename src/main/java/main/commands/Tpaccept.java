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
        String msg = "§7You got no active teleport request.";
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
            msg = "§7You got no active teleport request from " + MAIN_COLOR + args[0] + ".";
        }

        if (request == null) {
            sender.sendMessage(msg);
            return true;
        } else if (un.equals(n)) {
            sender.sendMessage("§7You can't teleport to yourself.");
            return true;
        }

        Player tempuser;
        Player temprecipient;
        if (!request.isHere()) {
            tempuser = request.getSender();
            temprecipient = (Player) sender;
            tempuser.sendMessage("§7You have accepted " + MAIN_COLOR + translate(tempuser.getDisplayName()) + "'s §7teleport request",
                    "§7Teleporting...");
            temprecipient.sendMessage(MAIN_COLOR + translate(temprecipient.getDisplayName()) + " §7has accepted your teleport request");
        } else {
            tempuser = (Player) sender;
            temprecipient = request.getSender();
            tempuser.sendMessage("§7You have accepted " + MAIN_COLOR + translate(tempuser.getDisplayName()) + "'s §7teleport request",
                    "§7Teleporting...");
            temprecipient.sendMessage(MAIN_COLOR + translate(temprecipient.getDisplayName()) + " §7has accepted your teleport request");
        }

        Bukkit.getScheduler().cancelTask(request.getRunnableid());
        tempuser.teleport(temprecipient.getLocation());
        tpa.remove(request);
        return true;
    }
}
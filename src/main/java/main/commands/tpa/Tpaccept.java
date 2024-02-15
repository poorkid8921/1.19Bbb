package main.commands.tpa;

import main.utils.Initializer;
import main.utils.instances.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Utils.getRequest;
import static main.utils.Utils.translate;

@SuppressWarnings("deprecation")
public class Tpaccept implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player user)) return true;
        String msg = "§7You got no active teleport request.";
        TpaRequest request;
        String n = "";
        String un = user.getName();

        if (args.length == 0) {
            request = getRequest(un);
        } else {
            n = args[0];
            Player p = Bukkit.getPlayer(n);
            if (p == null) {
                user.sendMessage(msg);
                return true;
            } else
                n = p.getName();
            request = getRequest(un, n);
            msg = "§7You got no active teleport request from " +
                    MAIN_COLOR + args[0] + ".";
        }

        if (request == null) {
            user.sendMessage(msg);
            return true;
        } else if (un == n) {
            user.sendMessage("§7You can't teleport to yourself.");
            return true;
        }

        Player tempuser;
        Player temprecipient;
        if (request.isHere()) {
            tempuser = user;
            temprecipient = request.getSender();
            tempuser.sendMessage("§7You have accepted " + MAIN_COLOR + translate(temprecipient.getDisplayName()) + "§7's teleport request.",
                    "§7Teleporting...");
            if (request.getTpaAll())
                temprecipient.sendMessage(MAIN_COLOR + translate(tempuser.getDisplayName()) + " §7has accepted your teleport request.");
        } else {
            tempuser = request.getSender();
            temprecipient = user;
            temprecipient.sendMessage("§7You have accepted " + MAIN_COLOR + translate(tempuser.getDisplayName()) + "§7's teleport request.",
                    "§7Teleporting...");
            tempuser.sendMessage(MAIN_COLOR + translate(temprecipient.getDisplayName()) + " §7has accepted your teleport request");
        }

        Bukkit.getScheduler().cancelTask(request.getRunnableid());
        tempuser.teleport(temprecipient.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        Initializer.requests.remove(request);
        return true;
    }
}
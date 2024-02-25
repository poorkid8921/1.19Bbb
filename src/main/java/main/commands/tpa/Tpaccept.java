package main.commands.tpa;

import main.utils.Initializer;
import main.utils.instances.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Initializer.playerData;
import static main.utils.Utils.*;

@SuppressWarnings("deprecation")
public class Tpaccept implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String msg = "§7You got no active teleport request.";
        TpaRequest request;
        String n = "";
        String un = sender.getName();

        if (args.length == 0) {
            request = getRequest(un);
        } else {
            n = args[0];
            Player p = Bukkit.getPlayer(n);
            if (p == null) {
                sender.sendMessage(msg);
                return true;
            } else
                n = p.getName();
            request = getRequest(un, n);
            msg = "§7You got no active teleport request from " +
                    MAIN_COLOR + playerData.get(n).getFRank(n) + ".";
        }

        if (request == null) {
            sender.sendMessage(msg);
            return true;
        } else if (un == n) {
            sender.sendMessage("§7You can't teleport to yourself.");
            return true;
        }

        Player user;
        Player target;
        if (request.isHere()) {
            user = (Player) sender;
            target = request.getSender();
            String recName = target.getName();
            user.sendMessage("§7You have accepted " + MAIN_COLOR + playerData.get(recName).getFRank(recName) + "§7's teleport request.",
                    "§7Teleporting...");
            if (request.getTpaAll()) {
                String userName = user.getName();
                target.sendMessage(MAIN_COLOR + playerData.get(userName).getFRank(userName) + " §7has accepted your teleport request.");
            }
        } else {
            user = request.getSender();
            target = (Player) sender;
            String userName = user.getName();
            target.sendMessage("§7You have accepted " + MAIN_COLOR + playerData.get(userName).getFRank(userName) + "§7's teleport request.",
                    "§7Teleporting...");
            String recName = user.getName();
            user.sendMessage(MAIN_COLOR + playerData.get(recName).getFRank(recName) + " §7has accepted your teleport request");
        }

        Bukkit.getScheduler().cancelTask(request.getRunnableid());
        Location loc = target.getLocation();
        user.teleport(target.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        teleportEffect(loc.getWorld(), loc);
        Initializer.requests.remove(request);
        return true;
    }
}
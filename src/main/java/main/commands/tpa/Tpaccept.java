package main.commands.tpa;

import main.utils.Initializer;
import main.utils.Instances.TpaRequest;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import static main.utils.Initializer.*;
import static main.utils.Utils.getRequest;
import static main.utils.Utils.teleportEffect;

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
        } else if (un.equals(n)) {
            sender.sendMessage("§7You can't teleport to yourself.");
            return true;
        }

        Player user;
        Player target;
        String recName;
        String sn;
        if (request.isHere()) {
            user = (Player) sender;
            target = request.getSender();
            if (target == null) {
                requests.remove(request);
                sender.sendMessage(msg);
                return true;
            }
            recName = target.getName();
            sender.sendMessage("§7You have accepted " + MAIN_COLOR + playerData.get(recName).getFRank(recName) + "§7's teleport request.",
                    "§7Teleporting...");
            String userName = sender.getName();
            sn = user.getName();
            target.sendMessage(MAIN_COLOR + playerData.get(userName).getFRank(userName) + " §7has accepted your teleport request.");
        } else {
            user = request.getSender();
            target = (Player) sender;
            if (user == null) {
                requests.remove(request);
                sender.sendMessage(msg);
                return true;
            }
            String userName = user.getName();
            sender.sendMessage("§7You have accepted " + MAIN_COLOR + playerData.get(userName).getFRank(userName) + "§7's teleport request.",
                    "§7Teleporting...");
            recName = sender.getName();
            sn = user.getName();
            user.sendMessage(MAIN_COLOR + playerData.get(recName).getFRank(recName) + " §7has accepted your teleport request");
        }

        Bukkit.getScheduler().cancelTask(request.getRunnableid());
        Location loc = target.getLocation();
        user.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(r -> {
            if (inFFA.contains(target))
                inFFA.add(user);
            else if (inFlat.contains(recName))
                inFlat.add(sn);
            else if (atSpawn.contains(recName)) {
                ServerGamePacketListenerImpl connection = ((CraftPlayer) user).getHandle().connection;
                main.utils.holos.Utils.showForPlayerTickable(connection);
                Bukkit.getScheduler().runTaskLater(Initializer.p, () -> main.utils.npcs.Utils.showForPlayer(connection), 3L);
                atSpawn.add(sn);
            }
        });
        teleportEffect(loc.getWorld(), loc);
        Initializer.requests.remove(request);
        return true;
    }
}
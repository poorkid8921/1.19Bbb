package bab.bbb.tpa;

import bab.bbb.Bbb;
import bab.bbb.Events.misc.MiscEvents;
import bab.bbb.utils.Methods;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TpacceptCommand implements CommandExecutor {
    private final Bbb plugin;

    public TpacceptCommand(Bbb plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player))
            return true;

        Player user = (Player) sender;

        if (plugin.getRequest(user) == null) {
            Methods.errormsg(user, "you don't have any active request");
            return true;
        }

        TpaRequest request = plugin.getRequest(user);
        String targetName = request.getSender().getName();

        Player recipient = Bukkit.getPlayer(targetName);

        if (recipient == null) {
            Methods.errormsg(user, "player &e" + request.getSender().getDisplayName() + " isn't online anymore");
            return true;
        }

        if (MiscEvents.antilog.contains(user.getName())) {
            Methods.errormsg(user, "can't teleport whilst being combat tagged");
            return true;
        }

        if (request.getType() == Type.TPA) {
            Methods.tpmsg(((Player) sender).getPlayer(), recipient, 10);
            Methods.tpmsg(recipient, null, 7);

            new BukkitRunnable() {
                @Override
                public void run() {
                    recipient.getWorld().strikeLightning(recipient.getLocation());
                    if (recipient.getVehicle() != null) {
                        recipient.getVehicle().teleport(user);
                        recipient.getVehicle().eject();
                    }

                    for (Player players : Bukkit.getOnlinePlayers())
                        players.hidePlayer(plugin, recipient);

                    recipient.teleport(user);
                    for (Player players : Bukkit.getOnlinePlayers())
                        players.showPlayer(plugin, recipient);
                    recipient.getWorld().strikeLightning(recipient.getLocation());
                }
            }.runTaskLater(plugin, 100);
        } else if (request.getType() == Type.TPAHERE) {
            Methods.tpmsg(recipient, ((Player) sender).getPlayer(), 8);
            Methods.tpmsg(((Player) sender).getPlayer(), recipient, 10);

            new BukkitRunnable() {
                @Override
                public void run() {
                    user.getWorld().strikeLightning(user.getLocation());
                    if (user.getVehicle() != null) {
                        user.getVehicle().teleport(recipient);
                        user.getVehicle().eject();
                    }

                    for (Player players : Bukkit.getOnlinePlayers())
                        players.hidePlayer(plugin, user);

                    user.teleport(recipient);
                    for (Player players : Bukkit.getOnlinePlayers())
                        players.showPlayer(plugin, user);
                    user.getWorld().strikeLightning(user.getLocation());
                }
            }.runTaskLater(plugin, 100);
        }

        plugin.removeRequest(user);
        return true;
    }
}
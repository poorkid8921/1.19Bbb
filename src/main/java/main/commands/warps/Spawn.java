package main.commands.warps;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.utils.Initializer;
import main.utils.instances.CustomPlayerDataHolder;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

import static main.utils.Initializer.playerData;

public class Spawn implements CommandExecutor, TabExecutor {
    private final ObjectArrayList<String> spawnQueue = ObjectArrayList.of();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        String name = sender.getName();
        CustomPlayerDataHolder D0 = playerData.get(name);
        if (D0.getLastTagged() == 0L) {
            p.teleport(Initializer.spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
            ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
            main.utils.holos.Utils.showForPlayerTickable(connection);
            Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
                main.utils.npcs.Utils.showForPlayer(connection);
            }, 3L);
            return true;
        }
        if ((System.currentTimeMillis() - D0.getLastTagged()) > 30000L) {
            p.teleport(Initializer.spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
            ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
            main.utils.holos.Utils.showForPlayerTickable(connection);
            Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
                main.utils.npcs.Utils.showForPlayer(connection);
            }, 3L);
        } else {
            spawnQueue.add(name);
            new BukkitRunnable() {
                final Location oldLoc = p.getLocation();
                int sec = 4;

                @Override
                public void run() {
                    boolean online = p.isOnline();
                    sec--;
                    if (sec == 0 || !online) {
                        spawnQueue.remove(name);
                        if (online) {
                            p.teleport(Initializer.spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
                            ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
                            main.utils.holos.Utils.showForPlayerTickable(connection);
                            Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
                                main.utils.npcs.Utils.showForPlayer(connection);
                            }, 3L);
                        }
                        this.cancel();
                        return;
                    }
                    if (p.getWorld() == oldLoc.getWorld() && oldLoc.distance(p.getLocation()) > 0.5D) {
                        p.sendActionBar("§aTeleport cancelled. You moved!");
                        this.cancel();
                        return;
                    } else if (D0.isTagged()) {
                        this.cancel();
                        return;
                    }
                    p.sendActionBar("§aTeleporting to spawn in " + (sec == 1 ? "a second!" : sec + " seconds!"));
                }
            }.runTaskTimer(Initializer.p, 0L, 20L);
        }
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
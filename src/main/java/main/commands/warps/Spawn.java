package main.commands.warps;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.utils.Initializer;
import main.utils.instances.CustomPlayerDataHolder;
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
import static main.utils.Utils.showCosmetics;

public class Spawn implements CommandExecutor, TabExecutor {
    private final ObjectArrayList<String> spawnQueue = ObjectArrayList.of();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player = (Player) sender;
        final String name = sender.getName();
        final CustomPlayerDataHolder D0 = playerData.get(name);
        if (D0.getLastTagged() == 0L) {
            player.teleport(Initializer.spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
            showCosmetics(((CraftPlayer) player).getHandle().connection);
            return true;
        }
        if ((System.currentTimeMillis() - D0.getLastTagged()) > 30000L) {
            player.teleport(Initializer.spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
            showCosmetics(((CraftPlayer) player).getHandle().connection);
        } else {
            spawnQueue.add(name);
            new BukkitRunnable() {
                final Location oldLoc = player.getLocation();
                int sec = 4;

                @Override
                public void run() {
                    final boolean online = player.isOnline();
                    sec--;
                    if (sec == 0 || !online) {
                        spawnQueue.remove(name);
                        if (online) {
                            player.teleport(Initializer.spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
                            showCosmetics(((CraftPlayer) player).getHandle().connection);
                        }
                        this.cancel();
                        return;
                    }
                    if (player.getWorld() == oldLoc.getWorld() && oldLoc.distance(player.getLocation()) > 0.5D) {
                        player.sendActionBar("§aTeleport cancelled. You moved!");
                        this.cancel();
                        return;
                    } else if (D0.isTagged()) {
                        this.cancel();
                        return;
                    }
                    player.sendActionBar("§aTeleporting to spawn in " + (sec == 1 ? "a second!" : sec + " seconds!"));
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
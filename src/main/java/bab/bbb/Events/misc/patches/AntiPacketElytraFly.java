package bab.bbb.Events.misc.patches;

import bab.bbb.Bbb;
import bab.bbb.utils.Utils;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

import static bab.bbb.utils.Utils.combattag;
import static bab.bbb.utils.Utils.translate;

public class AntiPacketElytraFly implements Listener {
    private final Bbb plugin = Bbb.getInstance();
    private final HashMap<UUID, Integer> levels = new HashMap<>();

    @EventHandler
    private void onElytraOpen(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        Location from = player.getLocation();
        UUID playerUniqueID = player.getUniqueId();

        Integer level = levels.get(playerUniqueID);
        if (level != null) {
            if (level > 25) {
                event.setCancelled(true);
                PaperLib.teleportAsync(player, from).thenAccept(reason -> player.kickPlayer(translate("&7Disconnected")));
            } else {
                levels.merge(playerUniqueID, 1, Integer::sum);
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> levels.put(playerUniqueID, levels.get(playerUniqueID) - 1), 200L);
            }
        } else {
            levels.put(playerUniqueID, 1);
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> levels.put(playerUniqueID, levels.get(playerUniqueID) - 1), 200L);
        }
    }
}
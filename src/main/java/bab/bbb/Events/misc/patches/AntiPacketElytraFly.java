package bab.bbb.Events.misc.patches;

import bab.bbb.Bbb;
import bab.bbb.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

import java.util.HashMap;
import java.util.UUID;

import static bab.bbb.utils.Utils.combattag;

public class AntiPacketElytraFly implements Listener {
    private final Bbb plugin = Bbb.getInstance();
    private final HashMap<UUID, Integer> levels = new HashMap<>();

    @EventHandler
    private void onElytraOpen(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = ((Player) event.getEntity()).getPlayer();
        if (player == null)
            return;
        Location from = player.getLocation();

        if (combattag.contains(player.getName()))
        {
            Utils.elytraflag(player, 0, 2, 0, null);
            event.setCancelled(true);
            return;
        }

        if (Bbb.getTPSofLastSecond() <= plugin.config.getInt("take-anti-lag-measures-if-tps")) {
            event.setCancelled(true);
            Utils.elytraflag(player, 1, 1, 0, null);
            return;
        }

        UUID playerUniqueID = player.getUniqueId();

        Integer level = levels.get(playerUniqueID);
        if (level != null) {
            if (level > 25) {
                event.setCancelled(true);
                Utils.elytraflag(player, 2, 2, 1, from);
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
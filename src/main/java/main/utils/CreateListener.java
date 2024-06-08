package main.utils;

import main.utils.instances.AbstractRegionHolder;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static main.Economy.d;
import static main.utils.Initializer.EXCEPTION_PVP;
import static main.utils.Initializer.regions;

public class CreateListener implements Listener {
    @EventHandler
    private void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;
        final Entity attacker = e.getDamager();
        final Entity entity = e.getEntity();
        if (entity.getType() != EntityType.PLAYER)
            return;
        Player player = (Player) entity;
        Location location = player.getLocation();
        if (location.getWorld() != d) return;
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        for (final AbstractRegionHolder region : regions) {
            if (region.testY(x, y, z)) {
                attacker.sendMessage(EXCEPTION_PVP);
                e.setCancelled(true);
                return;
            }
        }
    }
}
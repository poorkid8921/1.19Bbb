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
        Entity attacker = e.getDamager();
        Entity ent = e.getEntity();
        if (ent.getType() != EntityType.PLAYER)
            return;
        Player p = (Player) ent;
        if (p.getWorld() != d) return;
        Location loc = p.getLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for (AbstractRegionHolder k : regions) {
            if (k.testY(x, y, z)) {
                attacker.sendMessage(EXCEPTION_PVP);
                e.setCancelled(true);
                return;
            }
        }
    }
}
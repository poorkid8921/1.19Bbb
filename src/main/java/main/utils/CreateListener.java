package main.utils;

import main.utils.instances.AbstractRegionHolder;
import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static main.Economy.d;
import static main.utils.Initializer.*;

public class CreateListener implements Listener {
    @EventHandler
    private void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled())
            return;
        Entity attacker = e.getDamager();
        EntityType entType = attacker.getType();
        boolean playerAttacker = entType == EntityType.PLAYER;
        Entity ent = e.getEntity();
        if (ent.getType() != EntityType.PLAYER ||
                (!playerAttacker && entType != EntityType.SPLASH_POTION) && entType != EntityType.ARROW)
            return;
        Player p = (Player) ent;
        Location loc = p.getLocation();
        if (loc.getWorld() != d)
            return;
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        if (playerAttacker) {
            for (AbstractRegionHolder k : regions) {
                if (k.test(x, z)) {
                    attacker.sendMessage("§7You can't combat here!");
                    e.setCancelled(true);
                    return;
                }
            }
            CustomPlayerDataHolder D0 = playerData.get(p.getName());
            if (D0.isTagged())
                D0.setTagTime(p);
            else
                D0.setupCombatRunnable(p);
            Player damagePlayer = (Player) attacker;
            CustomPlayerDataHolder D1 = playerData.get(attacker.getName());
            if (D1.isTagged())
                D1.setTagTime(damagePlayer);
            else
                D1.setupCombatRunnable(damagePlayer);
        } else {
            for (AbstractRegionHolder k : regions) {
                if (k.test(x, z)) {
                    e.setCancelled(true);
                    return;
                }
            }
            CustomPlayerDataHolder D0 = playerData.get(p.getName());
            if (D0.isTagged())
                D0.setTagTime(p);
            else
                D0.setupCombatRunnable(p);
        }
    }
}

package bab.bbb.Events.Dupes;

import bab.bbb.Bbb;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.Random;

import static java.lang.Math.random;

public class FrameDupe implements Listener {
    @EventHandler
    public void framedupe(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof ItemFrame) {
            int rng = (int) Math.round(random() * 100);
            if (rng < 50) {
                Random ran = new Random();
                int b = ran.nextInt(5);
                for (int i = 0; i < b; i++)
                    e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), ((ItemFrame) e.getEntity()).getItem());
            }
        }
    }
}

package bab.bbb.Events.Dupes;

import bab.bbb.Bbb;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.Bukkit;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.Random;

import static bab.bbb.utils.Utils.getHead;
import static bab.bbb.utils.Utils.plugin;
import static java.lang.Math.random;

public class FrameDupe implements Listener {
    @EventHandler
    public void framedupe(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof ItemFrame) {
            if (FastMath.random() < 0.3) {
                Random ran = new Random();
                int b = ran.nextInt(3);
                for (int i = 0; i < b; i++)
                    Bukkit.getScheduler().runTask(plugin, () -> e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), ((ItemFrame) e.getEntity()).getItem()));
            }
        }
    }
}
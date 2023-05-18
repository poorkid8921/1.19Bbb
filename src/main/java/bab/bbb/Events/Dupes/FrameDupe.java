package bab.bbb.Events.Dupes;

import bab.bbb.Bbb;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Random;

import static java.lang.Math.random;

public class FrameDupe implements Listener {
    public void framedupe(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof ItemFrame) {
            int rng = (int) Math.round(random() * 100);
            if (rng < Bbb.getInstance().frameduperng) {
                Random ran = new Random();
                int b = ran.nextInt(5);
                for (int i = 0; i < b; i++)
                    e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), ((ItemFrame) e.getEntity()).getItem());
            }
        }
    }
}

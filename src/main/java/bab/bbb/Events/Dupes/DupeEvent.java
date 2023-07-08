package bab.bbb.Events.Dupes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class DupeEvent implements Listener {
    @EventHandler
    public void onDupe(PlayerDupeEvent event) {
        ArrayList<Entity> items = new ArrayList<>();
        for (Entity entity : event.getPlayer().getLocation().getNearbyEntities(16, 16, 16)) {
            if (entity.getType() == EntityType.DROPPED_ITEM)
                items.add(entity);
        }

        while (items.size() > 50) {
            items.get(items.size() - 1).remove();
            items.remove(items.size() -1);
        }
    }
}
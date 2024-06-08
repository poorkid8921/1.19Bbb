package main.utils.modules.kits.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import static main.Practice.publicChecker;

public class PublicKitsCloseEvent implements Listener {
    @EventHandler
    public void onGUIClose(InventoryCloseEvent event) {
        String key = event.getPlayer().getUniqueId().toString();
        if (publicChecker.containsKey(key)) {
            publicChecker.remove(key);
        }
    }
}

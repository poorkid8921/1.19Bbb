package main.utils.modules.kits.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import static main.Practice.roomChecker;

public class KitRoomCloseEvent implements Listener {
    @EventHandler
    public void onGUIClose(InventoryCloseEvent event) {
        String key = event.getPlayer().getUniqueId().toString();
        if (roomChecker.containsKey(key)) {
            roomChecker.remove(key);
        }
    }
}

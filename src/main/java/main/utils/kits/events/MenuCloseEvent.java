package main.utils.kits.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Collections;

import static main.Practice.menuChecker;

public class MenuCloseEvent implements Listener {
    @EventHandler
    public void onGUIClose(InventoryCloseEvent event) {
        if (menuChecker.contains(event.getPlayer().getUniqueId().toString())) {
            menuChecker.removeAll(Collections.singleton(event.getPlayer().getUniqueId().toString()));
        }
    }
}

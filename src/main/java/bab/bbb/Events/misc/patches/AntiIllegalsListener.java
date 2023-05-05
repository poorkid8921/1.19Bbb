package bab.bbb.Events.misc.patches;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import static bab.bbb.Bbb.*;

public class AntiIllegalsListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if (event.getInventory().equals(event.getPlayer().getEnderChest()) || event.getPlayer().isOp())
            return;

        checkInventory(event.getInventory(), event.getPlayer().getLocation(), true);
        checkArmorContents(event.getPlayer().getInventory(), event.getPlayer().getLocation(), true);
    }
}

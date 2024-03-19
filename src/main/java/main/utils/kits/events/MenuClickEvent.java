package main.utils.kits.events;

import main.utils.kits.KitClaimer;
import main.utils.kits.inventories.KitEditor;
import main.utils.kits.inventories.KitRoom;
import main.utils.kits.inventories.PublicKits;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;

import static main.Practice.menuChecker;

public class MenuClickEvent implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (menuChecker.contains(event.getWhoClicked().getUniqueId().toString()) && !(event.getClickedInventory() instanceof PlayerInventory)) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            if (10 <= event.getSlot() && event.getSlot() <= 12) {
                String kit = "Kit " + (event.getSlot() - 9);
                if (event.isLeftClick()) {
                    KitClaimer.claim((Player) event.getWhoClicked(), kit, false);
                    player.closeInventory();
                } else
                    new KitEditor(player, kit);
            } else {
                if (event.getSlot() == 38) {
                    new KitRoom(player);
                } else if (event.getSlot() == 43) {
                    new PublicKits(player, 1);
                }
            }
        }
    }
}

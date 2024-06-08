package main.utils.modules.kits.events;

import main.utils.Initializer;
import main.utils.modules.kits.KitClaimer;
import main.utils.modules.kits.inventories.KitMenu;
import main.utils.modules.kits.inventories.PublicKits;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static main.Practice.publicChecker;

public class PublicKitsClickEvent implements Listener {
    NamespacedKey itemKey = new NamespacedKey(Initializer.p, "key");

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String key = player.getUniqueId().toString();
        if (publicChecker.containsKey(key) && !(event.getClickedInventory() instanceof PlayerInventory)) {
            event.setCancelled(true);
            if (10 <= event.getSlot() && event.getSlot() <= 43 && event.getCurrentItem() != null && event.getCurrentItem().getType().toString().contains("CHEST") && event.isLeftClick()) {
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();
                if (container.has(itemKey, PersistentDataType.STRING)) {
                    String foundValue = container.get(itemKey, PersistentDataType.STRING);
                    KitClaimer.claimPublicKit(player, foundValue);
                    player.closeInventory();
                }
            }

            int newPage;
            if (event.getSlot() == 48 && event.getCurrentItem().getType().toString().contains("PLAYER_HEAD")) {
                newPage = publicChecker.get(key) - 1;
                new PublicKits(player, newPage);
            } else if (event.getSlot() == 50 && event.getCurrentItem().getType().toString().contains("PLAYER_HEAD")) {
                newPage = publicChecker.get(key) + 1;
                new PublicKits(player, newPage);
            } else if (event.getSlot() == 49) {
                new KitMenu(player);
            }
        }
    }
}

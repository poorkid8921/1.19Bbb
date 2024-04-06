package main.utils.kits.events;

import main.utils.kits.ItemCreator;
import main.utils.kits.inventories.KitMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;

import static main.Practice.kitRoomMap;
import static main.Practice.roomChecker;

public class KitRoomClickEvent implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        String key = event.getWhoClicked().getUniqueId().toString();
        if (roomChecker.containsKey(key) && !(event.getClickedInventory() instanceof PlayerInventory)) {
            if (event.getSlot() >= 45)
                event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item != null && item.getType().toString().contains("SIGN") && !event.getWhoClicked().isOp()) {
                event.setCancelled(true);
            }
            int i;
            if (47 <= event.getSlot() && event.getSlot() <= 51) {
                i = roomChecker.get(key);
                ItemStack cleanedItem = ItemCreator.disEnchant(event.getInventory().getItem(i + 46));
                event.getInventory().setItem(i + 46, cleanedItem);
                int newPage = event.getSlot() - 46;
                ItemStack enchantedItem = ItemCreator.enchant(item);
                event.getInventory().setItem(event.getSlot(), enchantedItem);

                for (i = 0; i <= 44; ++i) {
                    event.getInventory().setItem(i, ((ItemStack[]) kitRoomMap.get(newPage))[i]);
                }

                roomChecker.put(key, newPage);
            } else if (event.getSlot() == 53) {
                for (i = 0; i <= 44; ++i) {
                    event.getInventory().setItem(i, ((ItemStack[]) kitRoomMap.get(roomChecker.get(key)))[i]);
                }
            } else if (event.getSlot() == 45) {
                if (event.getWhoClicked().isOp()) {
                    ItemStack[] items = Arrays.copyOfRange(event.getInventory().getContents(), 0, 45);
                    kitRoomMap.put(roomChecker.get(key), items);
                    event.getWhoClicked().sendMessage(ChatColor.AQUA + "Page " + roomChecker.get(key) + "§d saved!");
                } else {
                    new KitMenu((Player) event.getWhoClicked());
                }
            }

        }
    }
}

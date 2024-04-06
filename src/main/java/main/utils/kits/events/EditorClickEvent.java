package main.utils.kits.events;

import main.Practice;
import main.utils.kits.ItemCreator;
import main.utils.kits.SaveEditor;
import main.utils.kits.inventories.KitMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static main.Practice.editorChecker;

public class EditorClickEvent implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof PlayerInventory)) {
            for (int i = 1; i <= 3; ++i) {
                if (editorChecker.contains(event.getWhoClicked().getUniqueId() + "-kit" + i)) {
                    Player player = (Player) event.getWhoClicked();
                    String key = player.getUniqueId() + "-kit" + i;
                    if (event.getSlot() >= 41) {
                        event.setCancelled(true);
                    }

                    if (event.getSlot() == 45) {
                        new KitMenu(player);
                    }

                    int j;
                    if (event.getSlot() == 47) {
                        for (j = 0; j <= 40; ++j) {
                            event.getInventory().setItem(j, player.getInventory().getItem(j));
                        }
                        return;
                    } else {
                        if (event.getSlot() != 48 && event.getSlot() != 49) {
                            if (event.getSlot() == 50) {
                                for (j = 0; j <= 40; ++j) {
                                    event.getInventory().setItem(j, (ItemStack) null);
                                }
                            } else if (event.getSlot() != 52 && event.getSlot() == 53) {
                                SaveEditor.save(player, i, false);
                                if (!Practice.kitMap.get(key).containsKey("public")) {
                                    if (Practice.kitMap.get(key).containsKey("items")) {
                                        player.sendMessage("§dPublished kit! Other players can now see it by clicking the §bglobe §din §b/kit§d.");
                                        Practice.kitMap.get(key).put("public", "to make kit private, delete this entire line (incliding \"public\")");
                                        event.getInventory().setItem(53, ItemCreator.getItem(ChatColor.GREEN + "" + ChatColor.BOLD + "MAKE PRIVATE", Material.FIREWORK_STAR));
                                    } else {
                                        player.sendMessage("§cCannot publish an empty kit.");
                                    }
                                } else {
                                    Practice.kitMap.get(key).remove("public");
                                    player.sendMessage("§dKit made private.");
                                    event.getInventory().setItem(53, ItemCreator.getHead(ChatColor.GREEN + "" + ChatColor.BOLD + "MAKE PUBLIC", "Kevos"));
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
}

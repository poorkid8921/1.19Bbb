package org.yuri.aestheticnetwork.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yuri.aestheticnetwork.utils.Initializer;
import org.yuri.aestheticnetwork.utils.InventoryInstanceShop;
import org.yuri.aestheticnetwork.utils.duels.DuelManager;

import static org.yuri.aestheticnetwork.utils.duels.DuelManager.updateDuels;

public class DuelInventory extends InventoryInstanceShop {
    public DuelInventory(Player player) {
        super(player);
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, "ᴅᴜᴇʟs");

        for (int i = 18; i < inv.getSize(); i++) {
            ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName(" ");
            glass.setItemMeta(meta);
            inv.setItem(i, glass);
        }

        for (int i = 0; i < 9; i++) {
            ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName(" ");
            glass.setItemMeta(meta);
            inv.setItem(i, glass);
        }

        updateDuels(inv);
        return inv;
    }

    @Override
    public void whenClicked(ItemStack item,
                            int slot) {
        ItemMeta meta = item.getItemMeta();

        if (!meta.hasLore())
            return;

        if (slot == 9) {
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
            item.setItemMeta(meta);
            if (!DuelManager.alreadyInMatchmaking(player.getName(), "field")) {
                DuelManager.startMatchmaking(player, "field");
                Initializer.inMatchmaking.put(player.getName(), "field");
            }
        }
    }
}
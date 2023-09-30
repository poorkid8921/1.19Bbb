package main.expansions.kits;


import main.expansions.kits.Utils;
import main.utils.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.List;

public class KitRoomInventory {
    Inventory inventory;

    public KitRoomInventory(Player player) {
        inventory = Bukkit.createInventory(player, 54, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Virtual Kit Room");
        inventory.setContents(Utils.kitRoomMap.get(1));
        if (player.hasPermission("personalkits.edit")) {
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Perm to see this button: personalkits.edit");
            ItemStack saveButton = ItemCreator.getItem(ChatColor.GREEN + "" + ChatColor.BOLD + "SAVE CHANGES", Material.GREEN_TERRACOTTA, lore);
            inventory.setItem(45, saveButton);
        } else {
            ItemStack exit = ItemCreator.getItem("§cEXIT", Material.OAK_DOOR, null);
            inventory.setItem(45, exit);
        }
        ItemStack item = ItemCreator.getItem(ChatColor.GREEN + "" + ChatColor.BOLD + "REFILL", Material.STRUCTURE_VOID, null);
        inventory.setItem(53, item);
        ItemStack[] buttons = {ItemCreator.getItem(ChatColor.GREEN + "Armory", Material.NETHERITE_SWORD, null), ItemCreator.getItem(ChatColor.GREEN + "Potions", Material.SPLASH_POTION, null), ItemCreator.getItem(ChatColor.GREEN + "Consumables", Material.ENDER_PEARL, null), ItemCreator.getItem(ChatColor.GREEN + "Arrows", Material.TIPPED_ARROW, null), ItemCreator.getItem(ChatColor.GREEN + "Explosives", Material.RESPAWN_ANCHOR, null)};
        int i = 47;
        for (ItemStack button : buttons) {
            ItemMeta meta = button.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            button.setItemMeta(meta);
            inventory.setItem(i, button);
            ++i;
        }
        item = inventory.getItem(47);
        ItemMeta meta2 = item.getItemMeta();
        meta2.addEnchant(Enchantment.DURABILITY, 1, true);
        item.setItemMeta(meta2);
        item = inventory.getItem(50);
        PotionMeta potMeta = (PotionMeta) item.getItemMeta();
        potMeta.setColor(Color.WHITE);
        item.setItemMeta(potMeta);
        item = ItemCreator.getItem(" ", Material.BLACK_STAINED_GLASS_PANE, null);
        for (int j = 45; j <= 53; ++j) {
            if (inventory.getItem(j) == null) {
                inventory.setItem(j, item);
            }
        }
        player.openInventory(inventory);
        Utils.checker.put(player.getName(), 1);
    }
}
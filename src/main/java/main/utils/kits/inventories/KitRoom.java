package main.utils.kits.inventories;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.utils.kits.ItemCreator;
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

import java.util.List;

import static main.Practice.kitRoomMap;
import static main.Practice.roomChecker;

public class KitRoom {
    Inventory inventory;

    public KitRoom(Player player) {
        Inventory kitRoom = Bukkit.createInventory(player, 54, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Virtual Kit Room");
        kitRoom.setContents(kitRoomMap.get(1));
        ItemStack item;
        if (player.isOp()) {
            kitRoom.setItem(45, ItemCreator.getItem(ChatColor.GREEN + "" + ChatColor.BOLD + "SAVE CHANGES", Material.GREEN_TERRACOTTA));
        } else {
            item = ItemCreator.getItem("§cEXIT", Material.OAK_DOOR);
            kitRoom.setItem(45, item);
        }

        item = ItemCreator.getItem(ChatColor.GREEN + "" + ChatColor.BOLD + "REFILL", Material.STRUCTURE_VOID);
        kitRoom.setItem(53, item);
        ItemStack[] buttons = new ItemStack[]{ItemCreator.getItem(ChatColor.GREEN + "Armory", Material.NETHERITE_SWORD), ItemCreator.getItem(ChatColor.GREEN + "Potions", Material.SPLASH_POTION), ItemCreator.getItem(ChatColor.GREEN + "Consumables", Material.ENDER_PEARL), ItemCreator.getItem(ChatColor.GREEN + "Arrows", Material.TIPPED_ARROW), ItemCreator.getItem(ChatColor.GREEN + "Explosives", Material.RESPAWN_ANCHOR)};
        int i = 47;
        int j;
        for (j = 0; j < buttons.length; ++j) {
            ItemStack button = buttons[j];
            ItemMeta meta = button.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            button.setItemMeta(meta);
            kitRoom.setItem(i, button);
            ++i;
        }

        item = kitRoom.getItem(47);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        item.setItemMeta(meta);
        item = kitRoom.getItem(50);
        PotionMeta potMeta = (PotionMeta) item.getItemMeta();
        potMeta.setColor(Color.WHITE);
        item.setItemMeta(potMeta);
        item = ItemCreator.getItem(" ", Material.BLACK_STAINED_GLASS_PANE);
        for (j = 45; j <= 53; ++j) {
            if (kitRoom.getItem(j) == null) {
                kitRoom.setItem(j, item);
            }
        }

        this.inventory = kitRoom;
        player.openInventory(this.getInventory());
        roomChecker.put(player.getUniqueId().toString(), 1);
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}

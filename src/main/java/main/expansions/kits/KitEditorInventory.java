package main.expansions.kits;

import main.utils.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitEditorInventory {
    Inventory inventory;

    public KitEditorInventory(Player player, int kit) {
        String name = player.getName();
        inventory = Bukkit.createInventory(player, 54, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + name);
        try {
            inventory.setContents((ItemStack[]) Utils.kitMap.get(name).get("items"));
        } catch (Exception ignored) {
        }
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Order does not matter!");
        ItemStack item = ItemCreator.getItem(ChatColor.GRAY + "" + ChatColor.BOLD + "← ARMOR + OFFHAND", Material.BLACK_STAINED_GLASS_PANE, lore);
        inventory.setItem(41, item);
        inventory.setItem(42, item);
        inventory.setItem(43, item);
        inventory.setItem(44, item);
        lore.clear();
        item = ItemCreator.getItem("§cEXIT", Material.OAK_DOOR, null);
        inventory.setItem(45, item);
        item = ItemCreator.getItem(" ", Material.BLACK_STAINED_GLASS_PANE, null);
        inventory.setItem(46, item);
        item = ItemCreator.getItem(ChatColor.GREEN + "" + ChatColor.BOLD + "IMPORT CURRENT INVENTORY", Material.CHEST, null);
        inventory.setItem(47, item);
        item = ItemCreator.getItem(ChatColor.GRAY + "" + ChatColor.BOLD + "COPY (COMING SOON)", Material.CYAN_TERRACOTTA, null);
        inventory.setItem(48, item);
        item = ItemCreator.getItem(ChatColor.GRAY + "" + ChatColor.BOLD + "PASTE (COMING SOON)", Material.CYAN_TERRACOTTA, null);
        inventory.setItem(49, item);
        item = ItemCreator.getItem(ChatColor.GREEN + "" + ChatColor.BOLD + "CLEAR", Material.STRUCTURE_VOID, null);
        inventory.setItem(50, item);
        item = ItemCreator.getItem(" ", Material.BLACK_STAINED_GLASS_PANE, null);
        inventory.setItem(51, item);
        item = ItemCreator.getItem(ChatColor.GRAY + "" + ChatColor.BOLD + "SHARE (COMING SOON)", Material.CYAN_TERRACOTTA, null);
        inventory.setItem(52, item);
        inventory.setItem(53, Utils.kitMap.get(name + "-" + kit).containsKey("public") ?
                ItemCreator.getItem(ChatColor.GREEN + "" + ChatColor.BOLD + "MAKE PRIVATE", Material.FIREWORK_STAR, null)
                : ItemCreator.getHead(ChatColor.GREEN + "" + ChatColor.BOLD + "MAKE PUBLIC", "Kevos", null));
        player.openInventory(inventory);
        Utils.editorChecker.put(name, kit);
    }
}

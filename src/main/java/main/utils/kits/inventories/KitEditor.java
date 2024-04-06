package main.utils.kits.inventories;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import main.Practice;
import main.utils.kits.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static main.Practice.editorChecker;

@Getter
public class KitEditor {
    Inventory inventory;

    public KitEditor(Player player, String kit) {
        String key = player.getUniqueId() + "-" + kit.replaceAll("\\s", "").toLowerCase();
        Inventory kitEditor = Bukkit.createInventory(player, 54, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + kit);
        try {
            kitEditor.setContents((ItemStack[]) Practice.kitMap.get(key).get("items"));
        } catch (Exception ignored) {
        }
        List<String> lore = ObjectArrayList.of();
        lore.add(ChatColor.GRAY + "Order does not matter!");
        ItemStack item = ItemCreator.getItem(ChatColor.GRAY + "" + ChatColor.BOLD + "← ARMOR + OFFHAND", Material.BLACK_STAINED_GLASS_PANE, lore);
        kitEditor.setItem(41, item);
        kitEditor.setItem(42, item);
        kitEditor.setItem(43, item);
        kitEditor.setItem(44, item);
        lore.clear();
        item = ItemCreator.getItem("§cEXIT", Material.OAK_DOOR);
        kitEditor.setItem(45, item);
        item = ItemCreator.getItem(" ", Material.BLACK_STAINED_GLASS_PANE);
        kitEditor.setItem(46, item);
        kitEditor.setItem(51, item);
        kitEditor.setItem(47, item);
        kitEditor.setItem(48, item);
        kitEditor.setItem(49, item);
        kitEditor.setItem(50, item);
        kitEditor.setItem(52, item);
        item = ItemCreator.getItem(ChatColor.GREEN + "" + ChatColor.BOLD + "IMPORT CURRENT INVENTORY", Material.CHEST);
        kitEditor.setItem(51, item);
        item = ItemCreator.getItem(ChatColor.GREEN + "" + ChatColor.BOLD + "CLEAR", Material.STRUCTURE_VOID);
        kitEditor.setItem(52, item);
        if (!Practice.kitMap.get(key).containsKey("public")) {
            item = ItemCreator.getHead(ChatColor.GREEN + "" + ChatColor.BOLD + "MAKE PUBLIC", "Marlowww");
        } else
            item = ItemCreator.getItem(ChatColor.GREEN + "" + ChatColor.BOLD + "MAKE PRIVATE", Material.FIREWORK_STAR);
        kitEditor.setItem(53, item);
        this.inventory = kitEditor;
        player.openInventory(this.getInventory());
        editorChecker.add(key);
    }
}

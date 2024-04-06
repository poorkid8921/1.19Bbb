package main.utils.kits.inventories;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import main.utils.kits.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static main.Practice.menuChecker;

@Getter
public class KitMenu {
    Inventory inventory;

    public KitMenu(Player player) {
        Inventory kitMenu = Bukkit.createInventory(player, 54, ChatColor.BLUE + "" + ChatColor.BOLD + player.getName() + "'s Kits");
        List<String> lore = ObjectArrayList.of();

        int i;
        for (i = 1; i <= 3; ++i) {
            lore.add("§dTo edit:");
            lore.add("§d• Right click this chest");
            lore.add(ChatColor.GRAY + "To load:");
            lore.add(ChatColor.GRAY + "• /k" + i);
            lore.add(ChatColor.GRAY + "• /kit" + i);
            ItemStack item = ItemCreator.getItem("§b§lKit " + i, Material.CHEST, lore);
            kitMenu.setItem(i + 9, item);
            lore.clear();
        }

        lore.add(ChatColor.GRAY + "COMING SOON");
        for (i = 1; i <= 3; ++i) {
            ItemStack item = ItemCreator.getItem(ChatColor.GRAY + "" + ChatColor.BOLD + "Ender Chest " + i, Material.ENDER_CHEST, lore);
            kitMenu.setItem(i + 18, item);
        }

        lore.clear();
        ItemStack item = ItemCreator.getItem(ChatColor.GRAY + "" + ChatColor.BOLD + "LOCKED", Material.CHEST);
        kitMenu.setItem(13, item);
        kitMenu.setItem(14, item);
        kitMenu.setItem(15, item);
        kitMenu.setItem(16, item);
        item = ItemCreator.getItem(ChatColor.GRAY + "" + ChatColor.BOLD + "LOCKED", Material.ENDER_CHEST);
        kitMenu.setItem(22, item);
        kitMenu.setItem(23, item);
        kitMenu.setItem(24, item);
        kitMenu.setItem(25, item);
        item = ItemCreator.getItem(ChatColor.GRAY + "" + ChatColor.BOLD + "UNLOCK MORE KIT SLOTS (COMING SOON)", Material.EMERALD);
        kitMenu.setItem(37, item);
        item = ItemCreator.getItem(ChatColor.GREEN + "" + ChatColor.BOLD + "VIRTUAL KIT ROOM", Material.NETHER_STAR);
        kitMenu.setItem(38, item);
        item = ItemCreator.getItem(ChatColor.GRAY + "" + ChatColor.BOLD + "INFO (COMING SOON)", Material.OAK_SIGN);
        kitMenu.setItem(40, item);
        item = ItemCreator.getItem(ChatColor.GRAY + "" + ChatColor.BOLD + "PREMADE KITS (COMING SOON)", Material.END_CRYSTAL);
        kitMenu.setItem(42, item);
        item = ItemCreator.getHead(ChatColor.GREEN + "" + ChatColor.BOLD + "PUBLIC KITS", "Kevos");
        kitMenu.setItem(43, item);
        item = ItemCreator.getItem(" ", Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        for (i = 28; i <= 34; ++i) {
            kitMenu.setItem(i, item);
        }

        item = ItemCreator.getItem(" ", Material.BLACK_STAINED_GLASS_PANE);
        kitMenu.setItem(39, item);
        kitMenu.setItem(41, item);
        item = ItemCreator.getItem(" ", Material.BLUE_STAINED_GLASS_PANE);

        for (i = 0; i < kitMenu.getContents().length; ++i) {
            if (kitMenu.getItem(i) == null) {
                kitMenu.setItem(i, item);
            }
        }

        this.inventory = kitMenu;
        player.openInventory(this.getInventory());
        menuChecker.add(player.getUniqueId().toString());
    }
}

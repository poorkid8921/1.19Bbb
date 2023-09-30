package main.expansions;

import main.AestheticNetwork;
import main.expansions.kits.KitClaim;
import main.expansions.kits.KitCreator;
import main.expansions.kits.Languages;
import main.expansions.kits.Utils;
import main.utils.ItemCreator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ExpansionManager {
    public static void initKits(AestheticNetwork a) {
        Utils.setupKitroom(a);
        Utils.setupKitsFile(a);
        if (Utils.kitsConf.contains("data")) {
            Utils.restoreKitMap();
        }
        Utils.restoreKitRoom();
        a.getCommand("kit").setExecutor(new KitCreator());
        a.getCommand("kit1").setExecutor(new KitClaim());
        a.getCommand("kit2").setExecutor(new KitClaim());
        a.getCommand("kit3").setExecutor(new KitClaim());

        // caching
        Inventory inventory = Utils.kitsInventory[0];
        ItemStack item = ItemCreator.getItem("", Material.ENDER_CHEST, List.of(ChatColor.GRAY + "COMING SOON"));
        ItemMeta im = item.getItemMeta();

        item.setType(Material.CHEST);
        List<String> lore = new ArrayList<>();
        for (int i = 1; i <= 3; ++i) {
            lore.addAll(List.of(ChatColor.LIGHT_PURPLE + "to edit:", ChatColor.LIGHT_PURPLE + "• right click this chest", ChatColor.GRAY + "to load:", ChatColor.GRAY + "• /k" + i, ChatColor.GRAY + "• /kit" + i));
            im.setDisplayName("§b§lKit " + i);
            item.setItemMeta(im);
            item.setLore(lore);
            inventory.setItem(i + 9, item);
            lore.clear();
        }

        for (int i = 1; i <= 3; ++i) {
            im.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Ender Chest " + i);
            item.setItemMeta(im);
            inventory.setItem(i + 18, item);
        }

        item.setType(Material.CHEST);
        item.setLore(null);
        im.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "LOCKED");
        item.setItemMeta(im);
        inventory.setItem(13, item);
        inventory.setItem(14, item);
        inventory.setItem(15, item);
        inventory.setItem(16, item);

        item.setType(Material.ENDER_CHEST);
        inventory.setItem(22, item);
        inventory.setItem(23, item);
        inventory.setItem(24, item);
        inventory.setItem(25, item);

        item.setType(Material.EMERALD);
        im.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "UNLOCK MORE KIT SLOTS (COMING SOON)");
        item.setItemMeta(im);
        inventory.setItem(37, item);

        item.setType(Material.NETHER_STAR);
        im.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "VIRTUAL KIT ROOM");
        item.setItemMeta(im);
        inventory.setItem(38, item);

        item.setType(Material.OAK_SIGN);
        im.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "INFO (COMING SOON)");
        item.setItemMeta(im);
        inventory.setItem(40, item);

        item.setType(Material.END_CRYSTAL);
        im.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "PREMADE KITS (COMING SOON)");
        item.setItemMeta(im);
        inventory.setItem(42, item);

        item.setType(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        for (int j = 28; j <= 34; ++j) {
            inventory.setItem(j, item);
        }

        item.setType(Material.BLACK_STAINED_GLASS_PANE);
        inventory.setItem(39, item);
        inventory.setItem(41, item);

        item = ItemCreator.getHead(ChatColor.GREEN + "" + ChatColor.BOLD + "PUBLIC KITS", "Kevos", null);
        inventory.setItem(43, item);

        item.setType(Material.BLUE_STAINED_GLASS_PANE);
        for (int j = 0; j < inventory.getContents().length; ++j) {
            if (inventory.getItem(j) == null) inventory.setItem(j, item);
        }

        Languages.init();
        AestheticNetwork.log("Initialized the Kits expansion.");
    }
}

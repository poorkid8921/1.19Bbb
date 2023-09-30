package main.expansions.kits;

import main.utils.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static main.expansions.kits.Utils.itemKey;

public class PublicKitsInventory {
    Inventory inventory;

    public PublicKitsInventory(Player player, int pg) {
        List<ItemStack> allItems = new ArrayList<>();
        List<String> lore = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : Utils.kitMap.entrySet()) {
            Map<String, Object> value = entry.getValue();
            if (value.containsKey("public")) {
                String name = "Kit";
                String key = entry.getKey();
                lore.add("§dby " + value.get("player"));
                ItemStack head = ItemCreator.getItem("§b§l" + name, Material.CHEST, lore);
                ItemMeta meta = head.getItemMeta();
                meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, key);
                head.setItemMeta(meta);
                allItems.add(head);
                lore.clear();
            }
        }
        int numPages = (int) Math.ceil(allItems.size() / 28.0);
        int endIndex = pg * 28;
        if (pg == numPages) {
            ItemStack filler = ItemCreator.getItem(" ", Material.CRIMSON_BUTTON, null);
            while (allItems.size() < numPages * 28) {
                allItems.add(filler);
            }
        }

        ItemStack[] page = Arrays.copyOfRange(allItems.toArray(new ItemStack[28]), endIndex - 28, endIndex);
        inventory = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Public Kits");
        inventory.setItem(49, ItemCreator.getItem(ChatColor.RED + "EXIT", Material.OAK_DOOR, null));
        if (pg > 1)
            inventory.setItem(48, ItemCreator.getHead(ChatColor.GREEN + "← Back", "MHF_ArrowLeft", null));
        else if (pg < numPages)
            inventory.setItem(50, ItemCreator.getHead(ChatColor.GREEN + "Next →", "MHF_ArrowRight", null));

        ItemStack glass = ItemCreator.getItem(" ", Material.PURPLE_STAINED_GLASS_PANE, null);
        for (int i = 0; i <= 8; ++i) {
            inventory.setItem(i, glass);
        }
        for (int i = 45; i <= 53; ++i) {
            if (inventory.getItem(i) == null)
                inventory.setItem(i, glass);
        }
        for (int i = 9; i <= 36; i += 9) {
            inventory.setItem(i, glass);
        }
        for (int i = 17; i <= 44; i += 9) {
            inventory.setItem(i, glass);
        }
        for (ItemStack item : page) {
            inventory.setItem(inventory.firstEmpty(), item);
        }
        player.openInventory(inventory);
        Utils.publicChecker.put(player.getName(), pg);
    }
}
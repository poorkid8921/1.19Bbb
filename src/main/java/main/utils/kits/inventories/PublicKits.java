package main.utils.kits.inventories;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.Practice;
import main.utils.Initializer;
import main.utils.kits.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import static main.Practice.publicChecker;

public class PublicKits {
    Inventory inventory;

    public PublicKits(Player player, int pg) {
        List<ItemStack> allItems = ObjectArrayList.of();
        List<String> lore = ObjectArrayList.of();

        for (Entry<String, Object2ObjectOpenHashMap<String, Object>> stringHashMapEntry : Practice.kitMap.entrySet()) {
            Object2ObjectOpenHashMap<String, Object> value = stringHashMapEntry.getValue();
            if (value.containsKey("public")) {
                String name;
                if (value.containsKey("name")) {
                    name = (String) value.get("name");
                } else {
                    name = "Kit";
                }

                String key = stringHashMapEntry.getKey();
                lore.add("§dBy " + value.get("player"));
                ItemStack head = ItemCreator.getItem("§b§l" + name, Material.CHEST, lore);
                ItemMeta meta = head.getItemMeta();
                NamespacedKey itemKey = new NamespacedKey(Initializer.p, "key");
                meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, key);
                head.setItemMeta(meta);
                allItems.add(head);
                lore.clear();
            }
        }

        int numPages = (int) Math.ceil((double) allItems.size() / 28.0D);
        int endIndex = pg * 28;
        if (pg == numPages) {
            ItemStack filler = ItemCreator.getItem(" ", Material.CRIMSON_BUTTON);

            while (allItems.size() < numPages * 28) {
                allItems.add(filler);
            }
        }

        ItemStack[] page = Arrays.copyOfRange(allItems.toArray(new ItemStack[28]), endIndex - 28, endIndex);
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Public Kits");
        inv.setItem(49, ItemCreator.getItem(ChatColor.RED + "EXIT", Material.OAK_DOOR));
        if (pg > 1)
            inv.setItem(48, ItemCreator.getHead(ChatColor.GREEN + "← Back", "MHF_ArrowLeft"));
        if (pg < numPages)
            inv.setItem(50, ItemCreator.getHead(ChatColor.GREEN + "Next →", "MHF_ArrowRight"));
        ItemStack glass = ItemCreator.getItem(" ", Material.PURPLE_STAINED_GLASS_PANE);
        int i;
        for (i = 0; i <= 8; ++i) {
            inv.setItem(i, glass);
        }
        for (i = 45; i <= 53; ++i) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, glass);
            }
        }
        for (i = 9; i <= 36; i += 9) {
            inv.setItem(i, glass);
        }
        for (i = 17; i <= 44; i += 9) {
            inv.setItem(i, glass);
        }
        for (ItemStack item : page) {
            inv.setItem(inv.firstEmpty(), item);
        }
        this.inventory = inv;
        player.openInventory(this.getInventory());
        publicChecker.put(player.getUniqueId().toString(), pg);
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}

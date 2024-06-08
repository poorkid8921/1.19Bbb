package main.utils;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static main.utils.Initializer.playerData;
import static main.utils.Utils.createItemStack;

public class Gui {
    private static ItemStack[] report = {};

    public static void init() {
        final Inventory inventory = Bukkit.createInventory(null, 27);
        final ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        final ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName("");
        glass.setItemMeta(meta);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, glass);
        }

        inventory.setItem(10, createItemStack(Material.END_CRYSTAL, "Cheating", ImmutableList.of("§aUse of a hacked client", "§aUse of an illegal client modification")));
        inventory.setItem(11, createItemStack(Material.PAPER, "Doxxing", ImmutableList.of("§aSaying private info of a player in the chat")));
        inventory.setItem(13, createItemStack(Material.RED_BED, "Spamming", ImmutableList.of("§aSaying more than 5 messages on the same topic")));
        report = inventory.getContents();
        inventory.clear();
    }

    public static void openReport(Player player, String arg) {
        playerData.get(player.getName()).setInventoryInfo(Pair.of(0, arg));
        final Inventory inventory = Bukkit.createInventory(player, 27, "ʀᴇᴘᴏʀᴛ");
        inventory.setContents(report);
        inventory.setItem(12, createItemStack(main.utils.Utils.getHead(arg), "Ban Evading", ImmutableList.of("§aUsing an alt to play after being banned")));
        player.openInventory(inventory);
    }
}

package main.inventories;

import main.utils.InventoryInstanceReport;
import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static main.utils.Utils.createItemStack;

@SuppressWarnings("deprecation")
public class ReportInventory extends InventoryInstanceReport {
    public ReportInventory(Player player, String str) {
        super(player, str);
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, "ʀᴇᴘᴏʀᴛ");

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName("");
            glass.setItemMeta(meta);
            inv.setItem(i, glass);
        }

        inv.setItem(10, createItemStack(Material.END_CRYSTAL, "Cheating", List.of("§aUse of a hacked client", "§aUse of an illegal client modification"), arg));
        inv.setItem(11, createItemStack(Material.PAPER, "Doxxing", List.of("§aSaying private info of a player in the chat"), arg));
        inv.setItem(12, createItemStack(Utils.getHead(arg), "Ban Evading", List.of("§aUsing an alt to play after being banned"), arg));
        inv.setItem(13, createItemStack(Material.RED_BED, "Spamming", List.of("§aSaying more than 5 messages on the same topic"), arg));

        return inv;
    }

    @Override
    public void whenClicked(ItemStack item, InventoryAction action, int slot, String arg) {
        Utils.report(player, arg, slot == 10 ? "Cheating" : slot == 11 ? "Doxxing" : slot == 12 ? "Ban Evading" : slot == 13 ? "Spamming" : null);
    }
}
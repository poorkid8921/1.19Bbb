package org.yuri.eco.inventories;

import common.commands.Report;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yuri.eco.utils.Initializer;
import org.yuri.eco.utils.InventoryInstanceReport;
import org.yuri.eco.utils.Utils;

import java.util.List;

import static org.yuri.eco.utils.Utils.report;
import static org.yuri.eco.utils.Utils.translateo;

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

        inv.setItem(10, Report.createitemstack(Material.END_CRYSTAL, "Cheating", List.of(translateo("&aUse of a hacked client"), translateo("&aUse of an illegal client modification"), translateo("&aUse of walksy optimizer below 200ms")), arg));

        inv.setItem(11, Report.createitemstack(Material.PAPER, "Doxxing", List.of(translateo("&aSaying private info of a player in the chat")), arg));

        inv.setItem(12, Report.createitemstack(Utils.getHead(arg), "Ban Evading", List.of(translateo("&aUsing an alt to play after being banned")), arg));

        inv.setItem(13, Report.createitemstack(Material.RED_BED, "Spamming", List.of(translateo("&aSaying more than 5 messages on the same topic")), arg));

        return inv;
    }

    @Override
    public void whenClicked(ItemStack item, InventoryAction action, int slot, String arg) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (!meta.hasLore()) return;

        String str = slot == 10 ? "Cheating" : slot == 11 ? "Doxxing" : slot == 12 ? "Ban Evading" : slot == 13 ? "Spamming" : null;

        report(Initializer.p, player, arg, str);
    }
}
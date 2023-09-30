package main.utils.inventories;

import main.commands.Report;
import main.utils.Instances.InventoryInstanceReport;
import main.utils.ItemCreator;
import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

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

        inv.setItem(10, Report.createitemstack(Material.END_CRYSTAL, "Cheating", List.of(Utils.translate("&aUse of a hacked client"), Utils.translate("&aUse of an illegal client modification")), arg));

        inv.setItem(11, Report.createitemstack(Material.PAPER, "Doxxing", List.of(Utils.translate("&aSaying private info of a player in the chat")), arg));

        inv.setItem(12, Report.createitemstack(ItemCreator.getHead(arg), "Ban Evading", List.of(Utils.translate("&aUsing an alt to play after being banned")), arg));

        inv.setItem(13, Report.createitemstack(Material.RED_BED, "Spamming", List.of(Utils.translate("&aSaying more than 5 messages on the same topic")), arg));

        inv.setItem(14, Report.createitemstack(Material.DIAMOND_SWORD, "Interrupting", List.of(Utils.translate("&aInterrupting fights in flat")), arg));

        inv.setItem(15, Report.createitemstack(Material.RESPAWN_ANCHOR, "Anchor Spamming", List.of(Utils.translate("&aUsing too many anchors in flat")), arg));

        return inv;
    }

    @Override
    public void whenClicked(ItemStack item, int slot, String arg) {
        Utils.report(player.get(), arg, slot == 10 ? "Cheating" : slot == 11 ? "Doxxing" : slot == 12 ? "Ban Evading" : slot == 13 ? "Spamming" : slot == 14 ? "Interrupting" : slot == 15 ? "Anchor Spam" : null);
    }
}
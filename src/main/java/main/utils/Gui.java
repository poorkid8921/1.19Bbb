package main.utils;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Pair;
import main.utils.instances.CustomPlayerDataHolder;
import main.utils.instances.MailHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static main.utils.Constants.playerData;
import static main.utils.Utils.createItemStack;

public class Gui {
    static ItemStack[] report = {};

    public static void init() {
        Inventory inv = Bukkit.createInventory(null, 27);
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName("");
        glass.setItemMeta(meta);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, glass);
        }

        inv.setItem(10, createItemStack(Material.END_CRYSTAL, "Cheating", ImmutableList.of("§aUse of a hacked client", "§aUse of an illegal client modification")));
        inv.setItem(11, createItemStack(Material.PAPER, "Doxxing", ImmutableList.of("§aSaying private info of a player in the chat")));
        inv.setItem(13, createItemStack(Material.RED_BED, "Spamming", ImmutableList.of("§aSaying more than 5 messages on the same topic")));
        report = inv.getContents();
        inv.clear();
    }

    public static void openReport(Player p, String arg) {
        playerData.get(p.getName()).setInventoryInfo(Pair.of(0, arg));
        Inventory inv = Bukkit.createInventory(p, 27, "ʀᴇᴘᴏʀᴛ");
        inv.setContents(report);
        inv.setItem(12, createItemStack(main.utils.Utils.getHead(arg), "Ban Evading", ImmutableList.of("§aUsing an alt to play after being banned")));
        p.openInventory(inv);
    }

    public static void openMail(Player p) {
        CustomPlayerDataHolder D0 = playerData.get(p.getName());
        D0.setInventoryInfo(Pair.of(1, "-"));
        Inventory inv = Bukkit.createInventory(p, 36, "ᴍᴀɪʟ");
        int i = 0;

        for (MailHolder mail : D0.getMails()) {
            inv.setItem(i++, createItemStack(Material.CHEST, mail.getSender()));
        }
        ItemStack GLASS = new ItemStack(Material.GRAY_STAINED_GLASS);
        for (i = 18; i < 27; i++) {
            inv.setItem(i, GLASS);
        }
        inv.setItem(36, createItemStack(Material.NETHER_STAR, "Send"));
        p.openInventory(inv);
    }

    public static void openInbox(Player p) {
        CustomPlayerDataHolder D0 = playerData.get(p.getName());
        D0.setInventoryInfo(Pair.of(1, "0"));
        Inventory inv = Bukkit.createInventory(p, 36, "ɪɴʙᴏx");
        int i = 0;

        for (MailHolder mail : D0.getMails()) {
            inv.setItem(i++, createItemStack(Material.CHEST, mail.getSender()));
        }
        ItemStack GLASS = new ItemStack(Material.GRAY_STAINED_GLASS);
        for (i = 18; i < 27; i++) {
            inv.setItem(i, GLASS);
        }
        inv.setItem(36, createItemStack(Material.NETHER_STAR, "Send"));
        p.openInventory(inv);
    }

    private static void openInbox(CustomPlayerDataHolder D0, Player p, String idx) {
        D0.setInventoryInfo(Pair.of(1, "1"));
        for (MailHolder mail : D0.getMails()) {
            if (mail.getSender().equals(idx)) {
                p.getInventory().setContents(mail.getContents());
                return;
            }
        }
    }
}

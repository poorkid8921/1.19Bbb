package org.yuri.aestheticnetwork.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static org.yuri.aestheticnetwork.utils.Utils.translate;

public class Shop implements CommandExecutor {
    public static Inventory inv = Bukkit.createInventory(null, 27, "ᴀᴇꜱᴛʜᴇᴛɪᴄꜱʜᴏᴘ | ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛꜱ");

    public Shop() {
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName("");
            glass.setItemMeta(meta);
            inv.setItem(i, glass);
        }

        ItemStack ie = new ItemStack(Material.BONE, 1);
        ItemMeta iem = ie.getItemMeta();
        iem.setDisplayName(translate("&eLightning Bolt"));
        iem.setLore(Arrays.asList(translate("&a$100"),
                translate("&7▪ #d6a7ebᴄʟɪᴄᴋ: &fᴘᴜʀᴄʜᴀꜱᴇ")));
        ie.setItemMeta(iem);
        inv.setItem(10, ie);

        ItemStack ie1 = new ItemStack(Material.TNT, 1);
        ItemMeta iem1 = ie1.getItemMeta();
        iem1.setDisplayName(translate("&eExplosion"));
        iem1.setLore(Arrays.asList(translate("&a$200"),
                translate("&7▪ #d6a7ebᴄʟɪᴄᴋ: &fᴘᴜʀᴄʜᴀꜱᴇ")));
        ie1.setItemMeta(iem1);
        inv.setItem(11, ie1);

        ItemStack ie2 = new ItemStack(Material.FIREWORK_ROCKET, 1);
        ItemMeta iem2 = ie2.getItemMeta();
        iem2.setDisplayName(translate("&eFirework"));
        iem2.setLore(Arrays.asList(translate("&a$250"),
                translate("&7▪ #d6a7ebᴄʟɪᴄᴋ: &fᴘᴜʀᴄʜᴀꜱᴇ")));
        ie2.setItemMeta(iem2);
        inv.setItem(12, ie2);

        /*ItemStack ie3 = new ItemStack(Material.FIREWORK_ROCKET, 1);
        ItemMeta iem3 = ie3.getItemMeta();
        iem3.setDisplayName(translate("Explosion"));
        iem3.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&a$300"),
                ChatColor.translateAlternateColorCodes('&', "&7▪ ") +
                        translate("&7▪ #d6a7ebᴄʟɪᴄᴋ: &fᴘᴜʀᴄʜᴀꜱᴇ")));
        ie3.setItemMeta(iem3);
        inv.setItem(13, ie3);*/
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, Command command, @NotNull String s, String[] strings) {
        Player p = ((Player) commandSender).getPlayer();
        if (command.getName().equalsIgnoreCase("shop")) {
            assert p != null;
            p.openInventory(inv);
            return true;
        }

        return false;
    }
}
package org.yuri.aestheticnetwork.commands;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.utils.inventories.ReportInventory;
import org.yuri.aestheticnetwork.utils.Messages.Languages;

import java.util.List;

import static org.yuri.aestheticnetwork.utils.Utils.report;

@SuppressWarnings("deprecation")
public class Report implements CommandExecutor, TabExecutor {
    AestheticNetwork p;

    public Report(AestheticNetwork pp) {
        p = pp;
    }

    public static ItemStack createitemstack(Material mat, String display, List<String> lore, String str) {
        ItemStack ie = new ItemStack(mat, 1);
        ItemMeta iem = ie.getItemMeta();
        iem.setDisplayName(display);
        iem.setLore(lore);
        NamespacedKey key2 = new NamespacedKey(AestheticNetwork.getInstance(), "reported");
        iem.getPersistentDataContainer().set(key2, PersistentDataType.STRING, str);
        ie.setItemMeta(iem);

        return ie;
    }

    public static ItemStack createitemstack(ItemStack ie, String display, List<String> lore, String str) {
        ItemMeta iem = ie.getItemMeta();
        iem.setDisplayName(display);
        iem.setLore(lore);
        NamespacedKey key2 = new NamespacedKey(AestheticNetwork.getInstance(), "reported");
        iem.getPersistentDataContainer().set(key2, PersistentDataType.STRING, str);
        ie.setItemMeta(iem);

        return ie;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Languages.EXCEPTION_REPORT_SPECIFY_PLAYER);
            return true;
        }

        if (args.length < 2)
            new ReportInventory(((Player) sender).getPlayer(), args[0]).open();
        else {
            StringBuilder msgargs = new StringBuilder();
            for (String arg : args) msgargs.append(arg).append(" ");

            report(p, ((Player) sender), msgargs.toString(), "Other");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}

package org.yuri.aestheticnetwork;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.yuri.aestheticnetwork.Utils.report;
import static org.yuri.aestheticnetwork.Utils.translate;

public class Report implements CommandExecutor, TabExecutor {
    static HashMap<UUID, Long> cooldown = new HashMap<>();

    AestheticNetwork p;

    public Report(AestheticNetwork pp) {
        p = pp;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return false;

        if (args.length < 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You must specify who you want to report."));
            return true;
        }

        UUID uuid = ((Player) sender).getUniqueId();
        if (
                cooldown.containsKey(uuid)
                        && cooldown.get(uuid) > System.currentTimeMillis()
        ) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You are on a cooldown right now. Try again later"));
            return true;
        }

        if (args.length < 2) {
            Inventory inv2 = Bukkit.createInventory(null, 27, "ʀᴇᴘᴏʀᴛ");

            for (int i = 0; i < inv2.getSize(); i++) {
                ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
                ItemMeta meta = glass.getItemMeta();
                meta.setDisplayName("");
                glass.setItemMeta(meta);
                inv2.setItem(i, glass);
            }

            inv2.setItem(10, createitemstack(Material.END_CRYSTAL,
                    "Exploiting",
                    List.of(translate("&aUse of a hacked client"),
                            translate("&aUse of an illegal client modification"),
                            translate("&aUse of walksy optimizer")),
                    args[0]));

            inv2.setItem(11, createitemstack(Material.PAPER,
                    "Doxxing",
                    List.of(translate("&aSaying private info of a player in the chat")),
                    args[0]));

            inv2.setItem(12, createitemstack(Material.PLAYER_HEAD,
                    "Ban Evasion",
                    List.of(translate("&aUsing an alt to play after being banned")),
                    args[0]));

            inv2.setItem(13, createitemstack(Material.RED_BED,
                    "Spamming",
                    List.of(translate("&aSaying more than 2 messages on the same topic")),
                    args[0]));

            inv2.setItem(14, createitemstack(Material.DIAMOND,
                    "Advertising",
                    List.of(translate("&aSaying a server ip/dc invite in chat")),
                    args[0]));

            ((Player) sender).openInventory(inv2);
        }
        else
        {
            StringBuilder msgargs = new StringBuilder();
            for (String arg : args) msgargs.append(arg).append(" ");

            report(p, ((Player) sender), msgargs.toString(), "Other");
        }
        return true;
    }

    public ItemStack createitemstack(Material mat, String display, List<String> lore, String str)
    {
        ItemStack ie = new ItemStack(mat, 1);
        ItemMeta iem = ie.getItemMeta();
        iem.setDisplayName(display);
        iem.setLore(lore);
        NamespacedKey key2 = new NamespacedKey(p, "reported");
        iem.getPersistentDataContainer().set(key2, PersistentDataType.STRING, str);
        ie.setItemMeta(iem);

        return ie;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}

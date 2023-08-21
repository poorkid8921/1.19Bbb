package org.yuri.aestheticnetwork.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.inventories.BaltopInventory;

public class baltop implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        /*Inventory inv2 = Bukkit.createInventory(null, 54, "ᴛᴏᴩ ʙᴀʟᴀɴᴄᴇꜱ");

        for (int i = 0; i < inv2.getSize(); i++) {
            ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName("");
            glass.setItemMeta(meta);
            inv2.setItem(i, glass);
        }

        // #1
        inv2.setItem(0, Utils.getHead(1, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_1_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_1_alltime_value%")));
        inv2.setItem(1, Utils.getHead(2, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_2_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_2_alltime_value%")));
        inv2.setItem(2, Utils.getHead(3, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_3_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_3_alltime_value%")));
        inv2.setItem(3, Utils.getHead(4, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_4_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_4_alltime_value%")));
        inv2.setItem(4, Utils.getHead(5, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_5_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_5_alltime_value%")));

        // #2
        inv2.setItem(5, Utils.getHead(6, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_6_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_6_alltime_value%")));
        inv2.setItem(6, Utils.getHead(7, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_7_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_7_alltime_value%")));
        inv2.setItem(7, Utils.getHead(8, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_8_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_8_alltime_value%")));
        inv2.setItem(8, Utils.getHead(9, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_9_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_9_alltime_value%")));
        inv2.setItem(9, Utils.getHead(10, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_10_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_10_alltime_value%")));

        // #3
        inv2.setItem(10, Utils.getHead(11, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_11_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_11_alltime_value%")));
        inv2.setItem(11, Utils.getHead(12, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_12_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_12_alltime_value%")));
        inv2.setItem(12, Utils.getHead(13, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_13_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_13_alltime_value%")));
        inv2.setItem(13, Utils.getHead(14, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_14_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_14_alltime_value%")));
        inv2.setItem(14, Utils.getHead(15, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_15_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_15_alltime_value%")));

        // #4
        inv2.setItem(15, Utils.getHead(16, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_16_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_16_alltime_value%")));
        inv2.setItem(16, Utils.getHead(17, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_17_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_17_alltime_value%")));
        inv2.setItem(17, Utils.getHead(18, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_18_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_18_alltime_value%")));
        inv2.setItem(18, Utils.getHead(19, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_19_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_19_alltime_value%")));
        inv2.setItem(19, Utils.getHead(20, PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_20_alltime_name%"),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_20_alltime_value%")));

        // #self
        inv2.setItem(53, Utils.getHead(Integer.parseInt(PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_position_vault_eco_balance_commas_alltime%")), ((Player) sender),
                PlaceholderAPI.setPlaceholders(null,
                        "%ajlb_lb_vault_eco_balance_commas_alltime_value%")));
        ((Player) sender).openInventory(inv2);
        return true;*/

        new BaltopInventory((Player) sender).open();
        return true;
    }
}
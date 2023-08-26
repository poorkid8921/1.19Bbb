package org.yuri.eco.inventories;

import me.clip.placeholderapi.PlaceholderAPI;
import org.yuri.eco.utils.InventoryInstance;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.yuri.eco.utils.Initializer;
import org.yuri.eco.utils.Utils;

import java.util.List;

public class BaltopInventory extends InventoryInstance {

    private static final int[] slots = {10,
            11,
            12,
            13,
            14,
            15,
            16,
            19,
            20,
            21,
            22,
            23,
            24,
            25,
            28,
            29,
            30,
            31,
            32,
            33,
            34};
    private int page = 1;

    public BaltopInventory(Player player) {
        super(player);
    }

    @Override
    public Inventory getInventory() {
        int maxPage = getMaxPage();

        Inventory inv = Bukkit.createInventory(this, 54, Utils.translateo(page +
                " / " +
                maxPage +
                " - ᴛᴏᴘ ʙᴀʟᴀɴᴄᴇs"));
        int min = (page - 1) * 21;
        int max = page * 21;

        for (int j = min; j < max && j < 4613; j++) {
            final int index = j - min;

            ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
            SkullMeta skull = (SkullMeta) item.getItemMeta();
            //item.setItemMeta(meta);
            inv.setItem(slots[index], item);
            String name = PlaceholderAPI.setPlaceholders(null,
                    "%ajlb_lb_vault_eco_balance_commas_" + index + "_alltime_name%");
            String money = PlaceholderAPI.setPlaceholders(null,
                    "%ajlb_lb_vault_eco_balance_commas_" + index + "_alltime_value%");

            // Apply head texture async
            Bukkit.getScheduler().runTaskAsynchronously(Initializer.p, () -> {
                skull.setDisplayName(Utils.translate("&#46FF64#" + index + " &f" + name));
                skull.setLore(List.of(Utils.translateo("&fᴍᴏɴᴇʏ: &a$" + money)));
                skull.setOwner(name);
                inv.getItem(slots[index]).setItemMeta(skull);
            });
        }

        if (page < maxPage)
            inv.setItem(26, new ItemStack(Material.GREEN_CONCRETE));
        if (page > 1)
            inv.setItem(18, new ItemStack(Material.RED_CONCRETE));

        ItemStack profile = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        inv.setItem(47, profile);
        Bukkit.getScheduler().runTaskAsynchronously(Initializer.p, () -> {
            SkullMeta skull = (SkullMeta) profile.getItemMeta();
            skull.setOwningPlayer(player);
            skull.setDisplayName(Utils.translate("&#46FF64#" +
                    Integer.parseInt(PlaceholderAPI.setPlaceholders(null,
                            "%ajlb_position_vault_eco_balance_commas_alltime%")) + " &f" + player.getDisplayName()));
            skull.setLore(List.of(Utils.translateo("&fᴍᴏɴᴇʏ: &a$" +
                    PlaceholderAPI.setPlaceholders(null,
                            "%ajlb_lb_vault_eco_balance_commas_alltime_value%"))));
            inv.getItem(47).setItemMeta(skull);
        });

        return inv;
    }

    public int getMaxPage() {
        return Math.max(1,
                (int) Math.ceil(4613.0D / 21d));
    }

    @Override
    public void whenClicked(ItemStack item, InventoryAction action, int slot) {
        // next page
        if (item.getType().equals(Material.GREEN_CONCRETE)) {
            page++;
            open();
        }

        // Previous page
        if (item.getType().equals(Material.RED_CONCRETE)) {
            page--;
            open();
        }
    }
}
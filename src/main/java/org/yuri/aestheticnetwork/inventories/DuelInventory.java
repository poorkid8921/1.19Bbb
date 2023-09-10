package org.yuri.aestheticnetwork.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.utils.Initializer;
import org.yuri.aestheticnetwork.utils.InventoryInstanceDuel;
import org.yuri.aestheticnetwork.utils.InventoryInstanceShop;
import org.yuri.aestheticnetwork.utils.Utils;
import org.yuri.aestheticnetwork.utils.duels.DuelManager;

import java.util.List;

import static org.yuri.aestheticnetwork.utils.duels.DuelManager.*;

public class DuelInventory extends InventoryInstanceDuel {
    NamespacedKey a = new NamespacedKey(AestheticNetwork.getInstance(), "against");

    public DuelInventory(Player player) {
        super(player);
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, "ᴅᴜᴇʟs");
        //inv.setContents(Initializer.duelInventory);

        ItemStack i = new ItemStack(Material.FEATHER);
        ItemMeta im = i.getItemMeta();
        im.setLore(List.of(Utils.translateo("&7Spectate others!")));
        i.setItemMeta(im);
        inv.setItem(43, i);
        updateDuels(inv);
        return inv;
    }

    @Override
    public void whenClicked(ItemStack item,
                            int slot) {
        ItemMeta meta = item.getItemMeta();

        if (!meta.hasLore())
            return;

        switch (progress) {
            case 0:
                switch (slot) {
                    case 9 -> {
                        meta.addEnchant(Enchantment.DURABILITY, 1, false);
                        item.setItemMeta(meta);
                        if (!DuelManager.alreadyInMatchmaking(player.getName(), "field")) {
                            DuelManager.startMatchmaking(player, "field");
                            Initializer.inMatchmaking.put(player.getName(), "field");
                        }
                        player.getInventory().close();
                    }
                    case 43 -> {
                        initializeSpectate(getInventory());
                        progress = 1;
                    }
                }
                break;
            case 1:
                switch (slot) {
                    default -> {
                        if (item.getType() == Material.PLAYER_HEAD) {
                            Initializer.spec.put(player.getName(), getDUELrequest(item
                                    .getItemMeta()
                                    .getPersistentDataContainer()
                                    .get(a, PersistentDataType.STRING)).getSender().getName());
                            player.getInventory().close();
                            return;
                        }
                    }
                }
                break;
            case 2:
                break;
        }
    }
}
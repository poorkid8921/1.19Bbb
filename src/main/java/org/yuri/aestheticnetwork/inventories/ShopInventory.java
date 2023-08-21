package org.yuri.aestheticnetwork.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.events;
import org.yuri.aestheticnetwork.utils.InventoryInstance;

import java.util.Arrays;

import static org.yuri.aestheticnetwork.utils.Utils.*;

public class ShopInventory extends InventoryInstance {
    AestheticNetwork plugin = AestheticNetwork.getInstance();

    public ShopInventory(Player player) {
        super(player);
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, "ᴀᴇꜱᴛʜᴇᴛɪᴄꜱʜᴏᴘ | ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛꜱ");

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName("");
            glass.setItemMeta(meta);
            inv.setItem(i, glass);
        }

        ItemStack ie = new ItemStack(Material.BONE, 1);
        ItemMeta iem = ie.getItemMeta();
        iem.setDisplayName(translateo("&eLightning Bolt"));
        iem.setLore(Arrays.asList(translateo("&a$100"),
                translate("&7▪ #d6a7ebᴄʟɪᴄᴋ: &fᴘᴜʀᴄʜᴀꜱᴇ")));
        ie.setItemMeta(iem);
        inv.setItem(10, ie);

        ItemStack ie1 = new ItemStack(Material.TNT, 1);
        ItemMeta iem1 = ie1.getItemMeta();
        iem1.setDisplayName(translateo("&eExplosion"));
        iem1.setLore(Arrays.asList(translateo("&a$200"),
                translate("&7▪ #d6a7ebᴄʟɪᴄᴋ: &fᴘᴜʀᴄʜᴀꜱᴇ")));
        ie1.setItemMeta(iem1);
        inv.setItem(11, ie1);

        ItemStack ie2 = new ItemStack(Material.FIREWORK_ROCKET, 1);
        ItemMeta iem2 = ie2.getItemMeta();
        iem2.setDisplayName(translateo("&eFirework"));
        iem2.setLore(Arrays.asList(translateo("&a$250"),
                translate("&7▪ #d6a7ebᴄʟɪᴄᴋ: &fᴘᴜʀᴄʜᴀꜱᴇ")));
        ie2.setItemMeta(iem2);
        inv.setItem(12, ie2);

        return inv;
    }

    @Override
    public void whenClicked(ItemStack item, InventoryAction action, int slot) {
        switch (slot) {
            case 10 -> killeffect(player, "lightning", "ʟɪɢʜᴛɴɪɴɢ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 50);
            case 11 -> killeffect(player, "totem_explosion", "ᴇxᴘʟᴏꜱɪᴏɴ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 125);
            case 12 -> killeffect(player, "firework", "ꜰɪʀᴇᴡᴏʀᴋ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 200);
        }
    }
}
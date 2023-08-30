package org.yuri.aestheticnetwork.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yuri.aestheticnetwork.utils.InventoryInstanceShop;

import java.util.Arrays;

import static org.yuri.aestheticnetwork.utils.Utils.*;

public class ShopInventory extends InventoryInstanceShop {
    public ShopInventory(Player player) {
        super(player);
    }

    private void init(Inventory inv) {
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

        ItemStack ie3 = new ItemStack(Material.END_CRYSTAL, 1);
        ItemMeta iem3 = ie3.getItemMeta();
        iem3.setDisplayName(translateo("&eL"));
        iem3.setLore(Arrays.asList(translateo("&a$500"),
                translate("&7▪ #d6a7ebᴄʟɪᴄᴋ: &fᴘᴜʀᴄʜᴀꜱᴇ")));
        ie3.setItemMeta(iem3);
        inv.setItem(13, ie3);

        ItemStack ie4 = new ItemStack(Material.BARRIER, 1);
        ItemMeta iem4 = ie4.getItemMeta();
        iem4.setDisplayName(translateo("#fc282fᴇxɪᴛ"));
        ie4.setItemMeta(iem4);
        inv.setItem(26, ie4);
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, "ᴀᴇꜱᴛʜᴇᴛɪᴄꜱʜᴏᴘ | ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛꜱ");
        init(inv);
        return inv;
    }

    @Override
    public void whenClicked(ItemStack item,
                            int slot) {
        ItemMeta meta = item.getItemMeta();

        if (!meta.hasLore())
            return;

        switch (slot) {
            case 10 -> killeffect(player, "lightning", "ʟɪɢʜᴛɴɪɴɢ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 100);
            case 11 -> killeffect(player, "totem_explosion", "ᴇxᴘʟᴏꜱɪᴏɴ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 200);
            case 12 -> killeffect(player, "firework", "ꜰɪʀᴇᴡᴏʀᴋ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 250);
        }
    }
}
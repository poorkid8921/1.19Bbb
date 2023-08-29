package org.yuri.aestheticnetwork.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.events;
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

        if (this.t == 0) {
            switch (slot) {
                case 11 -> {
                    color = "&c";
                    if (pressed > 0)
                        getInventory().getItem(pressed).removeEnchantment(Enchantment.DURABILITY);
                    pressed = 11;
                    item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    item.addEnchantment(Enchantment.DURABILITY, 1);
                }
                case 12 -> {
                    color = "&2";
                    if (pressed > 0)
                        getInventory().getItem(pressed).removeEnchantment(Enchantment.DURABILITY);
                    pressed = 12;
                    item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    item.addEnchantment(Enchantment.DURABILITY, 1);
                }
                case 13 -> {
                    color = "&a";
                    if (pressed > 0)
                        getInventory().getItem(pressed).removeEnchantment(Enchantment.DURABILITY);
                    pressed = 13;
                    item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    item.addEnchantment(Enchantment.DURABILITY, 1);
                }
                case 14 -> {
                    color = "&9";
                    if (pressed > 0)
                        getInventory().getItem(pressed).removeEnchantment(Enchantment.DURABILITY);
                    pressed = 14;
                    item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    item.addEnchantment(Enchantment.DURABILITY, 1);
                }
                case 15 -> {
                    color = "&e";
                    if (pressed > 0)
                        getInventory().getItem(pressed).removeEnchantment(Enchantment.DURABILITY);
                    pressed = 15;
                    item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    item.addEnchantment(Enchantment.DURABILITY, 1);
                }
                case 16 -> {
                    color = "&6";
                    if (pressed > 0)
                        getInventory().getItem(pressed).removeEnchantment(Enchantment.DURABILITY);
                    pressed = 16;
                    item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    item.addEnchantment(Enchantment.DURABILITY, 1);
                }
                case 26 -> {
                    this.t = -1;
                    init(getInventory());
                }
                case 34 -> {
                    ItemMeta iem0 = item.getItemMeta();
                    switch (t) {
                        case 0 -> {
                            method = "title";
                            iem0.setDisplayName(translateo("&eᴅɪsᴘʟᴀʏ - ᴛɪᴛʟᴇ"));
                            iem0.setLore(Arrays.asList(translateo("&7ᴛᴏɢɢʟᴇ ᴛʜᴇ ᴅɪsᴘʟᴀʏ ᴍᴇᴛʜᴏᴅ ᴏғ ᴛʜᴇ ʟ")));
                            t = 1;
                        }
                        case 1 -> {
                            method = "subtitle";
                            iem0.setDisplayName(translateo("&eᴅɪsᴘʟᴀʏ - sᴜʙᴛɪᴛʟᴇ"));
                            iem0.setLore(Arrays.asList(translateo("&7ᴛᴏɢɢʟᴇ ᴛʜᴇ ᴅɪsᴘʟᴀʏ ᴍᴇᴛʜᴏᴅ ᴏғ ᴛʜᴇ ʟ")));
                            t = 2;
                        }
                        case 2 -> {
                            method = "message";
                            iem0.setDisplayName(translateo("&eᴅɪsᴘʟᴀʏ - ᴍᴇssᴀɢᴇ"));
                            iem0.setLore(Arrays.asList(translateo("&7ᴛᴏɢɢʟᴇ ᴛʜᴇ ᴅɪsᴘʟᴀʏ ᴍᴇᴛʜᴏᴅ ᴏғ ᴛʜᴇ ʟ")));
                            t = 3;
                        }
                        case 3 -> {
                            method = "actionbar";
                            iem0.setDisplayName(translateo("&eᴅɪsᴘʟᴀʏ - ᴀᴄᴛɪᴏɴ ʙᴀʀ"));
                            iem0.setLore(Arrays.asList(translateo("&7ᴛᴏɢɢʟᴇ ᴛʜᴇ ᴅɪsᴘʟᴀʏ ᴍᴇᴛʜᴏᴅ ᴏғ ᴛʜᴇ ʟ")));
                            t = 0;
                        }
                    }
                    item.setItemMeta(iem0);
                }
                case 35 -> killeffect(player, color + "_" + method, "ʟ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 500);
            }
        } else {
            switch (slot) {
                case 10 -> killeffect(player, "lightning", "ʟɪɢʜᴛɴɪɴɢ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 100);
                case 11 -> killeffect(player, "totem_explosion", "ᴇxᴘʟᴏꜱɪᴏɴ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 200);
                case 12 -> killeffect(player, "firework", "ꜰɪʀᴇᴡᴏʀᴋ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 250);
                case 13 -> {
                    Inventory inv = getInventory();
                    for (int i = 0; i < inv.getSize(); i++) {
                        inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
                    }

                    ItemStack ie = new ItemStack(Material.BONE, 1);
                    ItemMeta iem = ie.getItemMeta();
                    iem.setDisplayName(translateo("&eᴄᴏʟᴏʀ"));
                    iem.setLore(Arrays.asList(translateo("&7ᴘɪᴄᴋ ᴛʜᴇ ᴄᴏʟᴏʀ ᴏғ ᴛʜᴇ ʟ")));
                    ie.setItemMeta(iem);
                    inv.setItem(9, ie);

                    ItemStack ie1 = new ItemStack(Material.RED_DYE, 1);
                    ItemMeta iem1 = ie1.getItemMeta();
                    iem1.setDisplayName(translateo("&cʀᴇᴅ"));
                    ie1.setItemMeta(iem1);
                    inv.setItem(11, ie1);

                    ItemStack ie2 = new ItemStack(Material.GREEN_DYE, 1);
                    ItemMeta iem2 = ie2.getItemMeta();
                    iem2.setDisplayName(translateo("&2ɢʀᴇᴇɴ"));
                    ie2.setItemMeta(iem2);
                    inv.setItem(12, ie2);

                    ItemStack ie3 = new ItemStack(Material.LIME_DYE, 1);
                    ItemMeta iem3 = ie3.getItemMeta();
                    iem3.setDisplayName(translateo("&aʟɪᴍᴇ"));
                    ie3.setItemMeta(iem3);
                    inv.setItem(13, ie3);

                    ItemStack ie4 = new ItemStack(Material.BLUE_DYE, 1);
                    ItemMeta iem4 = ie4.getItemMeta();
                    iem4.setDisplayName(translateo("&9ʙʟᴜᴇ"));
                    ie4.setItemMeta(iem4);
                    inv.setItem(14, ie4);

                    ItemStack ie5 = new ItemStack(Material.YELLOW_DYE, 1);
                    ItemMeta iem5 = ie5.getItemMeta();
                    iem5.setDisplayName(translateo("&eʏᴇʟʟᴏᴡ"));
                    ie5.setItemMeta(iem5);
                    inv.setItem(15, ie5);

                    ItemStack ie6 = new ItemStack(Material.ORANGE_DYE, 1);
                    ItemMeta iem6 = ie6.getItemMeta();
                    iem6.setDisplayName(translateo("&6ᴏʀᴀɴɢᴇ"));
                    ie6.setItemMeta(iem6);
                    inv.setItem(16, ie6);

                    ItemStack ie7 = new ItemStack(Material.BARRIER, 1);
                    ItemMeta iem7 = ie7.getItemMeta();
                    iem7.setDisplayName(translateo("#fc282fʙᴀᴄᴋ"));
                    ie7.setItemMeta(iem7);
                    inv.setItem(26, ie7);

                    ItemStack ie0 = new ItemStack(Material.GREEN_CONCRETE, 1);
                    ItemMeta iem0 = ie0.getItemMeta();
                    iem0.setDisplayName(translateo("&eᴅɪsᴘʟᴀʏ - ᴀᴄᴛɪᴏɴ ʙᴀʀ "));
                    iem0.setLore(Arrays.asList(translateo("&7ᴛᴏɢɢʟᴇ ᴛʜᴇ ᴅɪsᴘʟᴀʏ ᴍᴇᴛʜᴏᴅ ᴏғ ᴛʜᴇ ʟ")));
                    ie0.setItemMeta(iem0);
                    inv.setItem(34, ie0);

                    ItemStack iep = new ItemStack(Material.GREEN_CONCRETE, 1);
                    ItemMeta iemp = iep.getItemMeta();
                    iemp.setDisplayName(translateo("&eᴅᴏɴᴇ"));
                    iemp.setLore(Arrays.asList(translateo("&7ᴘᴜʀᴄʜᴀsᴇ")));
                    iep.setItemMeta(iemp);
                    inv.setItem(35, iep);
                    this.t = 0;
                    //player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                    //new L_Inventory(player).open();
                }
                case 26 -> player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            }
        }
    }
}
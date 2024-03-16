package main.commands.economy;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import main.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Map;

import static main.utils.Initializer.MAIN_COLOR;

public class Kit implements CommandExecutor, TabCompleter {
    Map<String, Long> cooldowns = new Object2ObjectOpenHashMap<>();
    ItemStack shulker_kit = new ItemStack(Material.RED_SHULKER_BOX);
    ItemStack[] kit = new ItemStack[36];

    public Kit() {
        String name = MAIN_COLOR + "ᴄᴀᴛsᴍᴘ.ꜰᴜɴ";

        BlockStateMeta shulkmeta = (BlockStateMeta) shulker_kit.getItemMeta();
        ShulkerBox box = (ShulkerBox) shulkmeta.getBlockState();
        shulkmeta.setDisplayName(name);
        Inventory shulker_inv = box.getInventory();

        ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET);
        ItemMeta meta = helmet.getItemMeta();
        meta.setDisplayName(name);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, false);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, false);
        meta.addEnchant(Enchantment.OXYGEN, 3, false);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        helmet.setItemMeta(meta);
        shulker_inv.setItem(0, helmet);
        kit[0] = helmet;

        ItemStack chestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
        meta = chestplate.getItemMeta();
        meta.setDisplayName(name);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, false);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, false);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        chestplate.setItemMeta(meta);
        shulker_inv.setItem(1, chestplate);
        kit[1] = chestplate;

        ItemStack leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
        meta = leggings.getItemMeta();
        meta.setDisplayName(name);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, false);
        meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 4, false);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        leggings.setItemMeta(meta);
        shulker_inv.setItem(2, leggings);
        kit[2] = leggings;

        ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);
        meta = boots.getItemMeta();
        meta.setDisplayName(name);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, false);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, false);
        meta.addEnchant(Enchantment.DEPTH_STRIDER, 3, false);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        boots.setItemMeta(meta);
        shulker_inv.setItem(3, boots);
        kit[3] = boots;

        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        meta = sword.getItemMeta();
        meta.setDisplayName(name);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, false);
        meta.addEnchant(Enchantment.DAMAGE_ALL, 5, false);
        meta.addEnchant(Enchantment.KNOCKBACK, 1, false);
        meta.addEnchant(Enchantment.SWEEPING_EDGE, 3, true);
        sword.setItemMeta(meta);
        shulker_inv.setItem(4, sword);
        kit[4] = sword;

        ItemStack pickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
        meta = pickaxe.getItemMeta();
        meta.setDisplayName(name);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, false);
        meta.addEnchant(Enchantment.DIG_SPEED, 5, false);
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, true);
        pickaxe.setItemMeta(meta);
        shulker_inv.setItem(5, pickaxe);
        kit[5] = pickaxe;

        ItemStack gap = new ItemStack(Material.GOLDEN_APPLE, 64);
        shulker_inv.setItem(6, gap);
        kit[6] = gap;

        ItemStack elytra = new ItemStack(Material.ELYTRA);
        meta = elytra.getItemMeta();
        meta.setDisplayName(name);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        elytra.setItemMeta(meta);
        shulker_inv.setItem(9, elytra);
        kit[9] = elytra;

        ItemStack fireworks = new ItemStack(Material.FIREWORK_ROCKET, 64);
        meta = fireworks.getItemMeta();
        ((FireworkMeta) meta).setPower(3);
        fireworks.setItemMeta(meta);
        shulker_inv.setItem(10, fireworks);
        kit[10] = fireworks;

        ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);
        shulker_inv.setItem(11, totem);
        kit[11] = totem;
        shulker_inv.setItem(13, totem);
        kit[13] = totem;

        shulkmeta.setBlockState(box);
        shulker_kit.setItemMeta(shulkmeta);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String sn = sender.getName();
        if (Utils.isPlayerUnRanked(sn)) {
            sender.sendMessage("§7You must be ranked in order to use this command!");
            return true;
        }

        Player p = (Player) sender;
        long D0 = cooldowns.getOrDefault(sn, 0L);
        if (System.currentTimeMillis() > D0) {
            PlayerInventory inv = p.getInventory();
            int freespace = 0;
            int specialspace = 0;
            for (ItemStack item : inv.getContents()) {
                if (item == null)
                    freespace++;
            }
            for (ItemStack item : inv.getArmorContents()) {
                if (item == null)
                    specialspace++;
            }
            if (specialspace == 4 && freespace == 41) {
                inv.setBoots(kit[3]);
                inv.setLeggings(kit[2]);
                inv.setChestplate(kit[1]);
                inv.setHelmet(kit[0]);

                inv.setItemInOffHand(kit[11]);
            } else if (freespace == 0)
                p.getWorld().dropItemNaturally(p.getLocation(), shulker_kit);
            else
                inv.addItem(shulker_kit);
            cooldowns.put(sender.getName(), System.currentTimeMillis() + 1200000L);
            sender.sendMessage("§7You have claimed your kit.");
        } else
            sender.sendMessage("§7You must wait " + MAIN_COLOR + Utils.getTime(D0 - System.currentTimeMillis()) + " §7in order to be able to claim your kit!");
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

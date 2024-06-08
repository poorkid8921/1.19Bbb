package main.commands.economy;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import main.utils.Utils;
import org.bukkit.Location;
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
import static main.utils.Initializer.SECOND_COLOR;

public class Kit implements CommandExecutor, TabCompleter {
    private static final String name = MAIN_COLOR + "ᴄᴀᴛsᴍᴘ.ꜰᴜɴ";
    private static final Map<String, Long> cooldowns = new Object2ObjectOpenHashMap<>();
    private static final ItemStack shulker_kit = new ItemStack(Material.RED_SHULKER_BOX);
    private static final ItemStack crystal = new ItemStack(Material.END_CRYSTAL, 64);
    private static final ItemStack gap = new ItemStack(Material.GOLDEN_APPLE, 64);
    private static final ItemStack obi = new ItemStack(Material.OBSIDIAN, 64);
    private static final ItemStack elytra = new ItemStack(Material.ELYTRA);
    private static final ItemStack fireworks = new ItemStack(Material.FIREWORK_ROCKET, 64);
    private static final ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);
    private static ItemStack helmet;
    private static ItemStack chestplate;
    private static ItemStack leggings;
    private static ItemStack boots;
    private static ItemStack sword;
    private static ItemStack pickaxe;

    public Kit() {
        final BlockStateMeta shulkmeta = (BlockStateMeta) shulker_kit.getItemMeta();
        final ShulkerBox box = (ShulkerBox) shulkmeta.getBlockState();
        shulkmeta.setDisplayName(name);
        final Inventory shulker_inv = box.getInventory();

        helmet = new ItemStack(Material.NETHERITE_HELMET);
        ItemMeta meta = helmet.getItemMeta();
        meta.setDisplayName(name);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, false);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, false);
        meta.addEnchant(Enchantment.OXYGEN, 3, false);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        helmet.setItemMeta(meta);
        shulker_inv.setItem(0, helmet);

        chestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
        meta = chestplate.getItemMeta();
        meta.setDisplayName(name);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, false);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, false);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        chestplate.setItemMeta(meta);
        shulker_inv.setItem(1, chestplate);

        leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
        meta = leggings.getItemMeta();
        meta.setDisplayName(name);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, false);
        meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 4, false);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        leggings.setItemMeta(meta);
        shulker_inv.setItem(2, leggings);

        boots = new ItemStack(Material.NETHERITE_BOOTS);
        meta = boots.getItemMeta();
        meta.setDisplayName(name);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, false);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, false);
        meta.addEnchant(Enchantment.DEPTH_STRIDER, 3, false);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        boots.setItemMeta(meta);
        shulker_inv.setItem(3, boots);

        sword = new ItemStack(Material.NETHERITE_SWORD);
        meta = sword.getItemMeta();
        meta.setDisplayName(name);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, false);
        meta.addEnchant(Enchantment.DAMAGE_ALL, 5, false);
        meta.addEnchant(Enchantment.KNOCKBACK, 1, false);
        meta.addEnchant(Enchantment.SWEEPING_EDGE, 3, true);
        sword.setItemMeta(meta);
        shulker_inv.setItem(4, sword);

        pickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
        meta = pickaxe.getItemMeta();
        meta.setDisplayName(name);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, false);
        meta.addEnchant(Enchantment.DIG_SPEED, 5, false);
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, true);
        pickaxe.setItemMeta(meta);
        shulker_inv.setItem(5, pickaxe);

        shulker_inv.setItem(6, gap);
        shulker_inv.setItem(7, crystal);
        shulker_inv.setItem(8, obi);
        meta = elytra.getItemMeta();
        meta.setDisplayName(name);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        elytra.setItemMeta(meta);
        shulker_inv.setItem(9, elytra);
        meta = fireworks.getItemMeta();
        ((FireworkMeta) meta).setPower(3);
        fireworks.setItemMeta(meta);
        shulker_inv.setItem(10, fireworks);
        shulker_inv.setItem(11, totem);
        shulker_inv.setItem(12, totem);

        shulkmeta.setBlockState(box);
        shulker_kit.setItemMeta(shulkmeta);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final String name = sender.getName();
        if (Utils.isPlayerUnRanked(name)) {
            sender.sendMessage("§7You must be ranked in order to use this command!");
            return true;
        }

        final Player p = (Player) sender;
        final long D0 = cooldowns.getOrDefault(name, 0L);
        if (System.currentTimeMillis() > D0) {
            PlayerInventory inv = p.getInventory();
            boolean anyFreeSpace = false;
            boolean anyFreeArmorSpace = false;
            for (final ItemStack item : inv.getContents()) {
                if (item == null) {
                    anyFreeSpace = true;
                    break;
                }
            }
            for (final ItemStack item : inv.getArmorContents()) {
                if (item == null) {
                    anyFreeArmorSpace = true;
                    break;
                }
            }
            if (anyFreeArmorSpace && anyFreeSpace) {
                inv.setBoots(boots);
                inv.setLeggings(leggings);
                inv.setChestplate(chestplate);
                inv.setHelmet(helmet);

                inv.setItemInOffHand(totem);
                inv.setItem(0, sword);
                inv.setItem(1, pickaxe);
                inv.setItem(2, gap);
                inv.setItem(3, crystal);
                inv.setItem(4, obi);
                inv.setItem(5, elytra);
                inv.setItem(6, fireworks);
            } else if (!anyFreeSpace) {
                final Location loc = p.getLocation();
                loc.getWorld().dropItemNaturally(loc, shulker_kit);
            } else inv.addItem(shulker_kit);
            cooldowns.put(sender.getName(), System.currentTimeMillis() + 3600000L);
            sender.sendMessage("§7You have claimed your kit.");
        } else
            sender.sendMessage("§7You are on a cooldown of " + SECOND_COLOR + Utils.getTime(D0 - System.currentTimeMillis()) + "!");
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

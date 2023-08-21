package org.yuri.aestheticnetwork.commands.duel;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KitManager {
    public static void field(Player p) {
        p.getInventory().setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING, 1));

        for (int i = 10; i <= p.getInventory().getSize(); i++) {
            p.getInventory().setItem(i, new ItemStack(Material.TOTEM_OF_UNDYING,
                    1));
        }

        // utils
        ItemStack shield = new ItemStack(Material.SHIELD, 1);
        shield.addEnchantment(Enchantment.MENDING, 1);
        shield.addEnchantment(Enchantment.DURABILITY, 3);
        p.getInventory().setItem(16, shield);

        ItemStack slowfalling = new ItemStack(Material.TIPPED_ARROW, 64);
        PotionMeta potmeta = (PotionMeta) slowfalling.getItemMeta();
        potmeta.addCustomEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,
                600,
                1), true);
        p.getInventory().setItem(25, slowfalling);

        ItemStack crossbow = new ItemStack(Material.CROSSBOW, 1);
        crossbow.addEnchantment(Enchantment.MENDING, 1);
        crossbow.addEnchantment(Enchantment.DURABILITY, 3);
        crossbow.addEnchantment(Enchantment.PIERCING, 4);
        crossbow.addEnchantment(Enchantment.QUICK_CHARGE, 3);
        crossbow.addEnchantment(Enchantment.MULTISHOT, 1);
        p.getInventory().setItem(34, crossbow);

        ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET, 1);
        helmet.addEnchantment(Enchantment.MENDING, 1);
        helmet.addEnchantment(Enchantment.DURABILITY, 3);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        p.getInventory().setHelmet(helmet);

        ItemStack chestplate = new ItemStack(Material.NETHERITE_CHESTPLATE, 1);
        chestplate.addEnchantment(Enchantment.MENDING, 1);
        chestplate.addEnchantment(Enchantment.DURABILITY, 3);
        chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        p.getInventory().setChestplate(chestplate);

        ItemStack leggings = new ItemStack(Material.NETHERITE_LEGGINGS, 1);
        leggings.addEnchantment(Enchantment.MENDING, 1);
        leggings.addEnchantment(Enchantment.DURABILITY, 3);
        leggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);
        p.getInventory().setLeggings(leggings);

        ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS, 1);
        boots.addEnchantment(Enchantment.MENDING, 1);
        boots.addEnchantment(Enchantment.DURABILITY, 3);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        p.getInventory().setBoots(boots);

        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD, 1);
        sword.addEnchantment(Enchantment.MENDING, 1);
        sword.addEnchantment(Enchantment.DURABILITY, 3);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 5);
        sword.addEnchantment(Enchantment.KNOCKBACK, 1);
        p.getInventory().setItem(0, sword);

        ItemStack pick = new ItemStack(Material.NETHERITE_PICKAXE, 1);
        pick.addEnchantment(Enchantment.MENDING, 1);
        pick.addEnchantment(Enchantment.DURABILITY, 3);
        pick.addEnchantment(Enchantment.DIG_SPEED, 5);
        p.getInventory().setItem(1, pick);

        p.getInventory().setItem(2, new ItemStack(Material.GOLDEN_APPLE,
                64));
        p.getInventory().setItem(3, new ItemStack(Material.ENDER_PEARL,
                16));
        p.getInventory().setItem(4, new ItemStack(Material.OBSIDIAN,
                64));
        p.getInventory().setItem(8, new ItemStack(Material.TOTEM_OF_UNDYING,
                1));

        for (int i = 0; i <= 3; i++) {
            p.getInventory().setItem(5 + (9 * i), new ItemStack(Material.END_CRYSTAL,
                    64));
        }

        for (int i = 0; i <= 2; i++) {
            int a = 9 * i;
            p.getInventory().setItem(6 + a, new ItemStack(Material.GLOWSTONE,
                    64));
            p.getInventory().setItem(7 + a, new ItemStack(Material.RESPAWN_ANCHOR,
                    64));
        }

        // xp bottles
        p.getInventory().setItem(17, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
        p.getInventory().setItem(26, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
        p.getInventory().setItem(35, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
    }

    public static void nethpot(Player p) {
        if (!p.hasPermission("has.staff"))
            return;

        // initializers
        ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET);
        helmet.addEnchantment(Enchantment.MENDING, 1);
        helmet.addEnchantment(Enchantment.DURABILITY, 3);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        ItemStack chestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
        chestplate.addEnchantment(Enchantment.MENDING, 1);
        chestplate.addEnchantment(Enchantment.DURABILITY, 3);
        chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        ItemStack leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
        leggings.addEnchantment(Enchantment.MENDING, 1);
        leggings.addEnchantment(Enchantment.DURABILITY, 3);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);
        boots.addEnchantment(Enchantment.MENDING, 1);
        boots.addEnchantment(Enchantment.DURABILITY, 3);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD, 64);
        sword.addEnchantment(Enchantment.MENDING, 1);
        sword.addEnchantment(Enchantment.DURABILITY, 3);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 5);
        sword.addEnchantment(Enchantment.KNOCKBACK, 1);
        sword.addEnchantment(Enchantment.FIRE_ASPECT, 2);

        // initializers
        ItemStack strength = new ItemStack(Material.SPLASH_POTION);
        PotionMeta potmetastrength = (PotionMeta) strength.getItemMeta();
        potmetastrength.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,
                        1800,
                        2),
                true);
        strength.setItemMeta(potmetastrength);

        ItemStack regen = new ItemStack(Material.SPLASH_POTION);
        PotionMeta regenmeta = (PotionMeta) regen.getItemMeta();
        regenmeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION,
                        440,
                        2),
                true);
        regen.setItemMeta(regenmeta);

        ItemStack speed = new ItemStack(Material.SPLASH_POTION);
        PotionMeta speedmeta = (PotionMeta) regen.getItemMeta();
        speedmeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED,
                        1800,
                        2),
                true);
        speed.setItemMeta(speedmeta);

        ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);

        // regen
        for (int i = 1; i <= p.getInventory().getSize(); i++) {
            p.getInventory().setItem(i, regen);
        }

        // speed
        for (int i = 18; i <= 22; i++) {
            p.getInventory().setItem(i, speed);
        }
        p.getInventory().setItem(2, speed);

        // strength
        for (int i = 27; i <= 31; i++) {
            p.getInventory().setItem(i, strength);
        }
        p.getInventory().setItem(1, strength);
        p.getInventory().setItem(0, sword);

        // exp bottles
        for (int i = 9; i <= 10; i++) {
            p.getInventory().setItem(i, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
        }

        // totems
        for (int i = 11; i <= 12; i++) {
            p.getInventory().setItem(i, totem);
        }
        p.getInventory().setItem(8, totem);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
        p.getInventory().setItemInOffHand(new ItemStack(Material.GOLDEN_APPLE, 64));
    }

    public static void tank(Player e) {
        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS, 1);
        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET, 1);

        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

        e.getInventory().setBoots(boots);
        e.getInventory().setLeggings(leggings);
        e.getInventory().setChestplate(chestplate);
        e.getInventory().setHelmet(helmet);
        e.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD, 1));
    }
}

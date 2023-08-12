package org.yuri.aestheticnetwork.utils;

import io.papermc.lib.PaperLib;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.commands.Report;
import org.yuri.aestheticnetwork.commands.duel.Event;
import org.yuri.aestheticnetwork.commands.duel.KitManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.COLOR_CHAR;

public class Utils {
    static AestheticNetwork plugin = AestheticNetwork.getInstance();

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

    public static Location duelloc(String type, boolean red, Player p, int i, boolean first) {
        Location loc = red ? new Location(Bukkit.getWorld("world"), -391.5, 149, -303.5) : new Location(Bukkit.getWorld("world"), -279.5, 149, -303.5);
        p.getInventory().clear();

        if (type.equals("field")) {
            if (first) AestheticNetwork.getInstance().field += 1;
            KitManager.field(p);
            if (i == 1)
                loc = red ? new Location(Bukkit.getWorld("world"), -391.5, 149, -303.5) : new Location(Bukkit.getWorld("world"), -279.5, 149, -303.5);
            else if (i == 2)
                loc = red ? new Location(Bukkit.getWorld("world"), -391.5, 149, -432.5) : new Location(Bukkit.getWorld("world"), -279.5, 149, -432.5);
            else if (i == 3)
                loc = red ? new Location(Bukkit.getWorld("world"), -520.5, 149, -432.5) : new Location(Bukkit.getWorld("world"), -408.5, 149, -432.5);
            else if (i == 4)
                loc = red ? new Location(Bukkit.getWorld("world"), -520.5, 149, -303.5) : new Location(Bukkit.getWorld("world"), -408.5, 149, -303.5);
            else if (i == 5)
                loc = red ? new Location(Bukkit.getWorld("world"), -649.5, 149, -303.5) : new Location(Bukkit.getWorld("world"), -537.5, 149, -303.5);
            else if (i == 6)
                loc = red ? new Location(Bukkit.getWorld("world"), -649.5, 149, -432.5) : new Location(Bukkit.getWorld("world"), -637.5, 149.5, -432.5);
        }

        loc.setYaw(red ? -90.0F : 90.0F);

        return loc;
    }

    public static void spawn(Player p) {
        PaperLib.teleportAsync(p, plugin.spawn);
    }

    public static void displayduelresume(Player pl, Player p, boolean i, int red, int blue, long olddur, long newdur, String tie, boolean redwon) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
        sdfDate.setTimeZone(TimeZone.getTimeZone("UTC"));
        String strDate = sdfDate.format(new Date(newdur - olddur));

        TextComponent hi = new TextComponent(translate("&7Click to show the duel results"));
        hi.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/event " +
                redwon + " " +
                red + " " +
                blue + " " +
                strDate + " " +
                tie + " " +
                pl.getName() + " " +
                Math.round(pl.getHealth() / 2) + " " +
                pl.getStatistic(Statistic.PLAYER_KILLS) + " " +
                pl.getStatistic(Statistic.DEATHS)));

        pl.sendMessage(translate("&7Teleporting back to spawn in &c5 seconds..."));
        pl.sendMessage(hi);
        Event.valid.add(pl.getUniqueId());
        if (i) {
            Event.valid.add(p.getUniqueId());
            p.sendMessage(hi);
        }

        Utils.manager().set("r." + pl.getUniqueId() + ".wins", Utils.manager().getInt("r." + pl.getUniqueId() + ".wins") + 1);

        Utils.manager().set("r." + p.getUniqueId() + ".losses", Utils.manager().getInt("r." + p.getUniqueId() + ".losses") + 1);

        AestheticNetwork.getInstance().saveCustomConfig();
    }

    public static void startduel(Player user, Player recipient, String type, int round, int maxi, int arena, boolean first) {
        Location loc = Utils.duelloc(type, true, user, arena, first);
        Location loc2 = Utils.duelloc(type, false, recipient, arena, first);
        PaperLib.teleportAsync(user, loc).thenAccept((reason) -> {
            user.setGameMode(GameMode.SURVIVAL);
            user.setWalkSpeed(0.0F);
        });

        PaperLib.teleportAsync(recipient, loc2).thenAccept((reason) -> {
            recipient.setGameMode(GameMode.SURVIVAL);
            recipient.setWalkSpeed(0.0F);
        });

        new BukkitRunnable() {
            int i = 6;

            @Override
            public void run() {
                if (i == 1) {
                    user.sendTitle(translate("&a" + i), "", 1, 20, 1);
                    recipient.sendTitle(translate("&a" + i), "", 1, 20, 1);

                    user.setHealth(20.0F);
                    user.setWalkSpeed(0.2F);
                    user.sendMessage(translate("&7" + (maxi > 1 ? "Round " + round : "Duel") + " started! &cFight!"));

                    recipient.setHealth(20.0F);
                    recipient.setWalkSpeed(0.2F);
                    recipient.sendMessage(translate("&7" + (maxi > 1 ? "Round " + round : "Duel") + " started! &cFight!"));

                    user.sendTitle(translate("&cFight!"), "", 1, 30, 1);
                    recipient.sendTitle(translate("&cFight!"), "", 1, 30, 1);

                    this.cancel();
                    return;
                }

                i--;
                String sec = i == 1 ? "&ca second" : "&c" + i + " seconds";
                String msg = translate("&7" + (maxi > 1 ? "Round" : "Duel") + " starting in " + sec);
                if (round > 1) msg = translate("&7Round &c" + round + " / " + maxi + " &7starting in " + sec);
                user.sendMessage(msg);
                recipient.sendMessage(msg);
            }
        }.runTaskTimer(AestheticNetwork.getInstance(), 0L, 20L);
    }

    public static String translate(String text) {
        final Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(text);
        StringBuilder buffer = new StringBuilder(text.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static FileConfiguration manager() {
        return AestheticNetwork.getInstance().getCustomConfig();
    }

    public static FileConfiguration manager1() {
        return AestheticNetwork.getInstance().getCustomConfig1();
    }

    public static void report(Player e, String report, String reason) {
        e.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String avturl = "https://mc-heads.net/avatar/" + e.getName() + "/100";
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1125353498851168317/8CqqUqAHJn74K1X-9UCLUoHi6psT0Y1t051G5GtOQUPuFRnAAUCXxVL8_Z9jB0I7qm2y");
            webhook.setAvatarUrl(avturl);
            webhook.setUsername("Report");
            webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Report").addField("Server", "Practice", true).addField("Sender", e.getPlayer().getName(), true).addField("Target", report, true).addField("Reason", reason, true).setThumbnail(avturl).setColor(java.awt.Color.ORANGE));
            try {
                webhook.execute();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        e.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Successfully submitted the report."));
        Report.cooldown.put(e.getUniqueId(), System.currentTimeMillis() + 300000);
    }
}
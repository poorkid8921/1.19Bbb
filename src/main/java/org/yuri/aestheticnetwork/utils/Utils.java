package org.yuri.aestheticnetwork.utils;

import io.papermc.lib.PaperLib;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;
import org.yuri.aestheticnetwork.AestheticNetwork;

import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.COLOR_CHAR;
import static org.yuri.aestheticnetwork.utils.Initializer.*;

public class Utils {
    static AestheticNetwork plugin = AestheticNetwork.getInstance();

    public static void spawn(Player p) {
        PaperLib.teleportAsync(p, spawn);
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

    public static String translateo(String text) {
        return ChatColor.translateAlternateColorCodes('&',
                text);
    }

    public static FileConfiguration manager() {
        return AestheticNetwork.getInstance().getCustomConfig();
    }

    public static FileConfiguration manager1() {
        return AestheticNetwork.getInstance().getCustomConfig1();
    }

    public static boolean isSuspectedScanPacket(String buffer) {
        return (buffer.split(" ").length == 1 && !buffer.endsWith(" ")) || !buffer.startsWith("/") || buffer.startsWith("/about");
    }

    public static void spawnFireworks(Location loc) {
        loc.add(new Vector(0, 1, 0));
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(2);
        Random random = new Random();
        fwm.addEffect(FireworkEffect.builder().withColor(color.get(random.nextInt(color.size()))).withColor(color.get(random.nextInt(color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
        fw.setFireworkMeta(fwm);
    }

    public static void duel_spawnFireworks(Location loc) {
        loc.add(new Vector(0, 1, 0));
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.WHITE).withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
        fw.setFireworkMeta(fwm);
        fw.detonate();
    }

    public static void killeffect(Player p, String toset, String fancy, int cost) {
        p.closeInventory();
        double bal = econ.getBalance(p);
        if (bal < cost) {
            p.sendMessage(translate("#d6a7ebꜱʜᴏᴘ &7» #fc282fʏᴏᴜ ᴅᴏɴ'ᴛ ʜᴀᴠᴇ ᴇɴᴏᴜɢʜ ᴍᴏɴᴇʏ"));
            return;
        }

        EconomyResponse ar = econ.withdrawPlayer(p, cost);

        if (ar.transactionSuccess()) {
            plugin.getCustomConfig().set("r." + p.getUniqueId() + ".killeffect", toset);
            plugin.saveCustomConfig();
            p.sendMessage(translate("#d6a7ebꜱʜᴏᴘ &7» &fʏᴏᴜ ʜᴀᴠᴇ ꜱᴜᴄᴄᴇꜱꜱꜰᴜʟʟʏ ᴘᴜʀᴄʜᴀꜱᴇ ᴛʜᴇ #d6a7eb" + fancy + " &fꜰᴏʀ #d6a7eb$" + cost));
        }
    }

    public static void report(AestheticNetwork plugin, Player e, String report, String reason) {
        e.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String avturl = "https://mc-heads.net/avatar/" + e.getName() + "/100";
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1125353498851168317/8CqqUqAHJn74K1X-9UCLUoHi6psT0Y1t051G5GtOQUPuFRnAAUCXxVL8_Z9jB0I7qm2y");
            webhook.setAvatarUrl(avturl);
            webhook.setUsername("Report");
            if (reason == null)
                webhook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setTitle("Report")
                        .addField("Server", "Practice", true)
                        .addField("Sender", e.getPlayer().getName(), true)
                        .addField("Reason", report, true)
                        .addField("@here", "", true)
                        .setThumbnail(avturl)
                        .setColor(java.awt.Color.ORANGE));
            else
                webhook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setTitle("Report")
                        .addField("Server", "Practice", true)
                        .addField("Sender", e.getPlayer().getName(), true)
                        .addField("Target", report, true)
                        .addField("Reason", reason, true)
                        .addField("@here", "", true)
                        .setThumbnail(avturl)
                        .setColor(java.awt.Color.ORANGE));
            try {
                webhook.execute();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        e.sendMessage(translateo("&7Successfully submitted the report"));
        cooldown.put(e.getUniqueId(), System.currentTimeMillis() + 300000);
    }

    public static ItemStack getHead(String player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(player);
        skull.setOwner(player);
        item.setItemMeta(skull);
        return item;
    }
}
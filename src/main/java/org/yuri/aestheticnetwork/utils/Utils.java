package org.yuri.aestheticnetwork.utils;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.yuri.aestheticnetwork.AestheticNetwork;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.COLOR_CHAR;
import static org.yuri.aestheticnetwork.utils.Initializer.cooldown;
import static org.yuri.aestheticnetwork.utils.Initializer.spawn;

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

    public static void report(Player e, String report, String reason) {
        e.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String avturl = "https://mc-heads.net/avatar/" + e.getName() + "/100";
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1125353498851168317/8CqqUqAHJn74K1X-9UCLUoHi6psT0Y1t051G5GtOQUPuFRnAAUCXxVL8_Z9jB0I7qm2y");
            webhook.setAvatarUrl(avturl);
            webhook.setUsername("Report");
            webhook.addEmbed(new DiscordWebhook
                    .EmbedObject()
                    .setTitle("Report")
                    .addField("Server", "Practice", true)
                    .addField("Sender", e.getPlayer().getName(), true)
                    .addField("Target", report, true)
                    .addField("Reason", reason, true)
                    .setThumbnail(avturl).setColor(java.awt.Color.ORANGE));
            try {
                webhook.execute();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        e.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Successfully submitted the report."));
        cooldown.put(e.getUniqueId(), System.currentTimeMillis() + 300000);
    }
}
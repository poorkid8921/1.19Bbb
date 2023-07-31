package bab.bbb.utils;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class Methods {
    public static void setName(ItemStack item, String name, boolean overrideDefaultFormat) {
        ItemMeta itemStackMeta = item.getItemMeta();
        itemStackMeta.setDisplayName(ColorUtils.formatString(name, overrideDefaultFormat));
        item.setItemMeta(itemStackMeta);
    }

    public static String hex(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&").append(c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return translateAlternateColorCodes('&', message);
    }

    public static String translatestring(String s) {
        return hex(s);
    }

    public static String infostring(String s) {
        return hex("&7[&e+&7] " + s);
    }

    public static ChatColor getTPSColor(double tps) {
        if (tps >= 18.0D) {
            return ChatColor.GREEN;
        } else {
            return tps >= 15.0D ? ChatColor.YELLOW : ChatColor.RED;
        }
    }

    public static String format(double tps) {
        double math1 = Math.min((double) Math.round(tps * 100.0D) / 100.0D, 20.0D);
        return getTPSColor(tps) + String.format("%.2f", math1);
    }

    public static String format2(int ping) {
        return (ping >= 85 ? ChatColor.RED : (ping >= 50 ? ChatColor.GOLD : ChatColor.GREEN)) + String.valueOf(ping);
    }

    public static String getTps() {
        return Methods.translatestring(format(Bukkit.getServer().getTPS()[0]));
    }
}
package bab.bbb.utils;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class Methods {
    public static void setName(ItemStack item, String name, boolean overrideDefaultFormat) {
        ItemMeta itemStackMeta = item.getItemMeta();
        itemStackMeta.setDisplayName(ColorUtils.formatString(name, overrideDefaultFormat));
        item.setItemMeta(itemStackMeta);
    }

    public static double[] linear(double from, double to, int max) {
        final double[] res = new double[max];
        for (int i = 0; i < max; i++) {
            res[i] = from + i * ((to - from) / (max - 1));
        }
        return res;
    }

    public static String hsvGradient(String str, Color from, Color to) {
        final float[] hsvFrom = java.awt.Color.RGBtoHSB(from.getRed(), from.getGreen(), from.getBlue(), null);
        final float[] hsvTo = java.awt.Color.RGBtoHSB(to.getRed(), to.getGreen(), to.getBlue(), null);

        final double[] h = linear(hsvFrom[0], hsvTo[0], str.length());
        final double[] s = linear(hsvFrom[1], hsvTo[1], str.length());
        final double[] v = linear(hsvFrom[2], hsvTo[2], str.length());

        final StringBuilder builder = new StringBuilder();

        for (int i = 0 ; i < str.length(); i++) {
            builder.append(net.md_5.bungee.api.ChatColor.of(java.awt.Color.getHSBColor((float) h[i], (float) s[i], (float) v[i]))).append(str.charAt(i));
        }
        return builder.toString();
    }

    public static void errormsg(Player p, String s) {
        p.sendMessage(translatestring("&7[&4-&7] " + s));
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

    public static void elytraflag(Player p, int dmg, int msg, int from, Location fromloc) {
        p.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        if (msg == 1)
            p.sendActionBar(Methods.translatestring("&7Elytras are currently disabled due to &clag"));
        else if (msg == 0)
            p.sendActionBar(Methods.translatestring("&7You're moving &ctoo fast"));
        else
            p.sendActionBar(Methods.translatestring("&7Packet elytra fly isn't &callowed"));

        World rworld = Bukkit.getWorld(p.getWorld().getName());

        int y = rworld.getHighestBlockYAt((int) p.getLocation().getX(), (int) p.getLocation().getZ());
        if (from == 1)
            p.teleport(fromloc);
        else
            p.teleport(new Location(rworld, p.getLocation().getX(), y, p.getLocation().getZ()));
        if (p.isGliding())
            p.setGliding(false);
        p.setFlying(false);
        PlayerInventory playerInv = p.getInventory();
        if (playerInv.getChestplate() != null) {
            playerInv.setChestplate(null);
            p.getWorld().dropItemNaturally(p.getLocation(), playerInv.getChestplate());
        }
        if (dmg != 0)
            p.damage(dmg);
    }

    public static void tpmsg(Player p, Player target, int u) {
        switch (u) {
            case 1 -> // tp has been sent to
                    p.sendMessage(Methods.infostring("the teleport request has been sent to &e" + target.getDisplayName()));
            case 2 -> // timed out msg
                    p.sendMessage(Methods.infostring("your teleport request to &e" + target.getDisplayName() + " &7has timed out"));
            case 3 -> // tpa wants to teleport to you
                    p.sendMessage(Methods.infostring("&e" + target.getDisplayName() + " &7wants to teleport to you"));
            case 4 -> // tpahere wants to teleport to you
                    p.sendMessage(Methods.infostring("&e" + target.getDisplayName() + " &7wants you to teleport to them"));
            case 5 -> // has been denied
                    p.sendMessage(Methods.infostring("your request to &e" + target.getDisplayName() + " &7was denied"));
            case 6 -> // you have denied
                    p.sendMessage(Methods.infostring("you have denied &e" + target.getDisplayName() + "&7's request"));
            case 7 -> // teleporting...
                    p.sendMessage(Methods.infostring("teleporting..."));
            case 8 -> // teleporting to player...
                    p.sendMessage(Methods.infostring("teleporting to &e" + target.getDisplayName() + " &7..."));
            case 9 -> // isn't online anymore
                    p.sendMessage(Methods.infostring("&e" + target.getDisplayName() + " &7isn't online anymore"));
            case 10 -> // teleporting player...
                    p.sendMessage(Methods.infostring("teleporting &e" + target.getDisplayName() + "&7..."));
        }
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

    public static String parseText(Player player, String text) {
        text = Methods.translatestring(text).replaceAll("%tps%", getTps())
                .replaceAll("%players%", Integer.toString(Bukkit.getServer().getOnlinePlayers().size()))
                .replaceAll("%ping%", format2(player.getPing()));
        return text;
    }

    public static String parseText(String text) {
        text = Methods.translatestring(text).replaceAll("%tps%", getTps())
                .replaceAll("%players%", Integer.toString(Bukkit.getServer().getOnlinePlayers().size()));
        return text;
    }
}
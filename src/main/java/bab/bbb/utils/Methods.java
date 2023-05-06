package bab.bbb.utils;

import bab.bbb.Bbb;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class Methods {
    static final String ALL_CODE_REGEX = "[§&][0-9a-f-A-Fk-rK-R]";
    static final String HEX_CODE_REGEX = "#[a-fA-F0-9]{6}";

    public static void setName(ItemStack item, String name, boolean overrideDefaultFormat) {
        ItemMeta itemStackMeta = item.getItemMeta();
        itemStackMeta.setDisplayName(formatString(name, overrideDefaultFormat));
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

        for (int i = 0; i < str.length(); i++) {
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

    public static void maskedkick(Player p) {
        p.kickPlayer(Methods.translatestring("&7Disconnected"));
    }

    public static void message(Player e, String msg)
    {
        for (Player p : Bukkit.getOnlinePlayers()) {
            String b = Bbb.getInstance().getCustomConfig().getString("otherdata." + p.getUniqueId() + ".ignorelist");
            if (b != null && b.contains(e.getPlayer().getName()))
                return;
            p.sendMessage(Methods.parseText(e.getPlayer(), "&7<" + e.getPlayer().getDisplayName() + "&7> " + msg));
        }
    }

    public static void spoiler(Player e, String msg) {
        RainbowText rainbow = new RainbowText(msg);
        StringBuilder msgg = new StringBuilder();
        msgg.append("█".repeat(Math.max(1, msg.length() / 3 - 2)));

        TextComponent spoiler = new TextComponent(Methods.parseText("&7<" + e.getPlayer().getDisplayName() + "&7> " + msgg));
        Text HoverText = new Text(Methods.parseText(e.getPlayer(), msg.replace("||", "")));

        if (msg.contains("[gay]") && msg.contains("[/gay]"))
            HoverText = new Text(Methods.parseText(e.getPlayer(), rainbow.getText()));

        spoiler.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Content[]{HoverText}));

        for (Player p : Bukkit.getOnlinePlayers()) {
            String b = Bbb.getInstance().getCustomConfig().getString("otherdata." + p.getUniqueId() + ".ignorelist");
            if (b != null && b.contains(e.getPlayer().getName()))
                return;
            p.sendMessage(new BaseComponent[]{spoiler});
        }
    }

    public static String placeholders(String msg)
    {
        return Methods.parseText(msg.replace("<3", "❤")
                .replace("[ARROW]", "➜")
                .replace("[TICK]", "✔")
                .replace("[X]", "✖")
                .replace("[STAR]", "★")
                .replace("[POINT]", "●")
                .replace("[FLOWER]", "✿")
                .replace("[XD]", "☻")
                .replace("[DANGER]", "⚠")
                .replace("[MAIL]", "✉")
                .replace("[ARROW2]", "➤")
                .replace("[ROUND_STAR]", "✰")
                .replace("[SUIT]", "♦")
                .replace("[+]", "✦")
                .replace("[CIRCLE]", "●")
                .replace("[HEART]", "❤")
                .replace("[SUN]", "✹")
                .replace("||", "")
                .replace("[gay]", "")
                .replace("[/gay]", "")
                .replace("[unicode]", "")
                .replace("[/unicode]", "")
                .replace("[%]", "‱")
                .replace("[1/4]", "¼")
                .replace("[1/2]", "½")
                .replace("[3/4]", "¾")
                .replace("[SAD]", "☹")
                .replace("[CARPET]", "░▒▓")
                .replace("[BOW]", "&r\uD83C\uDFF9")
                .replace("[SKULL]", "&r☠")
                .replace("[HEART2]", "&r❣")
                .replace("[AXE]", "&r\uD83E\uDE93"));
    }

    public static String unicode(String msg)
    {
        return Methods.parseText(msg.replace("A", "ᴀ")
                .replace("B", "ʙ")
                .replace("C", "ᴄ")
                .replace("D", "ᴅ")
                .replace("E", "ᴇ")
                .replace("F", "ꜰ")
                .replace("G", "ɢ")
                .replace("H", "ʜ")
                .replace("J", "ᴊ")
                .replace("K", "ᴋ")
                .replace("L", "ʟ")
                .replace("M", "ᴍ")
                .replace("N", "ɴ")
                .replace("P", "ᴘ")
                .replace("Q", "ꞯ")
                .replace("R", "ʀ")
                .replace("S", "ꜱ")
                .replace("T", "ᴛ")
                .replace("U", "ᴜ")
                .replace("V", "ᴠ")
                .replace("W", "ᴡ")
                .replace("Y", "ʏ")
                .replace("Z", "ᴢ")
                .replace("a", "ᴀ")
                .replace("b", "ʙ")
                .replace("c", "ᴄ")
                .replace("d", "ᴅ")
                .replace("e", "ᴇ")
                .replace("f", "ꜰ")
                .replace("g", "ɢ")
                .replace("h", "ʜ")
                .replace("j", "ᴊ")
                .replace("k", "ᴋ")
                .replace("l", "ʟ")
                .replace("m", "ᴍ")
                .replace("n", "ɴ")
                .replace("p", "ᴘ")
                .replace("q", "ꞯ")
                .replace("r", "ʀ")
                .replace("s", "ꜱ")
                .replace("t", "ᴛ")
                .replace("u", "ᴜ")
                .replace("v", "ᴠ")
                .replace("w", "ᴡ")
                .replace("y", "ʏ")
                .replace("z", "ᴢ"));
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
        else {
            maskedkick(p);
            //p.sendActionBar(Methods.translatestring("&7Packet elytra fly isn't &callowed"));
            return;
        }

        World rworld = Bukkit.getWorld(p.getWorld().getName());

        int y = rworld.getHighestBlockYAt((int) p.getLocation().getX(), (int) p.getLocation().getZ());
        if (from == 1)
            p.teleport(fromloc);
        else
            p.teleport(new Location(rworld, p.getLocation().getX(), y, p.getLocation().getZ()));
        //if (p.isGliding())
        p.setGliding(false);
        p.setFlying(false);
        PlayerInventory playerInv = p.getInventory();
        if (playerInv.getChestplate() != null) {
            ItemStack a = playerInv.getChestplate();
            playerInv.setChestplate(null);
            p.getWorld().dropItemNaturally(p.getLocation(), a);
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

    public static String removeColorCodes(String string) {
        String a = ChatColor.stripColor(string).replaceAll(ALL_CODE_REGEX, "");
        return ChatColor.stripColor(a).replace(HEX_CODE_REGEX, "");
    }

    public static String formatString(String string, boolean overrideDefaultFormat) {
        if (!overrideDefaultFormat || string.startsWith("&r")) {
            return Methods.translatestring(string);
        } else {
            return Methods.translatestring("&r" + string);
        }
    }

    public static String extractArgs(int nondik, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = nondik; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String allArgs = sb.toString().trim();

        allArgs = allArgs.replace("[<3]", "\u2764");
        allArgs = allArgs.replace("[ARROW]", "\u279c");
        allArgs = allArgs.replace("[TICK]", "\u2714");
        allArgs = allArgs.replace("[X]", "\u2716");
        allArgs = allArgs.replace("[STAR]", "\u2605");
        allArgs = allArgs.replace("[POINT]", "\u25Cf");
        allArgs = allArgs.replace("[FLOWER]", "\u273f");
        allArgs = allArgs.replace("[XD]", "\u263b");
        allArgs = allArgs.replace("[DANGER]", "\u26a0");
        allArgs = allArgs.replace("[MAIL]", "\u2709");
        allArgs = allArgs.replace("[ARROW2]", "\u27a4");
        allArgs = allArgs.replace("[ROUND_STAR]", "\u2730");
        allArgs = allArgs.replace("[SUIT]", "\u2666");
        allArgs = allArgs.replace("[+]", "\u2726");
        allArgs = allArgs.replace("[CIRCLE]", "\u25CF");
        allArgs = allArgs.replace("[SUN]", "\u2739");
        return allArgs;
    }
}
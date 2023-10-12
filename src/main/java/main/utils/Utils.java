package main.utils;

import main.Practice;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Location;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.utils.Initializer.color;
import static main.utils.Initializer.econ;
import static org.bukkit.ChatColor.COLOR_CHAR;

@SuppressWarnings("deprecation")
public class Utils {
    static Practice plugin = Initializer.p;
    static Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");

    public static main.utils.Location Locationfrom(org.bukkit.Location a) {
        return new main.utils.Location(a.getWorld(), a.getX(), a.getY(), a.getZ(), a.getYaw(), a.getPitch());
    }

    public static String translate(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder(text.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static String translateA(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder(text.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
        }
        return matcher.appendTail(buffer).toString();
    }

    public static String translateo(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static boolean isSuspectedScanPacket(String buffer) {
        return (buffer.split(" ").length == 1 && !buffer.endsWith(" ")) || !buffer.startsWith("/") || buffer.startsWith("/about");
    }

    public static void spawnFireworks(Location loc) {
        loc.add(0, 1, 0);
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(color.get(random.nextInt(color.size()))).withColor(color.get(random.nextInt(color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
        fw.setFireworkMeta(fwm);
    }

    public static void killeffect(Player p, int toset, String fancy, int cost) {
        p.closeInventory();
        double bal = econ.getBalance(p);
        if (bal < cost) {
            p.sendMessage(translate("#d6a7ebꜱʜᴏᴘ &7» #fc282fʏᴏᴜ ᴅᴏɴ'ᴛ ʜᴀᴠᴇ ᴇɴᴏᴜɢʜ ᴍᴏɴᴇʏ"));
            return;
        }

        EconomyResponse ar = econ.withdrawPlayer(p, cost);

        if (ar.transactionSuccess()) {
            Practice.cc.set("r." + p.getName() + ".killeffect", toset);
            plugin.saveCustomConfig();
            p.sendMessage(translate("#d6a7ebꜱʜᴏᴘ &7» &fʏᴏᴜ ʜᴀᴠᴇ ꜱᴜᴄᴄᴇꜱꜱꜰᴜʟʟʏ ʙᴜʏ ᴛʜᴇ #d6a7eb" + fancy + " &fꜰᴏʀ #d6a7eb$" + cost));
        }
    }

    public static ItemStack createItemStack(Material mat, String display, List<String> lore) {
        ItemStack ie = new ItemStack(mat, 1);
        ItemMeta iem = ie.getItemMeta();
        iem.setDisplayName(display);
        iem.setLore(lore);
        ie.setItemMeta(iem);
        return ie;
    }

    public static ItemStack createItemStack(ItemStack ie, String display, List<String> lore) {
        ItemMeta iem = ie.getItemMeta();
        iem.setDisplayName(display);
        iem.setLore(lore);
        ie.setItemMeta(iem);
        return ie;
    }

    public static void report(Player pp, String report, String reason) {
        String d = pp.getDisplayName();
        Bukkit.getOnlinePlayers().stream().filter(r -> r.hasPermission("chatlock.use")).forEach(r -> r.sendMessage(translate("#fc282f" + d + " &7has submitted a report against #fc282f" + report + " &7with the reason of #fc282f" + reason)));
        pp.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        Bukkit.getScheduler().runTaskAsynchronously(Initializer.p, () -> {
            String avturl = "https://mc-heads.net/avatar/" + pp.getName() + "/100";
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1125353498851168317/8CqqUqAHJn74K1X-9UCLUoHi6psT0Y1t051G5GtOQUPuFRnAAUCXxVL8_Z9jB0I7qm2y");
            webhook.setAvatarUrl(avturl);
            webhook.setUsername("Report");
            if (reason == null)
                webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Report").addField("Server", "Practice", true).addField("Sender", pp.getName(), true).addField("Reason", report, true).setThumbnail(avturl).setColor(java.awt.Color.ORANGE));
            else
                webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Report").addField("Server", "Practice", true).addField("Sender", pp.getName(), true).addField("Target", report, true).addField("Reason", reason, true).setThumbnail(avturl).setColor(java.awt.Color.ORANGE));
            try {
                webhook.execute();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        pp.sendMessage(translateo("&7Successfully submitted the report."));
    }

    public static void duel_spawnFireworks(Location loc) {
        loc.add(0, 1, 0);
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.WHITE).withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
        fw.setFireworkMeta(fwm);
        fw.detonate();
    }
}
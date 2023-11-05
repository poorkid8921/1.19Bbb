package main.utils;

import main.Practice;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.utils.Languages.MAIN_COLOR;
import static main.utils.Languages.SECOND_COLOR;
import static org.bukkit.ChatColor.COLOR_CHAR;

@SuppressWarnings("deprecation")
public class Utils {
    static Practice plugin = Initializer.p;
    static Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");

    public static String translate(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder(text.length() + 32);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static String translateA(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder(text.length() + 32);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
        }
        return matcher.appendTail(buffer).toString();
    }

    public static void killeffect(Player p, int toset, String fancy) {
        p.closeInventory();

        Practice.config.set("r." + p.getName() + ".c", toset == -1 ? null : toset);
        plugin.saveCustomConfig();
        p.sendMessage(SECOND_COLOR + "sᴇᴛᴛɪɴɢs §7» §f" + (toset == -1 ? "ʏᴏᴜʀ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ ʜᴀs ʙᴇᴇɴ ʀᴇᴍᴏᴠᴇᴅ" :
                "ʏᴏᴜʀ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ ʜᴀs ʙᴇᴇɴ ᴄʜᴀɴɢᴇᴅ ᴛᴏ " + MAIN_COLOR + fancy));
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
        Bukkit.getOnlinePlayers().stream().filter(r -> r.hasPermission("chatlock.use")).forEach(r -> r.sendMessage(MAIN_COLOR + translate(d) + " §7has submitted a report against " + MAIN_COLOR +
                report + (reason == null ? "" : " §7with the reason of " + MAIN_COLOR + reason)));
        pp.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        Bukkit.getScheduler().runTaskAsynchronously(Initializer.p, () -> {
            String avturl = "https://mc-heads.net/avatar/" + pp.getName() + "/100";
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1163543558398156871/XigQ8rIWMLG2Nh0-j-XjQdourDcvsskcehRBTXRHJMfX63_9cqD5aiDQyvg-s58uVPNj");
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
        pp.sendMessage("§7Successfully submitted the report.");
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
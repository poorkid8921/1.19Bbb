package org.yuri.eco.utils;

import common.commands.tpa.TpaRequest;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.yuri.eco.AestheticNetwork;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.COLOR_CHAR;

@SuppressWarnings("deprecation")
public class Utils {
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
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static FileConfiguration manager() {
        return Initializer.p.getCustomConfig();
    }

    public static void report(AestheticNetwork plugin, Player e, String report, String reason) {
        Bukkit.getOnlinePlayers().stream().filter(r -> r.hasPermission("chatlock.use")).forEach(r -> r.sendMessage(translate("#fc282f" + e.getDisplayName() + " &7has submitted a report against #fc282f" + report + " &7with the reason of #fc282f" + reason)));
        e.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String avturl = "https://mc-heads.net/avatar/" + e.getName() + "/100";
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1125353498851168317/8CqqUqAHJn74K1X-9UCLUoHi6psT0Y1t051G5GtOQUPuFRnAAUCXxVL8_Z9jB0I7qm2y");
            webhook.setAvatarUrl(avturl);
            webhook.setUsername("Report");
            if (reason == null)
                webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Report").addField("Server", "Economy", true).addField("Sender", e.getPlayer().getName(), true).addField("Reason", report, true).setThumbnail(avturl).setColor(java.awt.Color.ORANGE));
            else
                webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Report").addField("Server", "Economy", true).addField("Sender", e.getPlayer().getName(), true).addField("Target", report, true).addField("Reason", reason, true).setThumbnail(avturl).setColor(java.awt.Color.ORANGE));
            try {
                webhook.execute();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        e.sendMessage(translateo("&7Successfully submitted the report"));
    }

    public static TpaRequest getRequest(String user) {
        for (TpaRequest r : Initializer.requests) {
            if (r.getReciever().getName().equals(user) || r.getSender().getName().equals(user)) return r;
        }

        return null;
    }

    public static TpaRequest getRequest(String user, String lookup) {
        for (TpaRequest r : Initializer.requests) {
            if ((r.getReciever().getName().equals(user) || r.getSender().getName().equals(user)) && (r.getReciever().getName().equals(lookup) || r.getSender().getName().equals(lookup)))
                return r;
        }

        return null;
    }

    public static void addRequest(Player sender, Player receiver, Type type, boolean showmsg) {
        TpaRequest tpaRequest;

        String name = sender.getDisplayName().replace("&", "");
        boolean c = name.contains(" ");
        TextComponent tc = new TextComponent(c ? translate(name.substring(0, name.indexOf(" ")) + "&r " + name.substring(name.indexOf(" ") + 1)) : translateo(name));
        tc.setColor(net.md_5.bungee.api.ChatColor.valueOf(c ? name.substring(0, 7) : "#fc282f"));
        TextComponent tc1 = new TextComponent(translateo(" &7has requested that you teleport to them. "));

        TextComponent a = new TextComponent(translateo("&7[&a✔&7]"));
        a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(translateo("&7Click to accept the teleportation request"))));
        a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));

        TextComponent b = new TextComponent(translate("&7[#fc282fx&7]"));
        b.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(translateo("&7Click to deny the teleportation request"))));
        b.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));
        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        if (showmsg) sender.sendMessage(translate("&7Request sent to #fc282f" + receiver.getDisplayName()));

        if (type == Type.TPAHERE) {
            tpaRequest = new TpaRequest(sender, receiver, type, false);
            tc.setText(translate("#fc282f" + sender.getDisplayName() + " &7has requested that you teleport to them. "));
        } else tpaRequest = new TpaRequest(sender, receiver, type, true);

        Initializer.requests.add(tpaRequest);

        new BukkitRunnable() {
            @Override
            public void run() {
                Initializer.requests.remove(tpaRequest);
            }
        }.runTaskLater(Initializer.p, 120 * 20);

        TextComponent space = new TextComponent("  ");
        receiver.sendMessage(tc, tc1, a, space, b);
    }

    public static ItemStack getHead(Player player, String killed) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(player.getDisplayName());
        skull.setOwner(player.getName());
        skull.setLore(List.of(translate("&7ᴋɪʟʟᴇʀ #fc282f» " + killed)));
        item.setItemMeta(skull);
        return item;
    }

    public static ItemStack getHead(String player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(player);
        skull.setOwner(player);
        item.setItemMeta(skull);
        return item;
    }

    public enum Type {
        TPA, TPAHERE
    }
}
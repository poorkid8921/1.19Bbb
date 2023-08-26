package org.yuri.eco.utils;

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
import common.commands.tpa.TpaRequest;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class Utils {
    public enum Type {
        TPA, TPAHERE
    }

    public static String translate(String text) {
        final Pattern hexPattern = Pattern.compile("&(#\\\\w{6})\"");
        Matcher matcher = hexPattern.matcher(ChatColor.translateAlternateColorCodes('&', text));
        StringBuilder buffer = new StringBuilder();

        while (matcher.find())
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group(1)).toString());

        return matcher.appendTail(buffer).toString();
    }

    public static String translateo(String text) {
        return ChatColor.translateAlternateColorCodes('&',
                text);
    }

    public static FileConfiguration manager() {
        return Initializer.p.getCustomConfig();
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
                        .addField("Server", "Economy", true)
                        .addField("Sender", e.getPlayer().getName(), true)
                        .addField("Reason", report, true)
                        .setThumbnail(avturl)
                        .setColor(java.awt.Color.ORANGE));
            else
                webhook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setTitle("Report")
                        .addField("Server", "Economy", true)
                        .addField("Sender", e.getPlayer().getName(), true)
                        .addField("Target", report, true)
                        .addField("Reason", reason, true)
                        .setThumbnail(avturl)
                        .setColor(java.awt.Color.ORANGE));
            try {
                webhook.execute();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        e.sendMessage(translateo("&7Successfully submitted the report."));
    }

    public static TpaRequest getRequest(Player user) {
        for (TpaRequest request : Initializer.requests) {
            if (request.getReciever().getName().equalsIgnoreCase(user.getName()) ||
                    request.getSender().getName().equalsIgnoreCase(user.getName())) return request;
        }

        return null;
    }

    public static void addRequest(Player sender,
                                  Player receiver,
                                  Type type,
                                  boolean showmsg) {
        TpaRequest tpaRequest;

        TextComponent tc = new TextComponent(translate(
                "&c" +
                        sender.getDisplayName() +
                        " &7has requested to teleport to you ")); /*new TextComponent(translateo(
                "&c" +
                        sender.getDisplayName() +
                        " &7has requested to teleport to you "));

        tc.setText(TextComponent.toLegacyText(tc));*/
        TextComponent accept = new TextComponent(translateo("&7[&a✔&7]"));
        Text acceptHoverText = new Text(translateo("&7Click to accept the teleportation request"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, acceptHoverText));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        TextComponent deny = new TextComponent(translateo("&7[&cX&7]"));
        Text denyHoverText = new Text(translateo("&7Click to deny the teleportation request"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, denyHoverText));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));

        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        if (showmsg) sender.sendMessage(translate("&7Request sent to &c" + receiver.getDisplayName() + "&7."));

        if (type == Type.TPAHERE) {
            tpaRequest = new TpaRequest(sender, receiver, type, false);
            tc.setText(translate("&c" + sender.getDisplayName() + " &7has requested that you teleport to them. "));
        } else
            tpaRequest = new TpaRequest(sender, receiver, type, true);

        Initializer.requests.add(tpaRequest);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (Utils.getRequest(receiver) != null)
                    Utils.removeRequest(receiver);
            }
        }.runTaskLater(Initializer.p, 120 * 20);

        TextComponent space = new TextComponent("  ");
        receiver.sendMessage(tc, accept, space, deny);
    }

    public static void removeRequest(Player user) {
        Initializer.requests.remove(getRequest(user));
    }

    public static ItemStack getHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(player.getDisplayName());
        skull.setOwner(player.getName());
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
}
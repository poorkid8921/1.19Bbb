package org.yuri.aestheticnetwork;

import lombok.Cleanup;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.yuri.aestheticnetwork.tpa.TpaRequest;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.bukkit.ChatColor.COLOR_CHAR;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

@SuppressWarnings("deprecation")
public class Utils {
    public static final ArrayList<TpaRequest> requests = new ArrayList<>();

    public static String translate(String text) {
        final Pattern hexPattern = Pattern.compile("#[A-fA-F0-9]{6}");
        Matcher matcher = hexPattern.matcher(text);
        while (matcher.find()) {
            String color = text.substring(matcher.start(), matcher.end());
            text = text.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
            matcher = hexPattern.matcher(text);
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private static final Pattern REPLACE_ALL_PATTERN = Pattern.compile("(&)?&([0-9a-fk-orA-FK-OR])");
    private static final Pattern REPLACE_ALL_RGB_PATTERN = Pattern.compile("(&)?&#([0-9a-fA-F]{6})");

    public static String parseHexColor(String hexColor) throws NumberFormatException {
        if (hexColor.startsWith("#")) {
            hexColor = hexColor.substring(1);
        }
        if (hexColor.length() != 6) {
            throw new NumberFormatException("Invalid hex length");
        }

        Color.fromRGB(Integer.decode("#" + hexColor));
        final StringBuilder assembledColorCode = new StringBuilder();
        assembledColorCode.append(ChatColor.COLOR_CHAR + "x");
        for (final char curChar : hexColor.toCharArray()) {
            assembledColorCode.append(ChatColor.COLOR_CHAR).append(curChar);
        }
        return assembledColorCode.toString();
    }

    public static String rgb(String input)
    {
        final StringBuilder legacyBuilder = new StringBuilder();
        final Matcher legacyMatcher = REPLACE_ALL_PATTERN.matcher(input);
        legacyLoop:
        while (legacyMatcher.find()) {
            final boolean isEscaped = legacyMatcher.group(1) != null;
            if (!isEscaped) {
                final char code = legacyMatcher.group(2).toLowerCase(Locale.ROOT).charAt(0);
                for (final ChatColor color : EnumSet.allOf(ChatColor.class)) {
                    if (color.getChar() == code) {
                        legacyMatcher.appendReplacement(legacyBuilder, ChatColor.COLOR_CHAR + "$2");
                        continue legacyLoop;
                    }
                }
            }
            legacyMatcher.appendReplacement(legacyBuilder, "&$2");
        }
        legacyMatcher.appendTail(legacyBuilder);
        final StringBuilder rgbBuilder = new StringBuilder();
        final Matcher rgbMatcher = REPLACE_ALL_RGB_PATTERN.matcher(legacyBuilder.toString());
        while (rgbMatcher.find()) {
            final boolean isEscaped = rgbMatcher.group(1) != null;
            if (!isEscaped) {
                try {
                    final String hexCode = rgbMatcher.group(2);
                    rgbMatcher.appendReplacement(rgbBuilder, parseHexColor(hexCode));
                    continue;
                } catch (final NumberFormatException ignored) {
                }
            }
            rgbMatcher.appendReplacement(rgbBuilder, "&#$2");
        }
        rgbMatcher.appendTail(rgbBuilder);
        return rgbBuilder.toString();
    }

    public static FileConfiguration manager()
    {
        return AestheticNetwork.getInstance().getCustomConfig();
    }


    public static void report(AestheticNetwork plugin, Player e, String report, String reason)
    {
        e.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String avturl = "https://mc-heads.net/avatar/" + e.getName() + "/100";
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1125353498851168317/8CqqUqAHJn74K1X-9UCLUoHi6psT0Y1t051G5GtOQUPuFRnAAUCXxVL8_Z9jB0I7qm2y");
            webhook.setAvatarUrl(avturl);
            webhook.setUsername("Report");
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
        e.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Successfully submitted the report."));
        Report.cooldown.put(e.getUniqueId(), System.currentTimeMillis() + 300000);
    }

    public static TpaRequest getRequest(Player user) {
        for (TpaRequest request : requests) {
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

        TextComponent tc = new TextComponent(translate("&c" + sender.getDisplayName() + " &7has requested to teleport to you "));
        TextComponent accept = new TextComponent(translate("&7[&a✔&7]"));
        Text acceptHoverText = new Text(translate("&7Click to accept the teleportation request"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, acceptHoverText));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        TextComponent deny = new TextComponent(translate("&7[&cX&7]"));
        Text denyHoverText = new Text(translate("&7Click to deny the teleportation request"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, denyHoverText));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));

        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        if (showmsg) sender.sendMessage(translate("&7Request sent to &c" + receiver.getDisplayName() + "&7."));

        if (type == Type.TPAHERE) {
            tpaRequest = new TpaRequest(sender, receiver, type, false);
            tc.setText(translate("&c" + sender.getDisplayName() + " &7has requested that you teleport to them. "));
        }
        else
            tpaRequest = new TpaRequest(sender, receiver, type, true);

        requests.add(tpaRequest);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (Utils.getRequest(receiver) != null)
                    Utils.removeRequest(receiver);
            }
        }.runTaskLater(AestheticNetwork.getInstance(), 120 * 20);

        TextComponent space = new TextComponent("  ");
        receiver.sendMessage(tc, accept, space, deny);
    }

    public static void removeRequest(Player user) {
        requests.remove(getRequest(user));
    }

    public static ItemStack getHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(player.getDisplayName());
        skull.setOwner(player.getName());
        item.setItemMeta(skull);
        return item;
    }

    public static ItemStack getHead(int pos, Player player, String money) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName("#46FF64#" + pos + " &7| &f" + player.getDisplayName());
        skull.setOwner(player.getName());
        skull.setLore(List.of(translate("&a$" + money)));
        item.setItemMeta(skull);
        return item;
    }

    public static ItemStack getHead(int pos, String name, String money) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(translate("#46FF64#" + pos + " &f" + name));
        skull.setOwner(name);
        skull.setLore(List.of(translate("&fᴍᴏɴᴇʏ: &a$" + money)));
        item.setItemMeta(skull);
        return item;
    }
}
package main.utils;

import commands.TpaRequest;
import io.papermc.lib.PaperLib;
import main.Economy;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.COLOR_CHAR;

@SuppressWarnings("deprecation")
public class Utils {
    static TextComponent tc = new TextComponent(translateo(" &7has requested to teleport to you. "));
    static TextComponent a = new TextComponent(translateo("&7[&a✔&7]"));
    static TextComponent b = new TextComponent(translate("&7[&cX&7]"));

    static {
        a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(translateo("&7Click to accept the teleportation request"))));
        b.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(translateo("&7Click to deny the teleportation request"))));
    }

    public static String translate(String text) {
        Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]{6})");
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

    public static void report(Player pp, String report, String reason) {
        String d = pp.getDisplayName();
        Bukkit.getOnlinePlayers().stream().filter(r -> r.hasPermission("chatlock.use")).forEach(r -> r.sendMessage(translate("#fc282f" + d + " &7has submitted a report against #fc282f" + report + " &7with the reason of #fc282f" + reason)));
        pp.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        Initializer.EXECUTOR.execute(() -> {
            String avturl = "https://mc-heads.net/avatar/" + pp.getName() + "/100";
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1125353498851168317/8CqqUqAHJn74K1X-9UCLUoHi6psT0Y1t051G5GtOQUPuFRnAAUCXxVL8_Z9jB0I7qm2y");
            webhook.setAvatarUrl(avturl);
            webhook.setUsername("Report");
            if (reason == null)
                webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Report").addField("Server", "Economy", true).addField("Sender", pp.getName(), true).addField("Reason", report, true).setThumbnail(avturl).setColor(java.awt.Color.ORANGE));
            else
                webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Report").addField("Server", "Economy", true).addField("Sender", pp.getName(), true).addField("Target", report, true).addField("Reason", reason, true).setThumbnail(avturl).setColor(java.awt.Color.ORANGE));
            try {
                webhook.execute();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        pp.sendMessage(translateo("&7Successfully submitted the report."));
    }

    public static TpaRequest getRequest(String user) {
        for (TpaRequest r : Initializer.requests) {
            if (r.getReceiver().getName().equals(user) || r.getSender().getName().equals(user)) return r;
        }

        return null;
    }

    public static TpaRequest getRequest(String user, String lookup) {
        for (TpaRequest r : Initializer.requests) {
            if ((r.getReceiver().getName().equals(user) || r.getSender().getName().equals(user)) && (r.getReceiver().getName().equalsIgnoreCase(lookup) || r.getSender().getName().equalsIgnoreCase(lookup)))
                return r;
        }

        return null;
    }

    public static void tpaccept(TpaRequest request, Player user) {
        Player tempuser;
        Player temprecipient;

        if (request.getType() == 0) {
            tempuser = request.getSender();
            temprecipient = user;
            temprecipient.sendMessage(translate("&7You have accepted #fc282f" + tempuser.getDisplayName() + "&7's teleport request"));
            temprecipient.sendMessage(translateo("&7Teleporting..."));
            if (request.getTpaAll())
                tempuser.sendMessage(translate("#fc282f" + tempuser.getDisplayName() + " &7has accepted your teleport request"));
        } else {
            tempuser = user;
            temprecipient = request.getSender();
            tempuser.sendMessage(translate("&7You have accepted #fc282f" + temprecipient.getDisplayName() + "&7's teleport request"));
            tempuser.sendMessage(translateo("&7Teleporting..."));
            if (request.getTpaAll())
                temprecipient.sendMessage(translate("#fc282f" + tempuser.getDisplayName() + " &7has accepted your teleport request"));
        }

        PaperLib.teleportAsync(tempuser, temprecipient.getLocation()).thenAccept(reason -> Initializer.requests.remove(request));
    }

    public static void addRequest(Player sender, Player receiver, boolean tpahere, boolean showmsg) {
        String sn = sender.getName();
        Initializer.requests.remove(getRequest(sn));
        TpaRequest tpaRequest;

        String clean = sender.getDisplayName();
        int c = clean.indexOf(" ");

        a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sn));
        b.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + sn));

        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        if (showmsg) sender.sendMessage(translate("&7Request sent to #fc282f" + receiver.getDisplayName()));

        if (tpahere) {
            tpaRequest = new TpaRequest(sn, receiver.getName(), 1, false);
            tc.setText(translate("#fc282f" + sender.getDisplayName() + " &7has requested that you teleport to them. "));
        } else tpaRequest = new TpaRequest(sn, receiver.getName(), 0, true);

        Initializer.requests.add(tpaRequest);

        TextComponent space = new TextComponent("  ");
        if (c != -1) {
            String color = clean.substring(0, 7);
            String noHex = clean.replace(color, "");
            String rank = noHex.substring(0, c);
            String realName = noHex.replace(rank + " ", "");
            TextComponent nametc = new TextComponent(realName);
            TextComponent ranktc = new TextComponent(rank + " ");
            nametc.setColor(net.md_5.bungee.api.ChatColor.of(color));
            receiver.sendMessage(ranktc, nametc, tc, a, space, b);
        } else
            receiver.sendMessage(new ComponentBuilder(sn).color(net.md_5.bungee.api.ChatColor.of("#fc282f")).create()[0], tc, a, space, b);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> Initializer.requests.remove(tpaRequest), 2400L);
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
}
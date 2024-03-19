package main.utils;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.Instances.TpaRequest;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.utils.Initializer.*;
import static main.utils.storage.DB.connection;
import static org.bukkit.ChatColor.COLOR_CHAR;

@SuppressWarnings("deprecation")
public class Utils {
    static Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");
    static TextComponent space = new TextComponent("  ");
    static Text ACCEPT_TEXT = new Text("§7Click to accept the teleportation request");
    static Text DENY_TEXT = new Text("§7Click to deny the teleportation request");

    public static void setPlayerData(String name) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT c,m,t,pz,pd,pk,rank FROM data WHERE name = '?'")) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) playerData.put(name, new CustomPlayerDataHolder(
                        resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getInt(3),
                        resultSet.getInt(4),
                        resultSet.getInt(5),
                        resultSet.getInt(6),
                        resultSet.getInt(7)
                ));
            }
        } catch (SQLException ignored) {
        }
        playerData.put(name, new CustomPlayerDataHolder(
                0,
                0,
                -1,
                0,
                0,
                0));
    }

    public static void teleportEffect(World w, Location locC) {
        for (int index = 1; index < 16; index++) {
            double p1 = (index * Math.PI) / 8;
            double p2 = (index - 1) * Math.PI / 8;
            double x1 = Math.cos(p1) * 3;
            double xx2 = Math.cos(p2) * 3;
            double z1 = Math.sin(p1) * 3;
            double z2 = Math.sin(p2) * 3;
            w.spawnParticle(Particle.TOTEM, locC.clone().add(xx2 - x1, 0, z2 - z1), 50);
        }
    }

    public static ObjectArrayList<String> tabCompleteFilter(ObjectArrayList<String> og, String arg) {
        ObjectArrayList<String> og2 = og.clone();
        og2.removeIf(s -> !s.toLowerCase().startsWith(arg));
        og2.sort(String::compareToIgnoreCase);
        return og2;
    }

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

    public static void killeffect(Player p, int toset, String fancy, int money) {
        String name = p.getName();
        p.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        CustomPlayerDataHolder D = playerData.get(name);
        if (money > D.getMoney()) {
            p.sendMessage(SECOND_COLOR + "ꜱʜᴏᴘ » " + MAIN_COLOR + "ʏᴏᴜ ᴅᴏɴ'ᴛ ʜᴀᴠᴇ ᴇɴᴏᴜɢʜ ᴍᴏɴᴇʏ");
            return;
        }
        D.decrementMoney(money);
        D.setKilleffect(toset);
        p.sendMessage(SECOND_COLOR + "ꜱʜᴏᴘ » §f" + (toset == -1 ? "ʏᴏᴜʀ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ ʜᴀs ʙᴇᴇɴ ʀᴇᴍᴏᴠᴇᴅ" :
                "ʏᴏᴜ ʜᴀᴠᴇ ꜱᴜᴄᴄᴇꜱꜱꜰᴜʟʟʏ ᴘᴜʀᴄʜᴀꜱᴇ ᴛʜᴇ " + MAIN_COLOR + fancy + " §fꜰᴏʀ " + SECOND_COLOR + "$" + money));
    }

    public static void killeffect(Player p) {
        p.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        playerData.get(p.getName()).setKilleffect(-1);
        p.sendMessage(SECOND_COLOR + "ꜱʜᴏᴘ » §fʏᴏᴜʀ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ ʜᴀs ʙᴇᴇɴ ʀᴇᴍᴏᴠᴇᴅ");
    }

    public static void submitReport(Player sender, String target, String reason) {
        String name = sender.getName();
        CustomPlayerDataHolder D0 = playerData.get(name);
        String staffMSG = reason == null ? (MAIN_COLOR + D0.getFRank(name) + " §7submitted a report: " + MAIN_COLOR +
                target) : (MAIN_COLOR + D0.getFRank(name) + " §7submitted a report against " + MAIN_COLOR +
                target + " §7with the reason of " + MAIN_COLOR + reason);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (playerData.get(p.getName()).getRank() > 6)
                p.sendMessage(staffMSG);
        }
        Bukkit.getScheduler().runTaskAsynchronously(Initializer.p, () -> {
            try {
                final HttpsURLConnection connection = (HttpsURLConnection) CACHED_WEBHOOK.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
                connection.setDoOutput(true);
                try (final OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write((reason == null ?
                            "{\"tts\":false,\"username\":\"Report\",\"avatar_url\":\"https://mc-heads.net/avatar/" + name + "/100\",\"embeds\":[{\"color\":16762880,\"fields\":[{\"value\":\"Practice\",\"name\":\"Server\",\"inline\":true},{\"value\":\"" + name + "\",\"name\":\"Sender\",\"inline\":true},{\"value\":\"" + target + "\",\"name\":\"Report\",\"inline\":true}],\"title\":\"Report\",\"thumbnail\":{\"url\":\"https://mc-heads.net/avatar/\" + name + \"/100\"}}]}" :
                            "{\"tts\":false,\"username\":\"Report\",\"avatar_url\":\"https://mc-heads.net/avatar/" + name + "/100\",\"embeds\":[{\"color\":16762880,\"fields\":[{\"value\":\"Practice\",\"name\":\"Server\",\"inline\":true},{\"value\":\"" + name + "\",\"name\":\"Sender\",\"inline\":true},{\"value\":\"" + target + "\",\"name\":\"Target\",\"inline\":true},{\"value\":\"" + reason + "\",\"name\":\"Reason\",\"inline\":true}],\"title\":\"Report\",\"thumbnail\":{\"url\":\"https://mc-heads.net/avatar/" + name + "/100\"}}]}")
                            .getBytes(StandardCharsets.UTF_8));
                }
                connection.getInputStream();
            } catch (IOException ignored) {
            }
        });
        sender.sendMessage("§7Successfully submitted your report.");
    }

    public static boolean hasRequestedThePlayer(String name, String requester) {
        for (TpaRequest r : requests) {
            try {
                if ((r.getReceiver() == name && r.getSenderF() == requester) || (r.getSenderF() == name && r.getReceiver() == requester))
                    return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    public static TpaRequest getRequest(String user) {
        for (TpaRequest request : requests) {
            try {
                if (request.getReceiver() == user || request.getSenderF() == user) return request;
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static TpaRequest getRequest(String user, String lookup) {
        for (TpaRequest request : requests) {
            try {
                if ((request.getReceiver() == user || request.getSenderF() == user) && (request.getReceiver() == lookup || request.getSenderF() == lookup))
                    return request;
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static void addRequest(Player sender, Player receiver, boolean tpahere) {
        String sn = sender.getName();
        TextComponent a = new TextComponent("§7[§a✔§7]");
        a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sn));
        TextComponent b = new TextComponent("§7[§cX§7]");
        b.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + sn));

        a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, ACCEPT_TEXT));
        b.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, DENY_TEXT));

        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        String receiverName = receiver.getName();
        sender.sendMessage("§7Request sent to " + MAIN_COLOR + playerData.get(receiverName).getFRank(receiverName));

        receiver.sendMessage(new ComponentBuilder(sn)
                        .color(net.md_5.bungee.api.ChatColor.of("#fc282f")).create()[0],
                new TextComponent(tpahere ? " §7has requested that you teleport to them. " :
                        " §7has requested to teleport to you. "),
                a,
                space,
                b);
        TpaRequest request = new TpaRequest(sn, receiver.getName(), tpahere);
        requests.add(request);

        BukkitTask runnable = new BukkitRunnable() {
            @Override
            public void run() {
                requests.remove(request);
            }
        }.runTaskLaterAsynchronously(Initializer.p, 2400L);
        request.setRunnableid(runnable.getTaskId());
    }

    public static ItemStack getHead(String name, String player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(player);
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getHead(String name, String player, ImmutableList<String> lore) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(player);
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItemStack(Material material, String display) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItemStack(Material material, String display, List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItemStack(ItemStack item, String display, ImmutableList<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display);
        meta.setLore(lore);
        item.setItemMeta(meta);
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
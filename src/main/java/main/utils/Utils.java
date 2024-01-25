package main.utils;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.utils.instances.TpaRequest;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.utils.Constants.*;
import static org.bukkit.ChatColor.COLOR_CHAR;

@SuppressWarnings("deprecation")
public class Utils {
    public static TextComponent space = new TextComponent("  ");
    static Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");

    public static ObjectArrayList<String> tabCompleteFilter(ObjectArrayList<String> og, String arg) {
        ObjectArrayList<String> og2 = og.clone();
        og2.removeIf(s -> !s.toLowerCase().startsWith(arg));
        og2.sort(String::compareToIgnoreCase);
        return og2;
    }

    public static ObjectArrayList<String> tabCompleteFilter(ObjectArrayList<String> og) {
        ObjectArrayList<String> og2 = og.clone();
        og2.sort(String::compareToIgnoreCase);
        return og2;
    }

    public static void spawnFirework(Location loc) {
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc.add(0, 1, 0), EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(2);
        meta.addEffect(FireworkEffect.builder().withColor(Constants.color.get(Constants.RANDOM.nextInt(Constants.color.size()))).withColor(Constants.color.get(Constants.RANDOM.nextInt(Constants.color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
        firework.setFireworkMeta(meta);
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

    public static String translate(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder(text.length() + 32);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static ItemStack createItemStack(Material mat, String display, ImmutableList<String> lore) {
        ItemStack ie = new ItemStack(mat, 1);
        ItemMeta iem = ie.getItemMeta();
        iem.setDisplayName(display);
        iem.setLore(lore);
        ie.setItemMeta(iem);
        return ie;
    }

    public static ItemStack createItemStack(ItemStack ie, String display, ImmutableList<String> lore) {
        ItemMeta iem = ie.getItemMeta();
        iem.setDisplayName(display);
        iem.setLore(lore);
        ie.setItemMeta(iem);
        return ie;
    }

    public static void submitReport(Player pp, String report, String reason) {
        String d = pp.getDisplayName();
        Bukkit.getOnlinePlayers().stream().filter(r -> r.hasPermission("has.staff")).forEach(r -> r.sendMessage(MAIN_COLOR + translate(d) + " §7has submitted a report against " + MAIN_COLOR +
                report + (reason == null ? "" : " §7with the reason of " + MAIN_COLOR + reason)));
        pp.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        Bukkit.getScheduler().runTaskAsynchronously(Constants.p, () -> {
            String pn = pp.getName();
            try {
                final HttpsURLConnection connection = (HttpsURLConnection) CACHED_WEBHOOK.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
                connection.setDoOutput(true);
                try (final OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write((reason == null ?
                            "{\"tts\":false,\"username\":\"Report\",\"avatar_url\":\"https://mc-heads.net/avatar/" + pn + "/100\",\"embeds\":[{\"fields\":[{\"value\":\"Practice\",\"name\":\"Server\",\"inline\":true},{\"value\":\"" + pn + "\",\"name\":\"Sender\",\"inline\":true},{\"value\":\"" + report + "\",\"name\":\"Reason\",\"inline\":true}],\"title\":\"Report\"}]}" :
                            "{\"tts\":false,\"username\":\"Report\",\"avatar_url\":\"https://mc-heads.net/avatar/" + pn + "/100\",\"embeds\":[{\"color\":16762880,\"fields\":[{\"value\":\"Practice\",\"name\":\"Server\",\"inline\":true},{\"value\":\"" + pn + "\",\"name\":\"Sender\",\"inline\":true},{\"value\":\"" + report + "\",\"name\":\"Target\",\"inline\":true},{\"value\":\"" + reason + "\",\"name\":\"Reason\",\"inline\":true}],\"title\":\"Report\",\"thumbnail\":{\"url\":\"https://mc-heads.net/avatar/" + pn + "/100\"}}]}")
                            .getBytes(StandardCharsets.UTF_8));
                }
                connection.getInputStream();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });
        pp.sendMessage("§7Successfully submitted the report.");
    }

    public static TpaRequest getRequest(String user) {
        for (TpaRequest r : requests) {
            if (r.getReceiver().equals(user) || r.getSenderF().equals(user)) return r;
        }

        return null;
    }

    public static TpaRequest getRequest(String user, String lookup) {
        for (TpaRequest r : requests) {
            if ((r.getReceiver().equals(user) || r.getSenderF().equals(user)) && (r.getReceiver().equals(lookup) || r.getSenderF().equals(lookup)))
                return r;
        }

        return null;
    }

    public static void addRequest(Player sender, Player receiver, boolean tpahere, boolean showmsg) {
        String sn = sender.getName();
        TpaRequest request = new TpaRequest(sn, receiver.getName(), tpahere, !tpahere);
        TextComponent a = new TextComponent("§7[§a✔§7]");
        a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sn));

        TextComponent b = new TextComponent("§7[§cX§7]");
        b.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + sn));

        a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to accept the teleportation request")));
        b.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to deny the teleportation request")));

        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        if (showmsg) sender.sendMessage("§7Request sent to " + MAIN_COLOR + translate(receiver.getDisplayName()));

        receiver.sendMessage(new ComponentBuilder(sn).color(net.md_5.bungee.api.ChatColor.of("#fc282f")).create()[0],
                new TextComponent(tpahere ? " §7has requested that you teleport to them. " :
                        " §7has requested to teleport to you. "), a, space, b);

        requests.add(request);
        BukkitTask runnable = new BukkitRunnable() {
            @Override
            public void run() {
                requests.remove(request);
            }
        }.runTaskLaterAsynchronously(Constants.p, 2400L);
        request.setRunnableid(runnable.getTaskId());
    }

    public static ItemStack getHead(Player player, String killed) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(player.getDisplayName());
        skull.setOwner(player.getName());
        skull.setLore(List.of("§7ᴋɪʟʟᴇʀ " + MAIN_COLOR + "» " + killed));
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
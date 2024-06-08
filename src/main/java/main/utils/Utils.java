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
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.utils.Initializer.*;
import static main.utils.modules.npcs.Utils.moveNPCs;
import static main.utils.modules.storage.DB.connection;
import static org.bukkit.ChatColor.COLOR_CHAR;

@SuppressWarnings("deprecation")
public class Utils {
    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");
    private static final TextComponent space = new TextComponent("  ");

    public static void showCosmetics(final ServerGamePacketListenerImpl connection) {
        main.utils.modules.holos.Utils.showForPlayerTickable(connection);
        main.utils.modules.npcs.Utils.showForPlayer(connection);
    }

    public static void rotateNPCs(final Location to, final ServerGamePacketListenerImpl connection) {
        Entity entity;
        Location loc;
        Vector vector;
        double x, z, yaw;
        for (final ServerPlayer k : moveNPCs) {
            entity = k.getBukkitEntity();
            loc = entity.getLocation();
            vector = to.clone().subtract(loc).toVector();
            x = vector.getX();
            z = vector.getZ();
            yaw = Math.toDegrees((Math.atan2(-x, z) + 6.283185307179586D) % 6.283185307179586D);
            connection.send(new ClientboundRotateHeadPacket(k, (byte) ((yaw % 360) * 256 / 360)));
            connection.send(new ClientboundMoveEntityPacket.Rot(entity.getEntityId(), (byte) ((yaw % 360.) * 256 / 360), (byte) ((Math.toDegrees(Math.atan(-vector.getY() / Math.sqrt(NumberConversions.square(x) + NumberConversions.square(z)))) % 360.) * 256 / 360), false));
        }
    }

    public static void setupRank(Player player, int rank) {
        Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
            switch (rank) {
                case 1 -> cattoLovesTeam.addEntity(player);
                case 2 -> cattoHatesTeam.addEntity(player);
                case 3 -> gayTeam.addEntity(player);
                case 4 -> vipTeam.addEntity(player);
                case 5 -> boosterTeam.addEntity(player);
                case 6 -> mediaTeam.addEntity(player);
                case 7 -> trialHelperTeam.addEntity(player);
                case 8 -> helperTeam.addEntity(player);
                case 9 -> jrmodTeam.addEntity(player);
                case 10 -> modTeam.addEntity(player);
                case 11 -> adminTeam.addEntity(player);
                case 12 -> managerTeam.addEntity(player);
                case 13 -> ownerTeam.addEntity(player);
            }
        }, 5L);
    }

    public static void banEffect(Player player) {
        final Location loc = player.getLocation();
        final World world = loc.getWorld();
        double p1, p2;
        for (short index = 1; index < 16; index++) {
            p1 = (index * Math.PI) / 8;
            p2 = (index - 1) * Math.PI / 8;
            world.spawnParticle(Particle.FLAME, loc.clone().add((Math.cos(p2) * 3) - (Math.cos(p1) * 3), 0, (Math.sin(p2) * 3) - (Math.sin(p1) * 3)), 50);
        }
        world.strikeLightningEffect(loc);
    }

    public static void teleportEffect(World world, Location loc) {
        double p1, p2;
        for (short index = 1; index < 16; index++) {
            p1 = (index * Math.PI) / 8;
            p2 = (index - 1) * Math.PI / 8;
            world.spawnParticle(Particle.TOTEM, loc.clone().add((Math.cos(p2) * 3) - (Math.cos(p1) * 3), 0, (Math.sin(p2) * 3) - (Math.sin(p1) * 3)), 50);
        }
    }

    public static CustomPlayerDataHolder getPlayerData(Player player, String name) {
        try (final PreparedStatement statement = connection.prepareStatement("SELECT rank,c,m,t,pz,pd,pk FROM data WHERE name = '?'")) {
            statement.setString(1, name);
            try (final ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    final int rank = resultSet.getInt(1);
                    setupRank(player, rank);
                    return new CustomPlayerDataHolder(rank, resultSet.getInt(2), resultSet.getInt(3), resultSet.getInt(4), resultSet.getInt(5), resultSet.getInt(6), resultSet.getInt(7));
                }
            }
        } catch (SQLException ignored) {
        }
        return new CustomPlayerDataHolder(0, 0, -1, 0, 0, 0);
    }

    public static ObjectArrayList<String> tabCompleteFilter(ObjectArrayList<String> og, String arg) {
        final ObjectArrayList<String> og2 = og.clone();
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

    public static void sendWebhook(String json, URL webhook) {
        try {
            final HttpsURLConnection connection = (HttpsURLConnection) webhook.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
            connection.setDoOutput(true);
            try (final OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(json.getBytes(StandardCharsets.UTF_8));
            }
            connection.getInputStream();
        } catch (IOException ignored) {
        }
    }

    public static void killeffect(Player player, int toset, String fancy, int money) {
        final CustomPlayerDataHolder D = playerData.get(player.getName());
        if (money > D.getMoney()) {
            player.sendMessage(SECOND_COLOR + "ꜱʜᴏᴘ » " + MAIN_COLOR + "ʏᴏᴜ ᴅᴏɴ'ᴛ ʜᴀᴠᴇ ᴇɴᴏᴜɢʜ ᴍᴏɴᴇʏ");
            return;
        }
        D.decrementMoney(money);
        D.setKilleffect(toset);
        player.sendMessage(SECOND_COLOR + "ꜱʜᴏᴘ » §f" + (toset == -1 ? "ʏᴏᴜʀ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ ʜᴀs ʙᴇᴇɴ ʀᴇᴍᴏᴠᴇᴅ" : "ʏᴏᴜ ʜᴀᴠᴇ ꜱᴜᴄᴄᴇꜱꜱꜰᴜʟʟʏ ᴘᴜʀᴄʜᴀꜱᴇ ᴛʜᴇ " + MAIN_COLOR + fancy + " §fꜰᴏʀ " + SECOND_COLOR + "$" + money));
    }

    public static void killeffect(Player player) {
        playerData.get(player.getName()).setKilleffect(-1);
        player.sendMessage(SECOND_COLOR + "ꜱʜᴏᴘ » §fʏᴏᴜʀ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ ʜᴀs ʙᴇᴇɴ ʀᴇᴍᴏᴠᴇᴅ");
    }

    public static void submitReport(Player sender, String target, String reason) {
        final String name = sender.getName();
        final CustomPlayerDataHolder D0 = playerData.get(name);
        final String staffMSG = reason == null ? (MAIN_COLOR + D0.getFRank(name) + " §7submitted a report: " + MAIN_COLOR + target) : (MAIN_COLOR + D0.getFRank(name) + " §7submitted a report against " + MAIN_COLOR + target + " §7with the reason of " + MAIN_COLOR + reason);
        for (Player k : Bukkit.getOnlinePlayers()) {
            if (playerData.get(k.getName()).getRank() > 6) k.sendMessage(staffMSG);
        }
        Bukkit.getScheduler().runTaskAsynchronously(Initializer.p, () -> sendWebhook(reason == null ? "{\"tts\":false,\"username\":\"Report\",\"avatar_url\":\"https://mc-heads.net/avatar/" + name + "/100\",\"embeds\":[{\"color\":16762880,\"fields\":[{\"value\":\"Practice\",\"name\":\"Server\",\"inline\":true},{\"value\":\"" + name + "\",\"name\":\"Sender\",\"inline\":true},{\"value\":\"" + target + "\",\"name\":\"Report\",\"inline\":true}],\"title\":\"Report\",\"thumbnail\":{\"url\":\"https://mc-heads.net/avatar/\" + name + \"/100\"}}]}" : "{\"tts\":false,\"username\":\"Report\",\"avatar_url\":\"https://mc-heads.net/avatar/" + name + "/100\",\"embeds\":[{\"color\":16762880,\"fields\":[{\"value\":\"Practice\",\"name\":\"Server\",\"inline\":true},{\"value\":\"" + name + "\",\"name\":\"Sender\",\"inline\":true},{\"value\":\"" + target + "\",\"name\":\"Target\",\"inline\":true},{\"value\":\"" + reason + "\",\"name\":\"Reason\",\"inline\":true}],\"title\":\"Report\",\"thumbnail\":{\"url\":\"https://mc-heads.net/avatar/" + name + "/100\"}}]}", CACHED_WEBHOOK));
        sender.sendMessage("§7Successfully submitted your report.");
    }

    public static boolean hasRequest(String name, String requester) {
        for (TpaRequest request : requests) {
            if (Objects.equals(request.getReceiver(), name) && Objects.equals(request.getSenderF(), requester) || Objects.equals(request.getSenderF(), name) && Objects.equals(request.getReceiver(), requester)) {
                return true;
            }
        }
        return false;
    }

    public static TpaRequest getRequest(String user) {
        for (TpaRequest request : requests) {
            if (Objects.equals(request.getReceiver(), user) || Objects.equals(request.getSenderF(), user)) {
                return request;
            }
        }
        return null;
    }

    public static TpaRequest getRequest(String user, String lookup) {
        for (TpaRequest request : requests) {
            if ((Objects.equals(request.getReceiver(), user) || Objects.equals(request.getSenderF(), user)) && (Objects.equals(request.getReceiver(), lookup) || Objects.equals(request.getSenderF(), lookup))) {
                return request;
            }
        }
        return null;
    }

    public static void addRequest(Player sender, Player receiver, boolean tpahere) {
        final String name = sender.getName();
        final TextComponent tpacc = new TextComponent("§7[§a✔§7]");
        tpacc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + name));
        tpacc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to accept the teleportation request")));

        final TextComponent tpdeny = new TextComponent("§7[§cX§7]");
        tpdeny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + name));
        tpdeny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to deny the teleportation request")));

        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        final String receiverName = receiver.getName();
        sender.sendMessage("§7Request sent to " + MAIN_COLOR + playerData.get(receiverName).getFRank(receiverName));

        receiver.sendMessage(new ComponentBuilder(name).color(net.md_5.bungee.api.ChatColor.of("#fc282f")).create()[0], new TextComponent(tpahere ? " §7has requested that you teleport to them. " : " §7has requested to teleport to you. "), tpacc, space, tpdeny);
        final TpaRequest request = new TpaRequest(name, receiver.getName(), tpahere);
        request.setRunnableid(new BukkitRunnable() {
            @Override
            public void run() {
                requests.remove(request);
            }
        }.runTaskLater(p, 1200L).getTaskId());
        requests.add(request);
    }

    public static ItemStack getHead(String name, String player) {
        final ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        final SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(player);
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getHead(String name, String player, ImmutableList<String> lore) {
        final ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        final SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(player);
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItemStack(Material material, String display) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItemStack(Material material, String display, List<String> lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItemStack(ItemStack item, String display, ImmutableList<String> lore) {
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getHead(String player) {
        final ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        final SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(player);
        skull.setOwner(player);
        item.setItemMeta(skull);
        return item;
    }
}
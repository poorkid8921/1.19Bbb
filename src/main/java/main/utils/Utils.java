package main.utils;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.utils.instances.CustomPlayerDataHolder;
import main.utils.instances.TpaRequest;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.utils.Initializer.*;
import static main.utils.storage.DB.connection;
import static org.bukkit.ChatColor.COLOR_CHAR;

@SuppressWarnings("deprecation")
public class Utils {
    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");
    public static TextComponent space = new TextComponent("  ");
    public static ServerLevel nmsOverworld;
    public static ServerChunkCache chunkSource = null;
    public static ThreadedLevelLightEngine lightEngine = null;

    public static void setCuboid(short[][] positions, Block block, BlockState material) {
        short startX;
        short startY;
        short startZ;
        short endX;
        short endY;
        short endZ;
        for (short[] k : positions) {
            startX = k[0];
            startY = k[1];
            startZ = k[2];
            endX = k[3];
            endY = k[4];
            endZ = k[5];
            int x1 = Math.min(startX, endX), y1 = Math.min(startY, endY), z1 = Math.min(startZ, endZ);
            int x2 = Math.max(startX, endX), y2 = Math.max(startY, endY), z2 = Math.max(startZ, endZ);
            int sizeX = Math.abs(x2 - x1) + 1, sizeY = Math.abs(y2 - y1) + 1;
            int x3 = 0, y3 = 0, z3 = 0;
            int locx = x1 + x3, locy = y1 + y3, locz = z1 + z3;
            BlockPos blockPos;
            LevelChunk chunk = nmsOverworld.getChunk(locx, locz);
            LevelChunkSection section = chunk.getSections()[chunk.getSectionIndex(locy)];
            int lastChunkX = 0, lastChunkZ = 0;
            long start = System.currentTimeMillis();
            for (int i = 0; i < sizeX * (Math.abs(z2 - z1) + 1); i++) {
                while (System.currentTimeMillis() - start > 50L) {
                    blockPos = new BlockPos(locx, locy, locz);
                    int sectionX = locx >> 4;
                    int sectionZ = locz >> 4;
                    if (lastChunkX != sectionX || lastChunkZ != sectionZ) {
                        lastChunkX = sectionX;
                        lastChunkZ = sectionZ;
                        chunk = nmsOverworld.getChunkAt(blockPos);
                        section = chunk.getSections()[chunk.getSectionIndex(locy)];
                    }
                    if (chunk.getBlockState(blockPos).getBlock() != block) {
                        if (nmsOverworld.capturedTileEntities.get(blockPos) != null)
                            nmsOverworld.capturedTileEntities.remove(blockPos);
                        section.setBlockState(locx & 15, locy & 15, locz & 15, material);
                        chunkSource.blockChanged(blockPos);
                    }
                    if (++x3 >= sizeX) {
                        x3 = 0;
                        if (++y3 >= sizeY) {
                            y3 = 0;
                            ++z3;
                        }
                    }
                    locx = x1 + x3;
                    locy = y1 + y3;
                    locz = z1 + z3;
                    break;
                }
            }
        }
    }

    public static void setArea(int absY, short[][] positions, BlockState material) {
        short startX;
        short startZ;
        short endX;
        short endZ;
        for (short[] k : positions) {
            startX = k[0];
            startZ = k[1];
            endX = k[2];
            endZ = k[3];
            int x1 = Math.min(startX, endX), z1 = Math.min(startZ, endZ);
            int x2 = Math.max(startX, endX), z2 = Math.max(startZ, endZ);
            int sizeX = Math.abs(x2 - x1) + 1;
            int x3 = 0, z3 = 0;
            int locx = x1 + x3, locz = z1 + z3;
            BlockPos blockPos;
            LevelChunk chunk = nmsOverworld.getChunk(locx, locz);
            int absY15 = absY & 15;
            LevelChunkSection section = chunk.getSections()[chunk.getSectionIndex(absY)];
            int lastChunkX = 0, lastChunkZ = 0;
            long start = System.currentTimeMillis();
            for (int i = 0; i < sizeX * (Math.abs(z2 - z1) + 1); i++) {
                while (System.currentTimeMillis() - start > 50L) {
                    blockPos = new BlockPos(locx, absY, locz);
                    int sectionX = locx >> 4;
                    int sectionZ = locz >> 4;
                    if (lastChunkX != sectionX || lastChunkZ != sectionZ) {
                        lastChunkX = sectionX;
                        lastChunkZ = sectionZ;
                        chunk = nmsOverworld.getChunkAt(blockPos);
                        section = chunk.getSections()[chunk.getSectionIndex(absY)];
                    }
                    section.setBlockState(locx & 15, absY15, locz & 15, material);
                    chunkSource.blockChanged(blockPos);
                    lightEngine.checkBlock(blockPos);
                    if (++x3 >= sizeX) {
                        x3 = 0;
                        ++z3;
                    }
                    locx = x1 + x3;
                    locz = z1 + z3;
                    break;
                }
            }
        }
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

    public static void banEffect(Player player) {
        World world = player.getWorld();
        Location loc = player.getLocation();
        for (int index = 1; index < 16; index++) {
            double p1 = (index * Math.PI) / 8;
            double p2 = (index - 1) * Math.PI / 8;
            double x1 = Math.cos(p1) * 3;
            double xx2 = Math.cos(p2) * 3;
            double z1 = Math.sin(p1) * 3;
            double z2 = Math.sin(p2) * 3;
            world.spawnParticle(Particle.FLAME, loc.clone().add(xx2 - x1, 0, z2 - z1), 50);
        }
        world.strikeLightningEffect(loc);
    }

    public static CustomPlayerDataHolder getPlayerData(String name) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT rank,m,t,ez,ed,ek FROM data WHERE name = ?")) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new CustomPlayerDataHolder(resultSet.getInt(2), resultSet.getInt(3), resultSet.getInt(4), resultSet.getInt(5), resultSet.getInt(6), resultSet.getInt(1));
                }
            }
        } catch (SQLException ignored) {
        }
        return new CustomPlayerDataHolder(0, 0, -1, 0, 0, 0);
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

    public static boolean isPlayerUnRanked(String name) {
        return playerData.get(name).getRank() < 4;
    }

    public static String getTime(Player p) {
        StringBuilder builder = new StringBuilder();
        int seconds = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        int days = seconds / 86400;
        if (days > 0) builder.append(days).append(days > 1 ? " days " : " day ");

        seconds %= 86400;
        long hours = seconds / 3600;
        if (hours > 0) builder.append(hours).append(" ").append(hours > 1 ? "hours " : "hour ");
        long minutes = (seconds / 60) % 60;
        if (minutes > 0) builder.append(minutes).append(" ").append(minutes > 1 ? "minutes " : "minute ");
        if (days == 0) {
            seconds %= 60;
            if (seconds > 0) builder.append(seconds).append(" ").append(seconds > 1 ? "seconds" : "second");
        }
        return builder.toString();
    }

    public static String getTime(long ms) {
        StringBuilder builder = new StringBuilder();
        int seconds = (int) (ms / 1000);
        int days = seconds / 86400;
        if (days > 0) builder.append(days).append(days > 1 ? " days " : " day ");

        seconds %= 86400;
        long hours = seconds / 3600;
        if (hours > 0) builder.append(hours).append(" ").append(hours > 1 ? "hours " : "hour ");
        long minutes = (seconds / 60) % 60;
        if (minutes > 0) builder.append(minutes).append(" ").append(minutes > 1 ? "minutes " : "minute ");
        if (days == 0) {
            seconds %= 60;
            if (seconds > 0) builder.append(seconds).append(" ").append(seconds > 1 ? "seconds" : "second");
        }
        return builder.toString();
    }

    public static ObjectArrayList<String> tabCompleteFilter(ObjectArrayList<String> og, String arg) {
        ObjectArrayList<String> og2 = og.clone();
        og2.removeIf(s -> !s.toLowerCase().startsWith(arg));
        og2.sort(String::compareToIgnoreCase);
        return og2;
    }

    public static void spawnFirework(Location loc) {
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc.add(0, 1, 0), EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(2);
        meta.addEffect(FireworkEffect.builder().withColor(color[RANDOM.nextInt(color.length)]).withColor(color[RANDOM.nextInt(color.length)]).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
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

    public static void submitReport(Player sender, String target, String reason) {
        String name = sender.getName();
        CustomPlayerDataHolder D0 = playerData.get(name);
        String staffMSG = reason == null ? (MAIN_COLOR + D0.getFRank(name) + " §7submitted a report: " + MAIN_COLOR + target) : (MAIN_COLOR + D0.getFRank(name) + " §7submitted a report against " + MAIN_COLOR + target + " §7with the reason of " + MAIN_COLOR + reason);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (playerData.get(p.getName()).getRank() > 6) p.sendMessage(staffMSG);
        }
        Bukkit.getScheduler().runTaskAsynchronously(p, () -> sendWebhook(reason == null ? "{\"tts\":false,\"username\":\"Report\",\"avatar_url\":\"https://mc-heads.net/avatar/" + name + "/100\",\"embeds\":[{\"color\":16762880,\"fields\":[{\"value\":\"Economy\",\"name\":\"Server\",\"inline\":true},{\"value\":\"" + name + "\",\"name\":\"Sender\",\"inline\":true},{\"value\":\"" + target + "\",\"name\":\"Report\",\"inline\":true}],\"title\":\"Report\",\"thumbnail\":{\"url\":\"https://mc-heads.net/avatar/\" + name + \"/100\"}}]}" : "{\"tts\":false,\"username\":\"Report\",\"avatar_url\":\"https://mc-heads.net/avatar/" + name + "/100\",\"embeds\":[{\"color\":16762880,\"fields\":[{\"value\":\"Economy\",\"name\":\"Server\",\"inline\":true},{\"value\":\"" + name + "\",\"name\":\"Sender\",\"inline\":true},{\"value\":\"" + target + "\",\"name\":\"Target\",\"inline\":true},{\"value\":\"" + reason + "\",\"name\":\"Reason\",\"inline\":true}],\"title\":\"Report\",\"thumbnail\":{\"url\":\"https://mc-heads.net/avatar/" + name + "/100\"}}]}", CACHED_WEBHOOK));
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
        ObjectArrayList<TpaRequest> clone = requests.clone();
        Collections.reverse(clone);
        for (TpaRequest request : clone) {
            try {
                if (request.getReceiver() == user || request.getSenderF() == user) return request;
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static TpaRequest getRequest(String user, String lookup) {
        ObjectArrayList<TpaRequest> clone = requests.clone();
        Collections.reverse(clone);
        for (TpaRequest request : clone) {
            try {
                if ((request.getReceiver() == user || request.getSenderF() == user) && (request.getReceiver() == lookup || request.getSenderF() == lookup))
                    return request;
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static void addRequest(Player sender, Player receiver, boolean tpahere, boolean showmsg) {
        String sn = sender.getName();
        TpaRequest request = new TpaRequest(sn, receiver.getName(), tpahere, tpahere);
        TextComponent a = new TextComponent("§7[§a✔§7]");
        a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sn));

        TextComponent b = new TextComponent("§7[§cX§7]");
        b.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + sn));

        a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to accept the teleportation request")));
        b.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to deny the teleportation request")));

        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        if (showmsg) {
            String receiverName = receiver.getName();
            sender.sendMessage("§7Request sent to " + MAIN_COLOR + playerData.get(receiverName).getFRank(receiverName));
        }

        receiver.sendMessage(new ComponentBuilder(sn).color(net.md_5.bungee.api.ChatColor.of("#fc282f")).create()[0], new TextComponent(tpahere ? " §7has requested that you teleport to them. " : " §7has requested to teleport to you. "), a, space, b);

        request.setRunnableid(new BukkitRunnable() {
            @Override
            public void run() {
                requests.remove(request);
            }
        }.runTaskLater(p, 1200L).getTaskId());
        requests.add(request);
    }

    public static ItemStack getHead(String pname, String cname) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(cname);
        skull.setOwner(pname);
        skull.setLore(List.of("§7ᴋɪʟʟᴇʀ " + MAIN_COLOR + "» " + cname));
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
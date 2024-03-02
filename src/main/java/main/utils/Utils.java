package main.utils;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.Instances.HomeHolder;
import main.utils.Instances.TpaRequest;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.Economy.d;
import static main.utils.Initializer.*;
import static org.bukkit.ChatColor.COLOR_CHAR;

@SuppressWarnings("deprecation")
public class Utils {
    static final BigDecimal THOUSAND = new BigDecimal(1000);
    static final BigDecimal MILLION = new BigDecimal(1_000_000);
    static final BigDecimal BILLION = new BigDecimal(1_000_000_000);
    static final BigDecimal TRILLION = new BigDecimal(1_000_000_000_000L);
    public static TextComponent space = new TextComponent("  ");
    public static NumberFormat economyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    public static TreeMap<String, Integer> leaderBoardMoney = new TreeMap<>();
    static Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");

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

    public static void spawnLootdrop(int x, int z) {
        int y = 133 + (RANDOM.nextBoolean() ? RANDOM.nextInt(2) : -RANDOM.nextInt(2));
        //x y z axis (2 blocks)
        d.getBlockAt(x, y + 1, z).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x, y + 2, z).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x, y - 1, z).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x, y - 2, z).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x, y, z + 1).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x, y, z + 2).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x, y, z - 1).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x, y, z - 2).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x + 1, y, z).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x + 2, y, z).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x - 1, y, z).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x - 2, y, z).setType(Material.CRYING_OBSIDIAN, false);

        // x y z axis (a block, y-1)
        d.getBlockAt(x, y - 1, z + 1).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x, y - 1, z - 1).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x + 1, y - 1, z).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x - 1, y - 1, z).setType(Material.CRYING_OBSIDIAN, false);

        // x y z axis (a block, y+1)
        d.getBlockAt(x, y + 1, z + 1).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x, y + 1, z - 1).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x + 1, y + 1, z).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x - 1, y + 1, z).setType(Material.CRYING_OBSIDIAN, false);

        // x y z axis (a block, y)
        d.getBlockAt(x + 1, y + 1, z - 1).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x + 1, y + 1, z + 1).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x - 1, y + 1, z + 1).setType(Material.CRYING_OBSIDIAN, false);
        d.getBlockAt(x - 1, y + 1, z + 1).setType(Material.CRYING_OBSIDIAN, false);

        // center
        Block block = d.getBlockAt(x, y, z);
        block.setType(Material.CHEST, false);
        ((Chest) block.getState()).setCustomName("ʟᴏᴏᴛᴅʀᴏᴘ");

        ItemStack[] stack = new ItemStack[27];
        ItemStack TOTEM = new ItemStack(Material.TOTEM_OF_UNDYING);
        if (RANDOM.nextInt(2) == 0) for (int i = 0; i < RANDOM.nextInt(6) + 1; i++)
            stack[RANDOM.nextInt(27)] = TOTEM;

        if (RANDOM.nextInt(2) == 0)
            stack[RANDOM.nextInt(27)] = new ItemStack(Material.GOLDEN_APPLE, RANDOM.nextInt(32) + 32);

        if (RANDOM.nextInt(2) == 0)
            stack[RANDOM.nextInt(27)] = ench(Material.SHIELD, Map.of(Enchantment.MENDING, 1, Enchantment.DURABILITY, 3));

        if (RANDOM.nextInt(2) == 0) for (int i = 0; i < RANDOM.nextInt(3) + 1; i++)
            stack[RANDOM.nextInt(27)] = new ItemStack(Material.EXPERIENCE_BOTTLE, RANDOM.nextInt(33) + 32);

        if (RANDOM.nextInt(2) == 0)
            stack[RANDOM.nextInt(27)] = new ItemStack(Material.END_CRYSTAL, RANDOM.nextInt(33) + 32);

        if (RANDOM.nextInt(2) == 0)
            stack[RANDOM.nextInt(27)] = new ItemStack(Material.OBSIDIAN, RANDOM.nextInt(33) + 32);

        if (RANDOM.nextInt(4) == 0)
            stack[RANDOM.nextInt(27)] = new ItemStack(Material.ENDER_PEARL, RANDOM.nextInt(9) + 8);

        if (RANDOM.nextInt(6) == 0)
            stack[RANDOM.nextInt(27)] = new ItemStack(Material.RESPAWN_ANCHOR, RANDOM.nextInt(33) + 32);

        if (RANDOM.nextInt(6) == 0)
            stack[RANDOM.nextInt(27)] = new ItemStack(Material.GLOWSTONE, RANDOM.nextInt(33) + 32);

        if (RANDOM.nextInt(4) == 0)
            stack[RANDOM.nextInt(27)] = ench(Material.DIAMOND_CHESTPLATE, Enchantment.PROTECTION_ENVIRONMENTAL, RANDOM.nextInt(3) + 1, RANDOM.nextInt(4) == 0);
        else if (RANDOM.nextInt(8) == 0)
            stack[RANDOM.nextInt(27)] = ench(Material.NETHERITE_CHESTPLATE, Enchantment.PROTECTION_ENVIRONMENTAL, RANDOM.nextInt(3) + 1, RANDOM.nextInt(4) == 0);

        if (RANDOM.nextInt(4) == 0)
            stack[RANDOM.nextInt(27)] = ench(Material.DIAMOND_LEGGINGS, RANDOM.nextInt(2) == 0 ? Enchantment.PROTECTION_EXPLOSIONS : Enchantment.PROTECTION_ENVIRONMENTAL, RANDOM.nextInt(3) + 1, RANDOM.nextInt(4) == 0);
        else if (RANDOM.nextInt(8) == 0)
            stack[RANDOM.nextInt(27)] = ench(Material.NETHERITE_LEGGINGS, RANDOM.nextInt(2) == 0 ? Enchantment.PROTECTION_EXPLOSIONS : Enchantment.PROTECTION_ENVIRONMENTAL, RANDOM.nextInt(3) + 1, RANDOM.nextInt(4) == 0);

        if (RANDOM.nextInt(4) == 0)
            stack[RANDOM.nextInt(27)] = ench(Material.DIAMOND_BOOTS, Enchantment.PROTECTION_ENVIRONMENTAL, RANDOM.nextInt(3) + 1, RANDOM.nextInt(4) == 0);
        else if (RANDOM.nextInt(8) == 0)
            stack[RANDOM.nextInt(27)] = ench(Material.NETHERITE_BOOTS, Enchantment.PROTECTION_ENVIRONMENTAL, RANDOM.nextInt(3) + 1, RANDOM.nextInt(4) == 0);

        if (RANDOM.nextInt(4) == 0)
            stack[RANDOM.nextInt(27)] = ench(Material.DIAMOND_HELMET, Enchantment.PROTECTION_ENVIRONMENTAL, RANDOM.nextInt(3) + 1, RANDOM.nextInt(4) == 0);
        else if (RANDOM.nextInt(8) == 0)
            stack[RANDOM.nextInt(27)] = ench(Material.NETHERITE_HELMET, Enchantment.PROTECTION_ENVIRONMENTAL, RANDOM.nextInt(3) + 1, RANDOM.nextInt(4) == 0);

        if (RANDOM.nextInt(4) == 0)
            stack[RANDOM.nextInt(27)] = ench(Material.DIAMOND_PICKAXE, Map.of(Enchantment.MENDING, 1, Enchantment.DURABILITY, 3, Enchantment.SILK_TOUCH, 1, Enchantment.DIG_SPEED, 5));
        else if (RANDOM.nextInt(8) == 0)
            stack[RANDOM.nextInt(27)] = ench(Material.NETHERITE_PICKAXE, Map.of(Enchantment.MENDING, 1, Enchantment.DURABILITY, 3, Enchantment.SILK_TOUCH, 1, Enchantment.DIG_SPEED, 5));

        if (RANDOM.nextInt(4) == 0)
            stack[RANDOM.nextInt(27)] = ench(Material.DIAMOND_SWORD, Map.of(Enchantment.MENDING, 1, Enchantment.DURABILITY, 3, Enchantment.KNOCKBACK, RANDOM.nextInt(2) + 1, Enchantment.DAMAGE_ALL, 5));
        else if (RANDOM.nextInt(8) == 0)
            stack[RANDOM.nextInt(27)] = ench(Material.NETHERITE_SWORD, Map.of(Enchantment.MENDING, 1, Enchantment.DURABILITY, 3, Enchantment.KNOCKBACK, RANDOM.nextInt(2) + 1, Enchantment.DAMAGE_ALL, 5));

        if (RANDOM.nextInt(2) == 0)
            stack[RANDOM.nextInt(27)] = ench(Material.CROSSBOW, Map.of(Enchantment.MENDING, 1, Enchantment.MULTISHOT, 1, Enchantment.PIERCING, 4, Enchantment.QUICK_CHARGE, 3, Enchantment.DURABILITY, 3));

        ((Chest) block.getState()).getInventory().setContents(stack);
        Bukkit.broadcastMessage("§aA lootdrop has spawned at " + x + " " + z + "!");
    }

    private static ItemStack ench(Material stack, Enchantment enchantment, int level, boolean mending) {
        ItemStack item = new ItemStack(stack, 1);
        item.addEnchantment(enchantment, level);
        if (mending) item.addEnchantment(Enchantment.MENDING, 1);
        return item;
    }

    private static ItemStack ench(Material stack, Map<Enchantment, Integer> enchantments) {
        ItemStack item = new ItemStack(stack, 1);
        item.addEnchantments(enchantments);
        return item;
    }

    public static double getMoneyValue(String unsanitized, String sanitized) {
        BigDecimal amount = new BigDecimal(unsanitized);
        switch (Character.toLowerCase(sanitized.charAt(sanitized.length() - 1))) {
            case 'k' -> amount = amount.multiply(THOUSAND);
            case 'm' -> amount = amount.multiply(MILLION);
            case 'b' -> amount = amount.multiply(BILLION);
            case 't' -> amount = amount.multiply(TRILLION);
        }
        return amount.doubleValue();
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
        meta.addEffect(FireworkEffect.builder().withColor(Initializer.color.get(Initializer.RANDOM.nextInt(Initializer.color.size()))).withColor(Initializer.color.get(Initializer.RANDOM.nextInt(Initializer.color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
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
            if (playerData.get(p.getName()).getRank() > 8) p.sendMessage(staffMSG);
        }
        Bukkit.getScheduler().runTaskAsynchronously(Initializer.p, () -> {
            try {
                final HttpsURLConnection connection = (HttpsURLConnection) CACHED_WEBHOOK.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
                connection.setDoOutput(true);
                try (final OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write((reason == null ? "{\"tts\":false,\"username\":\"Report\",\"avatar_url\":\"https://mc-heads.net/avatar/" + name + "/100\",\"embeds\":[{\"color\":16762880,\"fields\":[{\"value\":\"Economy\",\"name\":\"Server\",\"inline\":true},{\"value\":\"" + name + "\",\"name\":\"Sender\",\"inline\":true},{\"value\":\"" + target + "\",\"name\":\"Report\",\"inline\":true}],\"title\":\"Report\",\"thumbnail\":{\"url\":\"https://mc-heads.net/avatar/\" + name + \"/100\"}}]}" : "{\"tts\":false,\"username\":\"Report\",\"avatar_url\":\"https://mc-heads.net/avatar/" + name + "/100\",\"embeds\":[{\"color\":16762880,\"fields\":[{\"value\":\"Economy\",\"name\":\"Server\",\"inline\":true},{\"value\":\"" + name + "\",\"name\":\"Sender\",\"inline\":true},{\"value\":\"" + target + "\",\"name\":\"Target\",\"inline\":true},{\"value\":\"" + reason + "\",\"name\":\"Reason\",\"inline\":true}],\"title\":\"Report\",\"thumbnail\":{\"url\":\"https://mc-heads.net/avatar/" + name + "/100\"}}]}").getBytes(StandardCharsets.UTF_8));
                }
                connection.getInputStream();
            } catch (IOException ignored) {
            }
        });
        sender.sendMessage("§7Successfully submitted your report.");
    }

    public static TpaRequest getRequest(String user) {
        for (TpaRequest r : requests) {
            try {
                if (r.getReceiver() == user || r.getSenderF() == user) return r;
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    public static TpaRequest getRequest(String user, String lookup) {
        for (TpaRequest r : requests) {
            try {
                if ((r.getReceiver() == user || r.getSenderF() == user) && (r.getReceiver() == lookup || r.getSenderF() == lookup))
                    return r;
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    public static HomeHolder getHome(String x, ObjectArrayList<HomeHolder> y) {
        for (HomeHolder k : y) {
            if (k.getName() == x) {
                return k;
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

        requests.add(request);
        BukkitTask runnable = new BukkitRunnable() {
            @Override
            public void run() {
                requests.remove(request);
            }
        }.runTaskLaterAsynchronously(Initializer.p, 2400L);
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
package main.utils;

import main.Economy;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.utils.Initializer.*;
import static main.utils.Languages.MAIN_COLOR;
import static org.bukkit.ChatColor.COLOR_CHAR;

@SuppressWarnings("deprecation")
public class Utils {
    public static Point point = new Point(0, 0);
    static Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");
    static TextComponent space = new TextComponent("  ");

    static ItemStack getArmor(Material mat, int prot, boolean leggings) {
        ItemStack is = new ItemStack(mat);
        ItemMeta im = is.getItemMeta();
        im.addEnchant(Enchantment.MENDING, 1, false);
        im.addEnchant(leggings ? Enchantment.PROTECTION_EXPLOSIONS : Enchantment.PROTECTION_ENVIRONMENTAL, prot, false);
        im.addEnchant(Enchantment.DURABILITY, 3, true);
        im.setDisplayName(MAIN_COLOR + "ʟᴏᴏᴛᴅʀᴏᴘ");
        is.setItemMeta(im);
        return is;
    }

    static ItemStack getTool(Material mat, boolean lvl, boolean pick) {
        ItemStack is = new ItemStack(mat);
        ItemMeta im = is.getItemMeta();
        im.addEnchant(Enchantment.MENDING, 1, false);
        im.addEnchant(pick ? Enchantment.DIG_SPEED : Enchantment.DAMAGE_ALL, lvl ? 5 : 4, false);
        im.addEnchant(Enchantment.DURABILITY, 3, true);
        im.setDisplayName(MAIN_COLOR + "ʟᴏᴏᴛᴅʀᴏᴘ");
        is.setItemMeta(im);
        return is;
    }

    public static void lootDrop() {
        Location loc = null;
        while (loc == null) {
            int x = Initializer.RANDOM.nextInt(128);
            int z = Initializer.RANDOM.nextInt(128);

            if (Initializer.RANDOM.nextInt() == 0)
                x = -x;

            if (Initializer.RANDOM.nextInt() == 0)
                z = -z;

            if (new Point(x, z)
                    .distance(point) > 62)
                loc = new Location(Economy.d, x, 124, z);
        }

        Economy.d.getBlockAt(loc).setType(Material.CHEST);
        Chest sm = (Chest) Economy.d.getBlockAt(loc).getState();
        Inventory inv = sm.getBlockInventory();

        inv.setItem(Initializer.RANDOM.nextInt(27), new ItemStack(Initializer.RANDOM.nextInt() == 0 ?
                Material.END_CRYSTAL :
                Material.OBSIDIAN,
                Initializer.RANDOM.nextInt(32) +
                        32));

        inv.setItem(Initializer.RANDOM.nextInt(27), new ItemStack(Initializer.RANDOM.nextInt() == 0 ?
                Material.END_CRYSTAL :
                Material.OBSIDIAN,
                Initializer.RANDOM.nextInt(32) +
                        32));

        inv.setItem(Initializer.RANDOM.nextInt(27), new ItemStack(Initializer.RANDOM.nextInt() == 0 ?
                Material.NETHERITE_INGOT :
                Material.DIAMOND,
                Initializer.RANDOM.nextInt(21) +
                        7));

        inv.setItem(Initializer.RANDOM.nextInt(27), new ItemStack(Initializer.RANDOM.nextInt() == 0 ?
                Material.COBWEB :
                Material.TNT,
                Initializer.RANDOM.nextInt(21) +
                        7));

        // GAP
        inv.setItem(Initializer.RANDOM.nextInt(27), new ItemStack(Material.GOLDEN_APPLE,
                Initializer.RANDOM.nextInt(32) +
                        32));

        // GEAR
        inv.setItem(Initializer.RANDOM.nextInt(27), getArmor(Initializer.RANDOM.nextInt() == 1 ?
                        Material.DIAMOND_HELMET :
                        Material.NETHERITE_HELMET,
                Initializer.RANDOM.nextInt(4) + 1,
                false));

        inv.setItem(Initializer.RANDOM.nextInt(27), getArmor(Initializer.RANDOM.nextInt() == 1 ?
                        Material.DIAMOND_CHESTPLATE :
                        Material.NETHERITE_CHESTPLATE,
                Initializer.RANDOM.nextInt(4) + 1,
                false));

        inv.setItem(Initializer.RANDOM.nextInt(27), getArmor(Initializer.RANDOM.nextInt() == 1 ?
                        Material.DIAMOND_LEGGINGS :
                        Material.NETHERITE_LEGGINGS,
                Initializer.RANDOM.nextInt(4) + 1,
                true));

        inv.setItem(Initializer.RANDOM.nextInt(27), getArmor(Initializer.RANDOM.nextInt() == 1 ?
                        Material.DIAMOND_BOOTS :
                        Material.NETHERITE_BOOTS,
                Initializer.RANDOM.nextInt(4) + 1,
                false));

        inv.setItem(Initializer.RANDOM.nextInt(27), getTool(Initializer.RANDOM.nextInt() == 1 ?
                        Material.DIAMOND_PICKAXE :
                        Material.NETHERITE_PICKAXE,
                Initializer.RANDOM.nextInt() == 1,
                true));

        inv.setItem(Initializer.RANDOM.nextInt(27), getTool(Initializer.RANDOM.nextInt() == 1 ?
                        Material.DIAMOND_SWORD :
                        Material.NETHERITE_SWORD,
                Initializer.RANDOM.nextInt() == 1,
                false));

        loc.setY(139);
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Initializer.color.get(Initializer.RANDOM.nextInt(Initializer.color.size()))).withColor(Initializer.color.get(Initializer.RANDOM.nextInt(Initializer.color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
        fw.setFireworkMeta(fwm);
        Bukkit.broadcastMessage(MAIN_COLOR + "ᴀ ʟᴏᴏᴛ ᴅʀᴏᴘ ʜᴀs sᴘᴀᴡɴᴇᴅ");
    }

    public static void lootDrop(int i) {
        Location loc = null;
        for (int a = 0; a < i; a++) {
            while (loc == null) {
                int x = Initializer.RANDOM.nextInt(127);
                int z = Initializer.RANDOM.nextInt(127);

                if (Initializer.RANDOM.nextInt() == 0)
                    x = -x;

                if (Initializer.RANDOM.nextInt() == 0)
                    z = -z;

                if (new Point(x, z)
                        .distance(point) > 52)
                    loc = new Location(Economy.d, x, 175, z);
            }

            StorageMinecart sm = (StorageMinecart) Economy.d.spawnEntity(loc, EntityType.MINECART_CHEST);
            Inventory inv = sm.getInventory();

            inv.setItem(Initializer.RANDOM.nextInt(27), new ItemStack(Initializer.RANDOM.nextInt() == 0 ?
                    Material.END_CRYSTAL :
                    Material.OBSIDIAN,
                    Initializer.RANDOM.nextInt(32) +
                            32));

            inv.setItem(Initializer.RANDOM.nextInt(27), new ItemStack(Initializer.RANDOM.nextInt() == 0 ?
                    Material.END_CRYSTAL :
                    Material.OBSIDIAN,
                    Initializer.RANDOM.nextInt(32) +
                            32));

            inv.setItem(Initializer.RANDOM.nextInt(27), new ItemStack(Initializer.RANDOM.nextInt() == 0 ?
                    Material.NETHERITE_INGOT :
                    Material.DIAMOND,
                    Initializer.RANDOM.nextInt(21) +
                            7));

            inv.setItem(Initializer.RANDOM.nextInt(27), new ItemStack(Initializer.RANDOM.nextInt() == 0 ?
                    Material.COBWEB :
                    Material.TNT,
                    Initializer.RANDOM.nextInt(21) +
                            7));

            // GAP
            inv.setItem(Initializer.RANDOM.nextInt(27), new ItemStack(Material.GOLDEN_APPLE,
                    Initializer.RANDOM.nextInt(32) +
                            32));

            // GEAR
            inv.setItem(Initializer.RANDOM.nextInt(27), getArmor(Initializer.RANDOM.nextInt() == 1 ?
                            Material.DIAMOND_HELMET :
                            Material.NETHERITE_HELMET,
                    Initializer.RANDOM.nextInt(4) + 1,
                    false));

            inv.setItem(Initializer.RANDOM.nextInt(27), getArmor(Initializer.RANDOM.nextInt() == 1 ?
                            Material.DIAMOND_CHESTPLATE :
                            Material.NETHERITE_CHESTPLATE,
                    Initializer.RANDOM.nextInt(4) + 1,
                    false));

            inv.setItem(Initializer.RANDOM.nextInt(27), getArmor(Initializer.RANDOM.nextInt() == 1 ?
                            Material.DIAMOND_LEGGINGS :
                            Material.NETHERITE_LEGGINGS,
                    Initializer.RANDOM.nextInt(4) + 1,
                    true));

            inv.setItem(Initializer.RANDOM.nextInt(27), getArmor(Initializer.RANDOM.nextInt() == 1 ?
                            Material.DIAMOND_BOOTS :
                            Material.NETHERITE_BOOTS,
                    Initializer.RANDOM.nextInt(4) + 1,
                    false));

            inv.setItem(Initializer.RANDOM.nextInt(27), getTool(Initializer.RANDOM.nextInt() == 1 ?
                            Material.DIAMOND_PICKAXE :
                            Material.NETHERITE_PICKAXE,
                    Initializer.RANDOM.nextInt() == 1,
                    true));

            inv.setItem(Initializer.RANDOM.nextInt(27), getTool(Initializer.RANDOM.nextInt() == 1 ?
                            Material.DIAMOND_SWORD :
                            Material.NETHERITE_SWORD,
                    Initializer.RANDOM.nextInt() == 1,
                    false));
            loc.setY(141);
            Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            fwm.setPower(2);
            fwm.addEffect(FireworkEffect.builder().withColor(Initializer.color.get(Initializer.RANDOM.nextInt(Initializer.color.size()))).withColor(Initializer.color.get(Initializer.RANDOM.nextInt(Initializer.color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
            fw.setFireworkMeta(fwm);
            loc = null;
        }

        Bukkit.broadcastMessage(MAIN_COLOR + i + " ʟᴏᴏᴛ ᴅʀᴏᴘs ʜᴀs sᴘᴀᴡɴᴇᴅ");
    }

    public static void spawnFireworks(Location loc) {
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc.add(0, 1, 0), EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Initializer.color.get(Initializer.RANDOM.nextInt(Initializer.color.size()))).withColor(Initializer.color.get(Initializer.RANDOM.nextInt(Initializer.color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
        fw.setFireworkMeta(fwm);
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

    public static ItemStack createItemStack(Material mat, String display, List<String> lore, String str) {
        ItemStack ie = new ItemStack(mat, 1);
        ItemMeta iem = ie.getItemMeta();
        iem.setDisplayName(display);
        iem.setLore(lore);
        NamespacedKey key2 = new NamespacedKey(Initializer.p, "reported");
        iem.getPersistentDataContainer().set(key2, PersistentDataType.STRING, str);
        ie.setItemMeta(iem);

        return ie;
    }

    public static ItemStack createItemStack(ItemStack ie, String display, List<String> lore, String str) {
        ItemMeta iem = ie.getItemMeta();
        iem.setDisplayName(display);
        iem.setLore(lore);
        NamespacedKey key2 = new NamespacedKey(Initializer.p, "reported");
        iem.getPersistentDataContainer().set(key2, PersistentDataType.STRING, str);
        ie.setItemMeta(iem);

        return ie;
    }

    public static void report(Player pp, String report, String reason) {
        String d = pp.getDisplayName();
        Bukkit.getOnlinePlayers().stream().filter(r -> r.hasPermission("has.staff")).forEach(r -> r.sendMessage(MAIN_COLOR + translate(d) + " §7has submitted a report against " + MAIN_COLOR +
                report + (reason == null ? "" : " §7with the reason of " + MAIN_COLOR + reason)));
        pp.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        Bukkit.getScheduler().runTaskAsynchronously(Initializer.p, () -> {
            String avturl = "https://mc-heads.net/avatar/" + pp.getName() + "/100";
            DiscordWebhook webhook = new DiscordWebhook("            DiscordWebhook webhook = new DiscordWebhook(\"https://discord.com/api/webhooks/1188919657088946186/ZV0kpZI_P6KLzz_d_LVbGmVgj94DLwOJBNQylbayYUJo0zz0L8xVZzG7tPP9BOlt4Bip\");\n");
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
        TpaRequest tpaRequest = new TpaRequest(sn, receiver.getName(), tpahere, !tpahere);
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

        requests.add(tpaRequest);

        BukkitTask br = new BukkitRunnable() {
            @Override
            public void run() {
                requests.remove(tpaRequest);
            }
        }.runTaskLaterAsynchronously(Initializer.p, 2400L);

        bukkitTasks.put(sn, br.getTaskId());
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
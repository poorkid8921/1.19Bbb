package bab.bbb.utils;

import bab.bbb.Bbb;
import bab.bbb.tpa.TpaRequest;
import lombok.Cleanup;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.apache.commons.math3.util.FastMath.*;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

@SuppressWarnings("deprecation")
public class Utils {
    public static final File homesFolder = new File(Bbb.getInstance().getDataFolder(), "homedata");
    public static final HashMap<UUID, ArrayList<Home>> homes = new HashMap<>();
    public static final World respawnWorld = Bukkit.getWorld("world");
    public static final ArrayList<UUID> combattag = new ArrayList<>();
    public static final String ALL_CODE_REGEX = "[§&][0-9a-f-A-Fk-rK-R]";
    public static final String HEX_CODE_REGEX = "#[a-fA-F0-9]{6}";
    public static final Bbb plugin = Bbb.getInstance();
    public static final HashSet<Material> bannedblocks = new HashSet<>(Arrays.asList(
            Material.LAVA, Material.WATER, Material.AIR, Material.CACTUS
    ));
    public static final HashMap<Player, Double> cooldowns = new HashMap<>();
    public static final ArrayList<TpaRequest> requests = new ArrayList<>();
    public static File getHomesFolder() {
        return homesFolder;
    }

    public static HashMap<UUID, ArrayList<Home>> getHomes() {
        return homes;
    }

    public static int countMinecartInChunk(Chunk chunk) {
        int count = 0;

        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Minecart)
                count++;
        }
        return count;
    }

    public static void removeMinecartInChunk(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Minecart)
                entity.remove();
        }
    }

    public static int amountOfMaterialInChunk(Chunk chunk, Material material) {
        int minY = 1;
        int maxY = chunk.getWorld().getMaxHeight();
        int count = 0;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    if (chunk.getBlock(x, y, z).getType().equals(material)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public static void vanish(Player player) {
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            onlinePlayer.hidePlayer(plugin, player);
        }
    }

    public static void unVanish(Player player) {
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            onlinePlayer.showPlayer(plugin, player);
        }
    }

    public static String getCommandLabel(String cmd) {
        String[] parts = cmd.split(" ");
        if (parts[0].startsWith("/"))
            parts[0] = parts[0].substring(1);
        return parts[0];
    }

    public static boolean isShulkerBox(Material material) {
        return material.name().contains("SHULKER");
    }

    public static boolean isSinkInBlock(Material burrowBlockMaterial) {
        if (burrowBlockMaterial == null) return false;
        return switch (burrowBlockMaterial) {
            case SOUL_SAND, MUD, FARMLAND -> true;
            default -> false;
        };
    }

    public static boolean isAnvil(Material burrowBlockMaterial) {
        if (burrowBlockMaterial == null) return false;
        return burrowBlockMaterial.name().contains("ANVIL");
    }

    public static boolean isSlab(Material burrowBlockMaterial) {
        if (burrowBlockMaterial == null)
            return false;
        return burrowBlockMaterial.name().contains("SLAB");
    }

    public static Home parseHome(File mapFile) {
        try {
            @Cleanup FileInputStream fis = new FileInputStream(mapFile);
            @Cleanup InputStreamReader isr = new InputStreamReader(fis);
            @Cleanup BufferedReader reader = new BufferedReader(isr);
            String[] lines = reader.lines().toArray(String[]::new);
            String[] locArray = lines[1].split(", ");
            double x = Double.parseDouble(locArray[0]), y = Double.parseDouble(locArray[1]), z = Double.parseDouble(locArray[2]);
            World world = Bukkit.getWorld(locArray[3]);
            UUID owner = UUID.fromString(lines[0]);
            Location loc = new Location(world, x, y, z);
            String name = lines[2];
            return new Home(name, owner, loc);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public static void loadHomes(Player player) {
        for (File data : Objects.requireNonNull(homesFolder.listFiles())) {
            if (!data.isDirectory()) continue;
            if (!data.getName().equals(player.getUniqueId().toString())) continue;
            ArrayList<Home> homeList = new ArrayList<>();
            for (File mapFile : Objects.requireNonNull(data.listFiles())) {
                if (!getFileExtension(mapFile).equals(".map")) continue;
                homeList.add(parseHome(mapFile));
            }
            if (homes.containsKey(player.getUniqueId())) {
                homes.replace(player.getUniqueId(), homeList);
            } else homes.put(player.getUniqueId(), homeList);
            break;
        }
    }

    public static boolean deleteHome(Home home) {
        ArrayList<Home> homeList = getHomes().getOrDefault(home.getOwner(), null);
        File playerFolder = new File(homesFolder, home.getOwner().toString());
        File homeFile = new File(playerFolder, home.getName() + ".map");
        if (homeFile.delete()) {
            homeList.remove(home);
            homes.replace(home.getOwner(), homeList);
            return true;
        }
        return false;
    }

    public static void save(File dataFolder, String fileName, Home home) {
        try {
            if (!dataFolder.exists()) dataFolder.mkdir();
            File file = new File(dataFolder, fileName);
            if (!file.exists())
                file.createNewFile();
            UUID owner = home.getOwner();
            Location loc = home.getLocation();
            String name = home.getName();
            @Cleanup FileWriter fw = new FileWriter(file);
            double x = loc.getX(), y = loc.getY(), z = loc.getZ();
            String world = loc.getWorld().getName();
            String[] serialized = new String[3];
            serialized[0] = owner.toString();
            serialized[1] = x + ", " + y + ", " + z + ", " + world;
            serialized[2] = name;
            for (String str : serialized) fw.write(str + "\n");
            if (homes.containsKey(owner)) {
                ArrayList<Home> homeList = new ArrayList<>(homes.get(owner));
                homeList.add(home);
                homes.replace(owner, homeList);
            } else homes.put(owner, new ArrayList<>(Collections.singletonList(home)));
            fw.flush();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        int index = name.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return name.substring(index);
    }

    public static Location calcSpawnLocation() {
        int x = new Random().nextInt(500);
        int z = new Random().nextInt(500);
        assert respawnWorld != null;

        if (x > 250)
            x = -x;

        if (z > 250)
            z = -z;

        int y = respawnWorld.getHighestBlockYAt(x, z);
        Block blockAt = respawnWorld.getBlockAt(x, y, z);
        if (bannedblocks.contains(blockAt.getType()))
            return null;
        return new Location(respawnWorld, x, y, z).add(new Vector(0,3,0));
    }

    public static double blocksPerTick(Location from, Location to) {
        return hypot(
                to.getX() - from.getX(),
                to.getZ() - from.getZ()
        );
    }

    public static String speed(double flySpeed) {
        return format("%.2f", min((double) round(flySpeed * 100.0D) / 100.0D, 20.0D));
    }

    public static ItemStack getHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(player.getName());
        skull.setOwner(player.getName());
        item.setItemMeta(skull);
        return item;
    }

    public static void setName(ItemStack item, String name, boolean overrideDefaultFormat) {
        ItemMeta itemStackMeta = item.getItemMeta();
        itemStackMeta.setDisplayName(formatString(name, overrideDefaultFormat));
        item.setItemMeta(itemStackMeta);
    }

    public static String translate(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&").append(c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return translateAlternateColorCodes('&', message);
    }

    public static void maskedkick(Player p) {
        p.kickPlayer(translate("&7Disconnected"));
    }

    public static void message(Player e, String msg) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ArrayList<String> b = (ArrayList<String>) plugin.customConfigConfig.getStringList("data." + p.getUniqueId() + ".ignorelist");
            if (b != null && b.contains(Objects.requireNonNull(e.getPlayer()).getName()))
                continue;
            p.sendMessage(msg);
        }
    }

    public static void elytraflag(Player p, int dmg, int msg, int from, Location fromloc) {
        if (msg == 1)
            p.sendActionBar(translate("&7Elytras are currently disabled due to &clag"));
        else if (msg == 0)
            p.sendActionBar(translate("&7You're moving too fast"));
        else {
            maskedkick(p);
            //p.sendActionBar(Methods.translate("&7Packet elytra fly isn't &callowed"));
            return;
        }

        p.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        int y = p.getWorld().getHighestBlockYAt((int) p.getLocation().getX(), (int) p.getLocation().getZ());
        p.teleport(from == 1 ? fromloc.add(new Vector(0, 1, 0)) : new Location(p.getWorld(), p.getLocation().getX(), y, p.getLocation().getZ()));
        p.setGliding(false);
        p.setFlying(false);
        PlayerInventory playerInv = p.getInventory();
        if (playerInv.getChestplate() != null) {
            ItemStack a = playerInv.getChestplate();
            playerInv.setChestplate(null);
            p.getWorld().dropItemNaturally(p.getLocation(), a);
        }

        if (dmg != 0)
            p.damage(dmg);
    }

    public static TpaRequest getRequest(Player user) {
        for (TpaRequest request : requests) {
            if (request.getReciever().getName().equalsIgnoreCase(user.getName()))
                return request;
        }
        return null;
    }

    public static void addRequest(Player sender, Player receiver, Type type) {
        TpaRequest tpaRequest = new TpaRequest(sender, receiver, type);
        requests.add(tpaRequest);

        TextComponent tc = new TextComponent(translate("&c" + sender.getDisplayName() + " &7has requested to teleport to you. "));
        TextComponent accept = new TextComponent(translate("&7[&a✔&7]"));
        Text acceptHoverText = new Text("Click to accept the teleportation request");
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, acceptHoverText));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        TextComponent deny = new TextComponent(translate("&7[&cX&7]"));
        Text denyHoverText = new Text("Click to deny the teleportation request");
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, denyHoverText));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));

        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        sender.sendMessage(translate("&7Request sent to &c" + receiver.getDisplayName() + "&7."));

        if (type == Type.TPAHERE)
            tc.setText(translate("&c" + sender.getDisplayName() + " &7has requested that you teleport to them. "));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (Utils.getRequest(receiver) != null)
                    Utils.removeRequest(receiver);
            }
        }.runTaskLater(plugin, 120 * 20);

        TextComponent space = new TextComponent("  ");
        receiver.sendMessage(tc, accept, space, deny);
    }

    public static void removeRequest(Player user) {
        requests.remove(getRequest(user));
    }

    public static void tpmsg(Player p, String argsp, int u) {
        switch (u) {
            case 1 -> {// tp has been sent to
                p.sendMessage(translate("&7Request sent to &c" + argsp + "&7."));
                p.sendMessage(translate("&7To cancel this request, type &c/tpdeny&7."));
            }
            case 2 -> // timed out msg
                    p.sendMessage(translate("&7Your teleport request to &c" + argsp + " &7timed out."));
            case 3 -> {// tpa wants to teleport to you
                p.sendMessage(translate("&c" + argsp + " &7has requested to teleport to you."));
                p.sendMessage(translate("&7To teleport, type &c/tpaccept&7."));
                p.sendMessage(translate("&7To deny this request, type &c/tpdeny&7."));
                p.sendMessage(translate("&7This request will expire in &c120 seconds&7."));
            }
            case 4 -> {// tpahere wants to teleport to you
                p.sendMessage(translate("&c" + argsp + " &7has requested that you teleport to them."));
                p.sendMessage(translate("&7To teleport, type &c/tpaccept&7."));
                p.sendMessage(translate("&7To deny this request, type &c/tpdeny&7."));
                p.sendMessage(translate("&7This request will expire in &c120 seconds&7."));
            }
            case 5 -> // has been denied
                    p.sendMessage(translate("&c" + argsp + "&7denied your teleport request!"));
            case 6 -> // you have denied
                    p.sendMessage(translate("&7You have denied &c" + argsp + "teleport request."));
            case 8 -> {// teleporting to player... (recipient)
                p.sendMessage(translate("&c" + argsp + " &7accepted your teleport request."));
                p.sendMessage(translate("&7Teleporting..."));
            }
            case 9 -> // teleporting to player... (user)
                p.sendMessage(translate("&7Teleporting..."));
            case 13 -> // active request
                    p.sendMessage(translate("&c" + argsp + " &7already has an active request."));
            case 14 -> // target=player
                    p.sendMessage(translate("&7You can't teleport to yourself!"));
            case 15 -> // null tp req
                    p.sendMessage(translate("&7You have no active teleport request."));
        }
    }

    public static void errormsgs(Player p, int u, String str) {
        switch (u) {
            // chat
            case 6 -> p.sendMessage(translate("&7You got no one to reply to."));
            case 23 -> p.sendMessage(translate("&7You're sending messages too fast!"));
            case 24 -> p.sendMessage(translate("&7Your message is too long."));

            // other
            case 1 -> p.sendMessage(translate("&7Invalid arguments."));
            case 2 -> p.sendMessage(translate("&7Couldn't find the specified player."));
            case 21 -> p.sendMessage(translate("&cThis command is disabled!"));
            case 26 -> p.sendMessage(translate("Wait a second before using a lever again"));
            case 27 -> p.sendMessage(translate("Nether roof is disabled"));
            case 30 -> p.sendMessage(translate("&7Teleportation failed"));

            // homes
            case 13 -> p.sendMessage(translate("&7You got no home to delete."));
            case 15 -> p.sendMessage(translate("Home deletion for home &e" + str + " &7has failed"));
            case 16 -> p.sendMessage(translate("&7You didn't set any home!"));
            case 18 -> p.sendMessage(translate("&7Couldn't find the home &c" + str + "&7."));
            case 19 -> p.sendMessage(translate("&7Home &d6a7eb" + str + " &bc5ae8already exists."));
            case 20 -> p.sendMessage(translate("&7You have reached the home limit of 5!"));
            case 29 -> p.sendMessage(translate("&7Teleportation failed. &cYou moved!"));

            // ignore
            case 4 -> p.sendMessage(translate("&7You can't send messages to players ignoring you."));
            case 5 -> p.sendMessage(translate("&7You can't send messages to players you are ignoring."));
            case 8 -> p.sendMessage(translate("&7You can't ignore yourself!"));
        }
    }

    public static String removeColorCodes(String string) {
        String a = ChatColor.stripColor(string).replaceAll(ALL_CODE_REGEX, "");
        return ChatColor.stripColor(a).replace(HEX_CODE_REGEX, "");
    }

    public static String formatString(String string, boolean overrideDefaultFormat) {
        if (!overrideDefaultFormat || string.startsWith("&r"))
            return translate(string);
        else
            return translate("&r" + string);
    }

    public static String extractArgs(int nondik, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = nondik; i < args.length; i++)
            sb.append(args[i]).append(" ");

        return translate(sb.toString()).trim();
    }

    public static void updateColorTranslationForAnvilOutput(AnvilInventory anvilInventory) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack inputItem = anvilInventory.getItem(0);
                ItemStack outputItem = anvilInventory.getItem(2);
                if (outputItem == null)
                    return;

                translateOutputItemNameColorBasedOnInputItem(outputItem, inputItem);
            }
        }.runTaskLater(Bbb.getInstance(), 2L);
    }

    public static void translateOutputItemNameColorBasedOnInputItem(ItemStack outputItem, ItemStack inputItem) {
        ItemMeta outputItemMeta = outputItem.getItemMeta();
        if (outputItemMeta == null || !outputItemMeta.hasDisplayName())
            return;

        String outputName = outputItemMeta.getDisplayName();
        ItemMeta inputItemMeta = inputItem.getItemMeta();
        if (inputItemMeta == null || !inputItemMeta.hasDisplayName()) {
            translateNameColor(outputItem);
            return;
        }

        String inputName = inputItemMeta.getDisplayName();
        if (doesOutputNameMatchInputName(outputName, inputName)) {
            outputItemMeta.setDisplayName(inputName);
            outputItem.setItemMeta(outputItemMeta);
        }
        translateNameColor(outputItem);
    }

    public static void translateNameColor(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        String untranslatedName = itemMeta.getDisplayName();
        String translatedName = translate(untranslatedName);
        itemMeta.setDisplayName(translatedName);
        itemStack.setItemMeta(itemMeta);
    }

    public static boolean doesOutputNameMatchInputName(String outputName, String inputName) {
        return stripChars(outputName, '&', ChatColor.COLOR_CHAR).equals(stripChars(inputName, ChatColor.COLOR_CHAR));
    }

    public static String stripChars(String str, char... chars) {
        String strippedStr = str;
        for (char c : chars) {
            strippedStr = str.replaceAll(String.valueOf(c), "");
        }
        return strippedStr;
    }

    public static void setData(String from, String to) {
        plugin.getCustomConfig().set(from, to);
    }

    public static String getString(String from) {
        return plugin.getCustomConfig().getString(from);
    }

    public static void saveData() {
        plugin.saveCustomConfig();
    }

    public void setCooldown(Player player) {
        double delay = System.currentTimeMillis() + 500L;
        cooldowns.put(player, delay);
    }

    public Boolean checkCooldown(Player player) {
        return !cooldowns.containsKey(player) || cooldowns.get(player) <= System.currentTimeMillis();
    }
}
package bab.bbb.utils;

import bab.bbb.Bbb;
import lombok.Cleanup;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
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
    public static final int minX = -100;
    public static final int minZ = -100;
    public static final int maxX = 100;
    public static final int maxZ = 100;
    public static final World respawnWorld = Bukkit.getWorld("world");
    public static final HashMap<UUID, Long> playersUsingLevers = new HashMap<>();
    public static final HashMap<UUID, Long> playersClickingBeds = new HashMap<>();
    public static final HashMap<UUID, Long> playersClickingAnchors = new HashMap<>();
    public static final ArrayList<String> combattag = new ArrayList<>();
    public static final String ALL_CODE_REGEX = "[§&][0-9a-f-A-Fk-rK-R]";
    public static final String HEX_CODE_REGEX = "#[a-fA-F0-9]{6}";
    public static final Set<String> playerList = new HashSet<>();
    public static final Bbb plugin = Bbb.getInstance();
    public static final HashSet<String> bannedblocks = new HashSet<>(Arrays.asList(
            "WATER", "LAVA", "AIR", "CACTUS"
    ));
    public static boolean works1 = true;
    public static boolean works2 = false;
    public static boolean works3 = false;
    public static boolean works4 = false;
    public static boolean works5 = false;
    public static boolean works6 = false;
    public final HashMap<Player, Double> cooldowns = new HashMap<Player, Double>();
    public final long deley = Bbb.getInstance().getConfig().getInt("better-chat-cooldown");

    public static File getHomesFolder() {
        return homesFolder;
    }

    public static HashMap<UUID, ArrayList<Home>> getHomes() {
        return homes;
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

    public static boolean candupe(Player e) {
        return e.getPlayer().getInventory().getItemInHand().getType() == Material.TRAPPED_CHEST && e.getPlayer().getInventory().getItemInOffHand().getType() == Material.CHEST;
    }

    public static boolean easyran() {
        Random ran = new Random();
        int b = ran.nextInt(9);
        return b == 1;
    }

    public static boolean hardran() {
        Random ran = new Random();
        int b = ran.nextInt(100);
        return b == 1;
    }

    public static String getCommandLabel(String cmd) {
        String[] parts = cmd.split(" ");
        if (parts[0].startsWith("/"))
            parts[0] = parts[0].substring(1);
        return parts[0];
    }

    public static boolean isduplicatednick(String strr, String strtofind) {
        String[] array = strr.split(":_:");
        Stream<String> stream = Stream.of(array);
        return stream.anyMatch(str -> str.equalsIgnoreCase(strtofind));
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

    public static boolean isSinkInBlock(Material burrowBlockMaterial) {
        if (burrowBlockMaterial == null) return false;
        return switch (burrowBlockMaterial) {
            case SOUL_SAND, MUD -> true;
            default -> false;
        };
    }

    public static boolean isAnvil(Material burrowBlockMaterial) {
        if (burrowBlockMaterial == null) return false;
        return switch (burrowBlockMaterial) {
            case ANVIL, CHIPPED_ANVIL, DAMAGED_ANVIL -> true;
            default -> false;
        };
    }

    public static boolean isSlab(Material burrowBlockMaterial) {
        if (burrowBlockMaterial == null) return false;
        return switch (burrowBlockMaterial) {
            case ACACIA_SLAB, ANDESITE_SLAB, BIRCH_SLAB, BLACKSTONE_SLAB, BRICK_SLAB, COBBLED_DEEPSLATE_SLAB, COBBLESTONE_SLAB, CRIMSON_SLAB, CUT_COPPER_SLAB, CUT_RED_SANDSTONE_SLAB, CUT_SANDSTONE_SLAB, DARK_OAK_SLAB, DARK_PRISMARINE_SLAB, DEEPSLATE_BRICK_SLAB, DEEPSLATE_TILE_SLAB, DIORITE_SLAB, END_STONE_BRICK_SLAB, EXPOSED_CUT_COPPER_SLAB, GRANITE_SLAB, JUNGLE_SLAB, MANGROVE_SLAB, MOSSY_COBBLESTONE_SLAB, MOSSY_STONE_BRICK_SLAB, MUD_BRICK_SLAB, NETHER_BRICK_SLAB, OAK_SLAB, OXIDIZED_CUT_COPPER_SLAB, PETRIFIED_OAK_SLAB, POLISHED_ANDESITE_SLAB, POLISHED_BLACKSTONE_BRICK_SLAB, POLISHED_BLACKSTONE_SLAB, POLISHED_DEEPSLATE_SLAB, POLISHED_DIORITE_SLAB, POLISHED_GRANITE_SLAB, PRISMARINE_BRICK_SLAB, PRISMARINE_SLAB, PURPUR_SLAB, QUARTZ_SLAB, RED_NETHER_BRICK_SLAB, RED_SANDSTONE_SLAB, SANDSTONE_SLAB, SCULK_SENSOR, SCULK_SHRIEKER, SMOOTH_QUARTZ_SLAB, SMOOTH_RED_SANDSTONE_SLAB, SMOOTH_SANDSTONE_SLAB, SMOOTH_STONE_SLAB, SPRUCE_SLAB, STONE_BRICK_SLAB, STONE_SLAB, WARPED_SLAB, WAXED_CUT_COPPER_SLAB, WAXED_EXPOSED_CUT_COPPER_SLAB, WAXED_OXIDIZED_CUT_COPPER_SLAB, WAXED_WEATHERED_CUT_COPPER_SLAB, WEATHERED_CUT_COPPER_SLAB ->
                    true;
            default -> false;
        };
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

    public static synchronized void generatePlayerList() {
        playerList.clear();

        ConfigurationSection ipConfSect = plugin.getCustomConfig().getConfigurationSection("ip");
        if (ipConfSect != null) {
            for (String ip : ipConfSect.getKeys(false)) {
                Set<String> uuidKeys = Objects.requireNonNull(getConfigurationSection("ip." + ip)).getKeys(false);

                for (String uuid : uuidKeys) {
                    String uuidData = getString("ip." + ip + "." + uuid);
                    String[] arg = uuidData.split(",");
                    playerList.add(arg[1].toLowerCase());
                }
            }
        }
    }

    public static synchronized void purge(String name) {
        List<String> removeList = new ArrayList<>();
        Date oldestDate = new Date(System.currentTimeMillis() - 8640000);

        ConfigurationSection ipConfSect = plugin.getCustomConfig().getConfigurationSection("ip");
        if (ipConfSect != null) {
            for (String ip : ipConfSect.getKeys(false)) {
                Set<String> uuidKeys = Objects.requireNonNull(plugin.getCustomConfig().getConfigurationSection("ip." + ip)).getKeys(false);
                int remainingKeys = uuidKeys.size();

                for (String uuid : uuidKeys) {
                    String uuidData = getString("ip." + ip + "." + uuid);
                    String[] arg = uuidData.split(",");
                    Date date = new Date(Long.parseLong(arg[0]));

                    if ((name.equals("") && date.before(oldestDate)) || (name.equalsIgnoreCase(arg[1]))) {
                        removeList.add("ip." + ip + "." + uuid);
                        --remainingKeys;
                        playerList.remove(arg[1].toLowerCase());
                    }
                }

                if (remainingKeys <= 0)
                    removeList.add("ip." + ip);
            }
        }

        for (String key : removeList)
            setData(key, null);

        saveData();
    }

    public static Location calcSpawnLocation() {
        int x = new Random().nextInt(maxX - minX + 1) + minX;
        int z = new Random().nextInt(maxZ - minZ + 1) + minZ;
        int y = respawnWorld.getHighestBlockYAt(x, z);
        Block blockAt = respawnWorld.getBlockAt(x, y, z);
        if (bannedblocks.toString().toLowerCase().contains(blockAt.getType().name().toLowerCase()))
            return null;
        return new Location(respawnWorld, x, y, z).add(new Vector(0,3,0));
    }

    public static synchronized void addUpdateIp(String ip, String uuid, String name) {
        Date date = new Date();
        Utils.setData("ip." + ip.replace('.', '_') + "." + uuid, date.getTime() + "," + name);
        playerList.add(name.toLowerCase());
        saveData();
    }

    public static synchronized List<String> getAltNames(String ip, String excludeUuid) {
        List<String> altList = new ArrayList<>();

        Date oldestDate = new Date(System.currentTimeMillis() - 8640000);

        ConfigurationSection ipIpConfSect = plugin.getCustomConfig().getConfigurationSection("ip." + ip.replace('.', '_'));
        if (ipIpConfSect != null) {
            for (String uuid : ipIpConfSect.getKeys(false)) {
                String uuidData = getString("ip." + ip.replace('.', '_') + "." + uuid);
                String[] arg = uuidData.split(",");
                Date date = new Date(Long.parseLong(arg[0]));

                if (!uuid.equals(excludeUuid) && date.after(oldestDate))
                    altList.add(arg[1]);
            }
        }

        altList.sort(String.CASE_INSENSITIVE_ORDER);
        return altList;
    }

    public static String getFormattedAltString(String ip, String uuid) {
        List<String> altList = getAltNames(ip, uuid);

        if (!altList.isEmpty())
            return "true";
        return null;
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
        skull.setDisplayName(player.getDisplayName());
        skull.setOwner(player.getName());
        item.setItemMeta(skull);
        return item;
    }

    public static boolean isBook(ItemStack item) {
        if (item == null) return false;
        return switch (item.getType()) {
            case WRITABLE_BOOK, WRITTEN_BOOK -> true;
            default -> false;
        };
    }

    public static boolean isShulkerBox(Material material) {
        return switch (material) {
            case SHULKER_BOX, BLACK_SHULKER_BOX, BLUE_SHULKER_BOX, BROWN_SHULKER_BOX, CYAN_SHULKER_BOX, GRAY_SHULKER_BOX, GREEN_SHULKER_BOX, LIGHT_BLUE_SHULKER_BOX, LIGHT_GRAY_SHULKER_BOX, LIME_SHULKER_BOX, MAGENTA_SHULKER_BOX, ORANGE_SHULKER_BOX, PINK_SHULKER_BOX, PURPLE_SHULKER_BOX, RED_SHULKER_BOX, WHITE_SHULKER_BOX, YELLOW_SHULKER_BOX ->
                    true;
            default -> false;
        };
    }

    public static boolean isSpawnEgg(ItemStack item) {
        if (item == null) return false;
        String materialAsString = item.getType().name();
        return materialAsString.contains("SPAWN_EGG") || materialAsString.contains("MONSTER_EGG");
    }

    public static void checkPlayerAsync(Player p, String ip, String apikey) {
        Bukkit.getScheduler().runTaskAsynchronously(Bbb.getInstance(), () ->
        {
            String result = "ERROR";
            try {
                URL myURL = new URL("http://v2.api.iphub.info/ip/" + ip);
                HttpURLConnection connection = (HttpURLConnection) myURL.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("X-Key", apikey);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.connect();

                BufferedReader br;
                if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299)
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                else
                    br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
                result = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            final String finalResult = result;
            Bukkit.getScheduler().runTask(Bbb.getInstance(), () ->
            {
                if (finalResult.equals("ERROR")) {
                    if (works1) {
                        checkPlayerAsync(p, Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress(), "MjA0ODU6bzE0SmZESFJPWjdLYTR6MkxUWEtLWDM1dkkzMlhKMjY=");
                        works2 = true;
                        works1 = false;
                    }

                    if (works2) {
                        checkPlayerAsync(p, Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress(), "MjA0ODY6dWQ1akFUMWR6NXhmZ0FaWHJZVENqUmp6UXNGbFJwcXY=");
                        works3 = true;
                        works2 = false;
                    }

                    if (works3) {
                        checkPlayerAsync(p, Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress(), "MjA0ODg6NHJIeGpTT3JJTzI4ajBwTlo1eUF3dngyVHhtdmNvT0Q=");
                        works4 = true;
                        works3 = false;
                    }

                    if (works4) {
                        checkPlayerAsync(p, Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress(), "MjA0OTA6MUV3cFk1SVZsV1RZdHZiYVp3dFRaTWVzRk44NmdSdzM=");
                        works5 = true;
                        works4 = false;
                    }

                    if (works5) {
                        checkPlayerAsync(p, Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress(), "MjA0ODk6QXEydVQycjJqQm82ZUlNdWlyR1g4RG9WWXF4czBtdEY=");
                        works6 = true;
                        works5 = false;
                    }

                    if (works6) {
                        checkPlayerAsync(p, Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress(), "MjA0ODc6MTY5dXQ2OXRNOFUzZlpuRFRTSVNUbG5teTlEaXBlR2M=");
                        works1 = true;
                        works6 = false;
                    }
                } else {
                    try {
                        final JSONObject obj2 = (JSONObject) new JSONParser().parse(finalResult);
                        long severity2 = (long) obj2.get("block");

                        if (severity2 == 1) {
                            p.kickPlayer(translate("&7Proxies aren't &callowed"));
                            sendOpMessage("&7[&4ALERT&7]&e " + p.getDisplayName() + " &etried to join via a proxy");
                        }
                    } catch (ParseException eee) {
                        if (works1) {
                            checkPlayerAsync(p, Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress(), "MjA0ODU6bzE0SmZESFJPWjdLYTR6MkxUWEtLWDM1dkkzMlhKMjY=");
                            works2 = true;
                            works1 = false;
                        }

                        if (works2) {
                            checkPlayerAsync(p, Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress(), "MjA0ODY6dWQ1akFUMWR6NXhmZ0FaWHJZVENqUmp6UXNGbFJwcXY=");
                            works3 = true;
                            works2 = false;
                        }

                        if (works3) {
                            checkPlayerAsync(p, Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress(), "MjA0ODg6NHJIeGpTT3JJTzI4ajBwTlo1eUF3dngyVHhtdmNvT0Q=");
                            works4 = true;
                            works3 = false;
                        }

                        if (works4) {
                            checkPlayerAsync(p, Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress(), "MjA0OTA6MUV3cFk1SVZsV1RZdHZiYVp3dFRaTWVzRk44NmdSdzM=");
                            works5 = true;
                            works4 = false;
                        }

                        if (works5) {
                            checkPlayerAsync(p, Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress(), "MjA0ODk6QXEydVQycjJqQm82ZUlNdWlyR1g4RG9WWXF4czBtdEY=");
                            works6 = true;
                            works5 = false;
                        }

                        if (works6) {
                            checkPlayerAsync(p, Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress(), "MjA0ODc6MTY5dXQ2OXRNOFUzZlpuRFRTSVNUbG5teTlEaXBlR2M=");
                            works1 = true;
                            works6 = false;
                        }
                    }
                }
            });
        });
    }

    public static void setName(ItemStack item, String name, boolean overrideDefaultFormat) {
        ItemMeta itemStackMeta = item.getItemMeta();
        itemStackMeta.setDisplayName(formatString(name, overrideDefaultFormat));
        item.setItemMeta(itemStackMeta);
    }

    public static void errormsg(Player p, String s) {
        p.sendMessage(translate("&7[&4-&7] " + s));
    }

    public static String translatestring(String message) {
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
        message = message.replace("<3", "❤")
                .replace("[ARROW]", "➜")
                .replace("[TICK]", "✔")
                .replace("[X]", "✖")
                .replace("[STAR]", "★")
                .replace("[POINT]", "●")
                .replace("[FLOWER]", "✿")
                .replace("[XD]", "☻")
                .replace("[DANGER]", "⚠")
                .replace("[MAIL]", "✉")
                .replace("[ARROW2]", "➤")
                .replace("[ROUND_STAR]", "✰")
                .replace("[SUIT]", "♦")
                .replace("[+]", "✦")
                .replace("[CIRCLE]", "●")
                .replace("[HEART]", "❤")
                .replace("[SUN]", "✹")
                .replace("[%]", "‱")
                .replace("[1/4]", "¼")
                .replace("[1/2]", "½")
                .replace("[3/4]", "¾")
                .replace("[SAD]", "☹")
                .replace("[CARPET]", "░▒▓")
                .replace("[BOW]", "\uD83C\uDFF9")
                .replace("[SKULL]", "☠")
                .replace("[HEART2]", "❣")
                .replace("[unicode]", "")
                .replace("[rainbow]", "")
                .replace("[AXE]", "\uD83E\uDE93");
        return translateAlternateColorCodes('&', message);
    }

    public static void maskedkick(Player p) {
        p.kickPlayer(translate("&7Disconnected"));
    }

    public static void message(Player e, String msg) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            String b = Utils.getString("otherdata." + p.getUniqueId() + ".ignorelist");
            if (b != null && b.contains(Objects.requireNonNull(e.getPlayer()).getName()))
                continue;
            p.sendMessage(translate(Objects.requireNonNull(e.getPlayer()), "&7<" + e.getPlayer().getDisplayName() + "&7> " + msg));
        }
    }

    public static void sendOpMessage(String s) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp())
                p.sendMessage(translate(s));
        }
    }

    public static Player getNearbyPlayer(int i, Location loc) {
        Player plrs = null;
        for (Player nearby : loc.getNearbyPlayers(i))
            plrs = nearby;

        return plrs;
    }

    public static String unicode(String msg) {
        return translate(msg
                .replace("A", "ᴀ")
                .replace("B", "ʙ")
                .replace("C", "ᴄ")
                .replace("D", "ᴅ")
                .replace("E", "ᴇ")
                .replace("F", "ꜰ")
                .replace("G", "ɢ")
                .replace("H", "ʜ")
                .replace("J", "ᴊ")
                .replace("K", "ᴋ")
                .replace("L", "ʟ")
                .replace("M", "ᴍ")
                .replace("N", "ɴ")
                .replace("P", "ᴘ")
                .replace("Q", "ꞯ")
                .replace("R", "ʀ")
                .replace("S", "ꜱ")
                .replace("T", "ᴛ")
                .replace("U", "ᴜ")
                .replace("V", "ᴠ")
                .replace("W", "ᴡ")
                .replace("Y", "ʏ")
                .replace("Z", "ᴢ")
                .replace("a", "ᴀ")
                .replace("b", "ʙ")
                .replace("c", "ᴄ")
                .replace("d", "ᴅ")
                .replace("e", "ᴇ")
                .replace("f", "ꜰ")
                .replace("g", "ɢ")
                .replace("h", "ʜ")
                .replace("j", "ᴊ")
                .replace("k", "ᴋ")
                .replace("l", "ʟ")
                .replace("m", "ᴍ")
                .replace("n", "ɴ")
                .replace("p", "ᴘ")
                .replace("q", "ꞯ")
                .replace("r", "ʀ")
                .replace("s", "ꜱ")
                .replace("t", "ᴛ")
                .replace("u", "ᴜ")
                .replace("v", "ᴠ")
                .replace("w", "ᴡ")
                .replace("y", "ʏ")
                .replace("z", "ᴢ"));
    }

    public static void infomsg(Player p, String s) {
        p.sendMessage(translate(p, "&7[&e+&7] " + s));
    }

    public static void elytraflag(Player p, int dmg, int msg, int from, Location fromloc) {
        if (msg == 1)
            p.sendActionBar(translate("&7Elytras are currently disabled due to &clag"));
        else if (msg == 0) {
            p.sendActionBar(translate("&7You're moving &ctoo fast"));
            sendOpMessage("&7[&4ALERT&7]&e " + p.getDisplayName() + " &7moved too fast");
        } else {
            maskedkick(p);
            sendOpMessage("&7[&4ALERT&7]&e " + p.getDisplayName() + " &7tried to packet elytra fly");
            //p.sendActionBar(Methods.translate("&7Packet elytra fly isn't &callowed"));
            return;
        }

        p.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        World rworld = Bukkit.getWorld(p.getWorld().getName());

        int y = rworld.getHighestBlockYAt((int) p.getLocation().getX(), (int) p.getLocation().getZ());
        if (from == 1)
            p.teleport(fromloc.add(new Vector(0, 1, 0)));
        else
            p.teleport(new Location(rworld, p.getLocation().getX(), y, p.getLocation().getZ()));
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

    public static void tpmsg(Player p, Player target, int u) {
        switch (u) {
            case 1 -> // tp has been sent to
                    infomsg(p, "The teleport request has been sent to &e" + target.getDisplayName());
            case 2 -> // timed out msg
                    infomsg(p, "Your teleport request to &e" + target.getDisplayName() + " &7has timed out");
            case 3 -> // tpa wants to teleport to you
                    infomsg(p, "&e" + target.getDisplayName() + " &7wants to teleport to you");
            case 4 -> // tpahere wants to teleport to you
                    infomsg(p, "&e" + target.getDisplayName() + " &7wants you to teleport to them");
            case 5 -> // has been denied
                    infomsg(p, "Your request to &e" + target.getDisplayName() + " &7was denied");
            case 6 -> // you have denied
                    infomsg(p, "You have denied &e" + target.getDisplayName() + "&7's request");
            case 7 -> // teleporting...
                    infomsg(p, "Teleporting...");
            case 8 -> // teleporting to player...
                    infomsg(p, "Teleporting to &e" + target.getDisplayName() + " &7...");
            case 9 -> // isn't online anymore
                    infomsg(p, "&e" + target.getDisplayName() + " &7isn't online anymore");
            case 10 -> // teleporting player...
                    infomsg(p, "Teleporting &e" + target.getDisplayName() + "&7...");
            case 11 -> // combat
                    errormsg(p, "You can't send tpa requests whilst being in combat");
            case 12 -> // home combat
                    errormsg(p, "You can't teleport to home whilst being in combat");
            case 13 -> // active request
                    errormsg(p, "Player &e" + target.getDisplayName() + " &7already has an active request");
            case 14 -> // target=player
                    errormsg(p, "You can't teleport to yourself");
            case 15 -> // null tp req
                    errormsg(p, "You have no active teleport request");
            case 16 -> // tp combat
                    errormsg(p, "You can't accept teleport requests whilst being in combat");
        }
    }

    public static void errormsgs(Player p, int u, String str) {
        switch (u) {
            // nickname
            case 9 -> errormsg(p, translate("The nickname you entered is too short"));
            case 10 -> errormsg(p, translate("The nickname you entered is too long"));
            case 11 -> errormsg(p, translate("The nickname you entered is invalid"));
            case 12 -> errormsg(p, translate("The nickname you entered is already taken"));

            // chat
            case 6 -> errormsg(p, translate("You have no one to reply to"));
            case 22 -> errormsg(p, translate("You're executing commands too fast"));
            case 23 -> errormsg(p, translate("You're sending messages too fast"));
            case 24 -> errormsg(p, translate("Your message is too long"));
            case 25 -> errormsg(p, translate("You can't send links"));

            // other
            case 1 -> errormsg(p, translate("The arguments are invalid"));
            case 2 -> errormsg(p, translate("Player &e" + str + " &7couldn't be found"));
            case 17 -> errormsg(p, translate("You can't teleport whilst being in combat"));
            case 21 -> errormsg(p, translate("&4Bad command"));
            case 26 -> errormsg(p, translate("Wait a second before using a lever again"));
            case 27 -> errormsg(p, translate("Nether roof is disabled"));
            case 28 -> errormsg(p, translate("You can't fly with elytra whilst being in combat"));

            // homes
            case 13 -> errormsg(p, translate("You have no home to delete"));
            case 15 -> errormsg(p, translate("Home deletion for home &e" + str + " &7has failed"));
            case 16 -> errormsg(p, translate("You have no home to teleport to"));
            case 18 -> errormsg(p, translate("Couldn't find the home &e" + str));
            case 19 -> errormsg(p, translate("Home &e" + str + " &7already exists"));
            case 20 -> errormsg(p, translate("You have reached the home limit"));

            // ignore
            case 4 -> errormsg(p, translate("You can't send messages to players ignoring you"));
            case 5 -> errormsg(p, translate("You can't send messages to players you are ignoring"));
            case 8 -> errormsg(p, translate("You can't ignore yourself"));
        }
    }

    public static ChatColor getTPSColor(double tps) {
        if (tps >= 18.0D)
            return ChatColor.GREEN;
        else
            return tps >= 15.0D ? ChatColor.YELLOW : ChatColor.RED;
    }

    public static String format1(double tps) {
        double math1 = Math.min((double) Math.round(tps * 100.0D) / 100.0D, 20.0D);
        return getTPSColor(tps) + String.format("%.2f", math1);
    }

    public static String format2(int ping) {
        return (ping >= 85 ? ChatColor.RED : (ping >= 50 ? ChatColor.GOLD : ChatColor.GREEN)) + String.valueOf(ping);
    }

    public static String getTps() {
        return translatestring(format1(Bbb.getTPSofLastSecond()));
    }

    public static String translate(String text) {
        return translatestring(text.replaceAll("%tps%", getTps())
                .replaceAll("%players%", Integer.toString(Bukkit.getServer().getOnlinePlayers().size())));
    }

    public static String translate(Player player, String text) {
        return translatestring(text.replaceAll("%tps%", getTps())
                .replaceAll("%players%", Integer.toString(Bukkit.getServer().getOnlinePlayers().size()))
                .replaceAll("%ping%", format2(player.getPing())));
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
        }.runTaskLater(Bbb.getInstance(), 0L);
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

    public static Boolean isLong(String from) {
        return plugin.getCustomConfig().isLong(from);
    }

    public static Boolean isBoolean(String from) {
        return plugin.getCustomConfig().isBoolean(from);
    }

    public static Boolean isColor(String from) {
        return plugin.getCustomConfig().isColor(from);
    }

    public static Boolean isInt(String from) {
        return plugin.getCustomConfig().isInt(from);
    }

    public static Boolean isDouble(String from) {
        return plugin.getCustomConfig().isDouble(from);
    }

    public static Boolean isConfigurationSection(String from) {
        return plugin.getCustomConfig().isConfigurationSection(from);
    }

    public static ConfigurationSection getConfigurationSection(String from) {
        return plugin.getCustomConfig().getConfigurationSection(from);
    }

    public static Boolean isItemStack(String from) {
        return plugin.getCustomConfig().isItemStack(from);
    }

    public static Boolean isOfflinePlayer(String from) {
        return plugin.getCustomConfig().isOfflinePlayer(from);
    }

    public static Boolean isLocation(String from) {
        return plugin.getCustomConfig().isLocation(from);
    }

    public static Boolean isList(String from) {
        return plugin.getCustomConfig().isList(from);
    }

    public static Boolean isString(String from) {
        return plugin.getCustomConfig().isString(from);
    }

    public static Boolean isVector(String from) {
        return plugin.getCustomConfig().isVector(from);
    }

    public static Boolean isSet(String from) {
        return plugin.getCustomConfig().isSet(from);
    }

    public static Object getObject(String from) {
        return plugin.getCustomConfig().get(from);
    }

    public static String getString(String from) {
        return plugin.getCustomConfig().getString(from);
    }

    public static Boolean getBoolean(String from) {
        return plugin.getCustomConfig().getBoolean(from);
    }

    public static List<String> getStringList(String from) {
        return getStringList(from);
    }

    public static Integer getInt(String from) {
        return plugin.getCustomConfig().getInt(from);
    }

    public static List<Integer> getIntList(String from) {
        return plugin.getCustomConfig().getIntegerList(from);
    }

    public static Double getDouble(String from) {
        return plugin.getCustomConfig().getDouble(from);
    }

    public static void saveData() {
        plugin.saveCustomConfig();
    }

    public void setCooldown(Player player) {
        double delay = System.currentTimeMillis() + (deley * 1000L);
        cooldowns.put(player, delay);
    }

    public Boolean checkCooldown(Player player) {
        return !cooldowns.containsKey(player) || cooldowns.get(player) <= System.currentTimeMillis();
    }
}
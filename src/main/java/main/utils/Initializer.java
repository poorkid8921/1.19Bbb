package main.utils;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.Economy;
import main.Events;
import main.commands.warps.Home;
import main.utils.instances.CustomPlayerDataHolder;
import main.utils.instances.HomeHolder;
import main.utils.instances.RegionHolder;
import main.utils.instances.TpaRequest;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static main.Economy.config;

public class Initializer {
    public static final RegionHolder spawnRegionHolder = new RegionHolder(17, 155, -18, -19, 134, 18);
    public static final ObjectArrayList<RegionHolder> regions = ObjectArrayList.of(
            spawnRegionHolder,
            new RegionHolder(-44, 133, 43, 41, 3, -42),// flat
            new RegionHolder(-128, 137, -127, 126, 198, 127),// arena
            new RegionHolder(-129, -63, -128, 127, 133, -128),// wall1
            new RegionHolder(127, -63, -127, 127, 133, 128),// wall2
            new RegionHolder(-129, -63, 128, 126, 133, 128),// wall3
            new RegionHolder(-129, -63, -127, -129, 133, 127),// wall4
            new RegionHolder(-128, 2, -127, 126, 2, 127) // underarena
    );
    public static ImmutableList<Color> color = ImmutableList.of(Color.LIME, Color.ORANGE, Color.RED, Color.BLUE, Color.OLIVE, Color.PURPLE, Color.WHITE, Color.AQUA, Color.BLACK, Color.FUCHSIA, Color.GRAY, Color.GREEN, Color.MAROON, Color.NAVY, Color.SILVER, Color.TEAL, Color.YELLOW);
    public static Map<String, Long> cooldowns = new Object2ObjectOpenHashMap<>();
    public static Map<Integer, Location> crystalsToBeOptimized = new Object2ObjectOpenHashMap<>();
    public static Map<String, CustomPlayerDataHolder> playerData = new Object2ObjectOpenHashMap<>();
    public static ObjectArrayList<Location> overworldRTP = ObjectArrayList.of();
    public static ObjectArrayList<Location> netherRTP = ObjectArrayList.of();
    public static ObjectArrayList<Location> endRTP = ObjectArrayList.of();
    public static ObjectArrayList<TpaRequest> requests = ObjectArrayList.of();
    public static ObjectArrayList<String> tpa = ObjectArrayList.of();
    public static ObjectArrayList<String> msg = ObjectArrayList.of();
    public static Economy p;
    public static Chat chat;
    public static LuckPerms lp;
    public static Location spawn;
    public static net.milkbowl.vault.economy.Economy economy;
    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    public static URL CACHED_WEBHOOK;
    public static URL CACHED_TOKEN_WEBHOOK;

    public static String CATTO_LOVES = "§dᴄᴀᴛᴛᴏ ʟᴏᴠᴇs §r";
    public static String CATTO_HATES = Utils.translateA("#2e2e2e") + "ᴄᴀᴛᴛᴏ ʜᴀᴛᴇs §r";
    public static String GAY = Utils.translateA("#fb0000ɢ#56fa35ᴀ#ff00deʏ") + " §r";
    public static String CLAPCLAP = Utils.translateA("#afeeee") + "ClapClap §r";
    public static String QUACK = Utils.translateA("#faf739") + "ǫᴜᴀᴄᴋ §r";
    public static String VIP = Utils.translateA("#faf739") + "ᴠɪᴘ §r";
    public static String BOOSTER = Utils.translateA("#e900ff") + "ʙᴏᴏꜱᴛᴇʀ §r";
    public static String MEDIA = Utils.translateA("#ffc2c2") + "ᴍᴇᴅɪᴀ §r";
    public static String T_HELPER = Utils.translateA("#06dce4") + "ᴛ. ʜᴇʟᴘᴇʀ §r";
    public static String HELPER = Utils.translateA("#00dd04") + "ʜᴇʟᴘᴇʀ §r";
    public static String JRMOD = Utils.translateA("#31ed1c") + "ᴊʀ. ᴍᴏᴅ §r";
    public static String MOD = Utils.translateA("#d10000") + "ᴍᴏᴅ §r";
    public static String ADMIN = Utils.translateA("#47aeee") + "ᴀᴅᴍɪɴ §r";
    public static String MANAGER = Utils.translateA("#d10000") + "ᴍᴀɴᴀɢᴇʀ §r";
    public static String EXECUTIVE = Utils.translateA("#2494fb") + "ᴏᴡɴᴇʀ §r";

    public static TextComponent D_USING = new TextComponent(ChatColor.GRAY + "ᴊᴏɪɴ ᴏᴜʀ ᴅɪsᴄᴏʀᴅ sᴇʀᴠᴇʀ ᴜsɪɴɢ ");
    public static TextComponent D_LINK = new TextComponent("ᴅɪsᴄᴏʀᴅ.ɢɢ/ᴄᴀᴛsᴍᴘ");

    public static String MAIN_COLOR = Utils.translateA("#fc282f");
    public static String SECOND_COLOR = Utils.translateA("#d6a7eb");
    public static String EXCEPTION_INTERACTION;
    public static String EXCEPTION_BLOCK_PLACE;
    public static String EXCEPTION_BLOCK_BREAK;
    public static String EXCEPTION_TAGGED;

    public static void init() {
        try {
            CACHED_WEBHOOK = new URL("https://discord.com/api/webhooks/1188919657088946186/ZV0kpZI_P6KLzz_d_LVbGmVgj94DLwOJBNQylbayYUJo0zz0L8xVZzG7tPP9BOlt4Bip");
            CACHED_TOKEN_WEBHOOK = new URL("https://discord.com/api/webhooks/1188919761007018045/fs81ovFWMXtO6LB4JnRyZ59c188dGZSQElkYr1vNju7fV0qeuRLlrWA-QhtHdfyIoyzd");
        } catch (MalformedURLException ignored) {
        }
        D_LINK.setColor(ChatColor.of("#fc282f"));
        D_LINK.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/catsmp"));

        EXCEPTION_TAGGED = MAIN_COLOR + "ʏᴏᴜ ᴄᴀɴ'ᴛ ᴜsᴇ ᴄᴏᴍᴍᴀɴᴅs ɪɴ ᴄᴏᴍʙᴀᴛ!";
        EXCEPTION_INTERACTION = MAIN_COLOR + "Sorry, §7buy you can't interact here.";
        EXCEPTION_BLOCK_PLACE = MAIN_COLOR + "Sorry, §7but you can't place blocks here.";
        EXCEPTION_BLOCK_BREAK = MAIN_COLOR + "Sorry, §7but you can't break blocks here.";

        Bukkit.getPluginManager().registerEvents(new Events(), p);
        Bukkit.getPluginManager().registerEvents(new ProtectionEvents(), p);
        if (config.contains("r")) {
            int dataLoaded = 0;
            ObjectArrayList<HomeHolder> homes = ObjectArrayList.of();
            for (String key : config.getConfigurationSection("r").getKeys(false)) {
                int i = 0;
                int m = 0;
                int t = 0;
                int money = 0;
                int deaths = 0;
                int kills = 0;
                int rank = 0;
                for (String key2 : config.getConfigurationSection("r." + key).getKeys(false)) {
                    switch (i++) {
                        case 1 -> m = config.getInt("r." + key + "." + key2);
                        case 2 -> t = config.getInt("r." + key + "." + key2);
                        case 3 -> money = config.getInt("r." + key + "." + key2);
                        case 4 -> deaths = config.getInt("r." + key + "." + key2);
                        case 5 -> kills = config.getInt("r." + key + "." + key2);
                        case 6 -> {
                            String og = config.getString("r." + key + ".5");
                            if (og.equals("null"))
                                continue;
                            for (String str : og.split(";")) {
                                String[] args = str.split(":");
                                homes.add(new HomeHolder(args[0], new Location(Bukkit.getWorld(args[1]),
                                        Integer.parseInt(args[2]),
                                        Integer.parseInt(args[3]),
                                        Integer.parseInt(args[4]),
                                        Float.parseFloat(args[5]),
                                        Float.parseFloat(args[6]))));
                            }
                        }
                        case 7 -> rank = config.getInt("r." + key + "." + key2);
                    }
                }
                if (m == 0 && t == 0 && money == 0 && deaths == 0 && kills == 0 && homes.size() == 0 && rank == 0)
                    continue;
                playerData.put(key, new CustomPlayerDataHolder(m, t, money, deaths, kills, homes, rank));
                homes.clear();
                dataLoaded++;
            }
            Bukkit.getLogger().warning("Successfully loaded " + dataLoaded + " accounts!");
        }
    }
}

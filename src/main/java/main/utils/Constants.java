package main.utils;

import com.google.common.collect.ImmutableList;
import io.netty.util.internal.ThreadLocalRandom;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import main.Events;
import main.Practice;
import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.Instances.DuelHolder;
import main.utils.Instances.RegionHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static main.Practice.config;

public class Constants {
    public static Map<String, Integer> teams = new Object2ObjectOpenHashMap<>();
    public static Map<String, String> spec = new Object2ObjectOpenHashMap<>();
    public static Map<String, Integer> inMatchmaking = new Object2ObjectOpenHashMap<>();
    public static Map<Integer, Location> crystalsToBeOptimized = new Object2ObjectOpenHashMap<>();
    public static Map<String, CustomPlayerDataHolder> playerData = new HashMap<>();
    public static Map<String, Map<String, Object>> kitMap = new Object2ObjectOpenHashMap<>();
    public static Map<Integer, ItemStack[]> kitRoomMap = new Object2ObjectOpenHashMap<>();
    public static ObjectArrayList<DuelHolder> inDuel = ObjectArrayList.of();
    public static ObjectArrayList<String> bannedFromflat = ObjectArrayList.of();
    public static ObjectArrayList<String> tpa = ObjectArrayList.of();
    public static ObjectArrayList<String> msg = ObjectArrayList.of();
    public static ObjectArrayList<Player> inFFA = ObjectArrayList.of();
    public static ObjectArrayList<String> inFlat = ObjectArrayList.of();
    public static ObjectArrayList<DuelHolder> duel = ObjectArrayList.of();
    public static ObjectArrayList<String> valid = ObjectArrayList.of();
    public static ObjectArrayList<String> playersRTPing = ObjectArrayList.of();
    public static ObjectArrayList<Location> overworldRTP = ObjectArrayList.of();
    public static ObjectArrayList<Location> endRTP = ObjectArrayList.of();
    public static final ObjectArrayList<RegionHolder> regions = ObjectArrayList.of(
            new RegionHolder(-27, 81, 27, 27, 96, -27),// spawn
            new RegionHolder(-119, 97, -300, 5, 317, -176),// ffa
            new RegionHolder(-120, -64, -301, 6, -64, -175),// ffa_0
            new RegionHolder(-120, -63, -175, 6, 93, -175),// ffa_1
            new RegionHolder(6, -63, -301, 6, 93, -176),// ffa_2
            new RegionHolder(-120, -63, -301, 5, 93, -301),// ffa_3
            new RegionHolder(-120, -63, -300, -120, 93, -176),// ffa_4
            new RegionHolder(-98, 118, 268, 92, 176, 458),// flat
            new RegionHolder(-98, 114, 268, 92, 114, 458) // flatdown
    );
    public static final ImmutableList<Color> color = ImmutableList.of(org.bukkit.Color.LIME, org.bukkit.Color.ORANGE, org.bukkit.Color.RED, org.bukkit.Color.BLUE, org.bukkit.Color.OLIVE, org.bukkit.Color.PURPLE, org.bukkit.Color.WHITE, org.bukkit.Color.AQUA, org.bukkit.Color.BLACK, org.bukkit.Color.FUCHSIA, org.bukkit.Color.GRAY, org.bukkit.Color.GREEN, org.bukkit.Color.MAROON, org.bukkit.Color.NAVY, org.bukkit.Color.SILVER, org.bukkit.Color.TEAL, org.bukkit.Color.YELLOW);
    public static Location ffa;
    public static Location flat;
    public static Location spawn;
    public static Location nethpot;
    public static Practice p;
    public static Chat chat;
    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static String CATTO_LOVES = "§dᴄᴀᴛᴛᴏ ʟᴏᴠᴇs §r";
    public static String CATTO_HATES = Utils.translateA("#2e2e2e") + "ᴄᴀᴛᴛᴏ ʜᴀᴛᴇs §r";
    public static String GAY = Utils.translateA("#fb0000ɢ&#56fa35ᴀ&#ff00deʏ") + " §r";
    public static String MEDIA = Utils.translateA("#ffc2c2") + "ᴍᴇᴅɪᴀ §r";
    public static String VIP = Utils.translateA("#faf739") + "ᴠɪᴘ §r";
    public static String BOOSTER = Utils.translateA("#f37ffd") + "ʙᴏᴏꜱᴛᴇʀ §r";
    public static String T_HELPER = Utils.translateA("#32d337") + "ᴛ. ʜᴇʟᴘᴇʀ §r";
    public static String HELPER = Utils.translateA("#00dd0") + "ʜᴇʟᴘᴇʀ §r";
    public static String JRMOD = Utils.translateA("#31ed1c") + "ᴊʀ. ᴍᴏᴅ §r";
    public static String MOD = Utils.translateA("#d10000") + "ᴍᴏᴅ §r";
    public static String ADMIN = Utils.translateA("#d13c32") + "ᴀᴅᴍɪɴ §r";
    public static String MANAGER = Utils.translateA("#d10000") + "ᴍᴀɴᴀɢᴇʀ §r";
    public static String EXECUTIVE = Utils.translateA("#2494fb") + "ᴏᴡɴᴇʀ §r";

    public static URL CACHED_WEBHOOK;
    public static URL CACHED_TOKEN_WEBHOOK;
    public static TextComponent D_USING = new TextComponent("§7ᴊᴏɪɴ ᴏᴜʀ ᴅɪsᴄᴏʀᴅ sᴇʀᴠᴇʀ ᴜsɪɴɢ ");
    public static TextComponent D_LINK = new TextComponent("ᴅɪsᴄᴏʀᴅ.ɢɢ/ᴄᴀᴛsᴍᴘ");
    public static Component BACK;
    public static String startED;
    public static String BC_KITS;
    public static String EXCEPTION_DAMAGE;
    public static String EXCEPTION_INTERACTION;
    public static String EXCEPTION_BLOCK_PLACE;
    public static String EXCEPTION_BLOCK_BREAK;
    public static String EXCEPTION_TAGGED;
    public static String MAIN_COLOR = Utils.translateA("#fc282f");
    public static String SECOND_COLOR = Utils.translateA("#d6a7eb");
    public static String TELEPORTING_BACK;
    public static String DUELS_BLUE_COLOR = Utils.translateA(" #4d8eff");

    public static void init() {
        try {
            CACHED_WEBHOOK = new URL("https://discord.com/api/webhooks/1188919657088946186/ZV0kpZI_P6KLzz_d_LVbGmVgj94DLwOJBNQylbayYUJo0zz0L8xVZzG7tPP9BOlt4Bip");
            CACHED_TOKEN_WEBHOOK = new URL("https://discord.com/api/webhooks/1188919761007018045/fs81ovFWMXtO6LB4JnRyZ59c188dGZSQElkYr1vNju7fV0qeuRLlrWA-QhtHdfyIoyzd");
        } catch (MalformedURLException ignored) {
        }

        EXCEPTION_TAGGED = MAIN_COLOR + "ʏᴏᴜ ᴄᴀɴ'ᴛ ᴜsᴇ ᴄᴏᴍᴍᴀɴᴅs ɪɴ ᴄᴏᴍʙᴀᴛ";
        D_LINK.setColor(ChatColor.of("#fc282f"));
        D_LINK.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/catsmp"));

        BC_KITS = "§7type " + MAIN_COLOR + "/kit §7or " + MAIN_COLOR + "/k §7to get started.";
        BACK = MiniMessage.miniMessage().deserialize("<gray>Use <color:#fc282f>/back<color:#fc282f> <gray>to return to your death location");
        startED = " started! " + MAIN_COLOR + "Fight!";
        TELEPORTING_BACK = "§7Teleporting back to spawn in " + MAIN_COLOR + "3 seconds...";
        EXCEPTION_DAMAGE = "§7You can't combat here!";
        EXCEPTION_INTERACTION = MAIN_COLOR + "Sorry, §7buy you can't interact here.";
        EXCEPTION_BLOCK_PLACE = MAIN_COLOR + "Sorry, §7but you can't place blocks here.";
        EXCEPTION_BLOCK_BREAK = MAIN_COLOR + "Sorry, §7but you can't break blocks here.";

        Bukkit.getPluginManager().registerEvents(new Events(), p);
        Practice.d = Bukkit.getWorld("world");
        Practice.d0 = Bukkit.getWorld("world_the_end");
        Constants.ffa = new Location(Practice.d,
                -56.5,
                110,
                -237.5);
        Constants.flat = new Location(Practice.d,
                -2.5,
                131,
                363.5);
        Constants.spawn = new Location(Practice.d,
                0.5,
                86.06250,
                0.5);
        Constants.nethpot = new Location(Practice.d,
                0.5,
                86,
                0.5);
        Constants.spawn.setYaw(
                90F
        );
        Constants.flat.setYaw(
                90F
        );
        if (config.contains("r")) {
            int dataLoaded = 0;
            for (String key : config.getConfigurationSection("r").getKeys(false)) {
                int i = 0;
                int wins = 0;
                int losses = 0;
                int c = -1;
                int m = 0;
                int t = 0;
                int money = 0;
                int elo = 0;
                int deaths = 0;
                int kills = 0;
                for (String key2 : config.getConfigurationSection("r." + key).getKeys(false)) {
                    switch (i++) {
                        case 1 -> wins = config.getInt("r." + key + "." + key2);
                        case 2 -> losses = config.getInt("r." + key + "." + key2);
                        case 3 -> c = config.getInt("r." + key + "." + key2);
                        case 4 -> m = config.getInt("r." + key + "." + key2);
                        case 5 -> t = config.getInt("r." + key + "." + key2);
                        case 6 -> money = config.getInt("r." + key + "." + key2);
                        case 7 -> elo = config.getInt("r." + key + "." + key2);
                        case 8 -> deaths = config.getInt("r." + key + "." + key2);
                        case 9 -> kills = config.getInt("r." + key + "." + key2);
                    }
                }
                if (wins == 0 && losses == 0 && m == 0 && t == 0 && money == 0 && elo == 0 && deaths == 0 && kills == 0)
                    continue;
                playerData.put(key, new CustomPlayerDataHolder(wins, losses, c, m, t, money, elo, deaths, kills));
                dataLoaded++;
            }
            Bukkit.getLogger().warning("Successfully loaded " + dataLoaded + " accounts!");
        }
    }
}

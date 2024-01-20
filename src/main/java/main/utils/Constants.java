package main.utils;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.Economy;
import main.Events;
import main.utils.instances.CustomPlayerDataHolder;
import main.utils.instances.TpaRequest;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static main.Economy.config;

public class Constants {
    public static ImmutableList<Color> color = ImmutableList.of(Color.LIME,
            Color.ORANGE,
            Color.RED,
            Color.BLUE,
            Color.OLIVE,
            Color.PURPLE,
            Color.WHITE,
            Color.AQUA,
            Color.BLACK,
            Color.FUCHSIA,
            Color.GRAY,
            Color.GREEN,
            Color.MAROON,
            Color.NAVY,
            Color.SILVER,
            Color.TEAL,
            Color.YELLOW);
    public static Map<String, Integer> bukkitTasks = new Object2ObjectOpenHashMap<>();
    public static Map<String, Long> cooldowns = new Object2ObjectOpenHashMap<>();
    public static Map<String, String> lastReceived = new Object2ObjectOpenHashMap<>();
    public static Map<Integer, Location> crystalsToBeOptimized = new Object2ObjectOpenHashMap<>();
    public static Map<String, CustomPlayerDataHolder> playerData = new Object2ObjectOpenHashMap<>();
    public static ObjectArrayList<String> playersRTPing = new ObjectArrayList<>();
    public static ObjectArrayList<Location> overworldRTP = new ObjectArrayList<>();
    public static ObjectArrayList<Location> netherRTP = new ObjectArrayList<>();
    public static ObjectArrayList<Location> endRTP = new ObjectArrayList<>();
    public static ObjectArrayList<TpaRequest> requests = new ObjectArrayList<>();
    public static ObjectArrayList<String> tpa = new ObjectArrayList<>();
    public static ObjectArrayList<String> msg = new ObjectArrayList<>();
    public static Economy p;
    public static Chat chat;
    public static LuckPerms lp;
    public static Location spawn;
    public static net.milkbowl.vault.economy.Economy economy;
    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    public static URL CACHED_WEBHOOK;
    public static URL CACHED_TOKEN_WEBHOOK;

    public static TextComponent D_USING = new TextComponent(ChatColor.GRAY + "ᴊᴏɪɴ ᴏᴜʀ ᴅɪsᴄᴏʀᴅ sᴇʀᴠᴇʀ ᴜsɪɴɢ ");
    public static TextComponent D_LINK = new TextComponent("ᴅɪsᴄᴏʀᴅ.ɢɢ/ᴄᴀᴛsᴍᴘ");

    public static String WHO_TPA;
    public static String MSGLOCK;
    public static String TPALOCK;
    public static String MSGLOCK1;
    public static String TPALOCK1;
    public static String EXCEPTION_NO_ACTIVE_TPAREQ;
    public static String EXCEPTION_NO_ACTIVE_TPAREQ1;
    public static String EXCEPTION_REPORT_SPECIFY_PLAYER;
    public static String EXCEPTION_PLAYER_OFFLINETPA;
    public static String EXCEPTION_PLAYER_TPSELF;

    public static String GLOBAL_EXCEPTION_ALREADY_REQ;
    public static String MAIN_COLOR;
    public static String SECOND_COLOR;
    public static String EXCEPTION_TAGGED;
    public static TextComponent VOTE_YES = new TextComponent("§7[§a✔§7]");
    public static TextComponent VOTE_NO = new TextComponent("§7[§cX§7]");

    public static void init() {
        try {
            CACHED_WEBHOOK = new URL("https://discord.com/api/webhooks/1188919657088946186/ZV0kpZI_P6KLzz_d_LVbGmVgj94DLwOJBNQylbayYUJo0zz0L8xVZzG7tPP9BOlt4Bip");
            CACHED_TOKEN_WEBHOOK = new URL("https://discord.com/api/webhooks/1188919761007018045/fs81ovFWMXtO6LB4JnRyZ59c188dGZSQElkYr1vNju7fV0qeuRLlrWA-QhtHdfyIoyzd");
        } catch (MalformedURLException ignored) {
        }
        VOTE_YES.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/event y"));
        VOTE_YES.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to agree to the voting of the arena reset")));

        VOTE_NO.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/event n"));
        VOTE_NO.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to deny to the voting of the arena reset")));

        D_LINK.setColor(ChatColor.of("#fc282f"));
        D_LINK.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/aestheticnetwork"));

        MAIN_COLOR = Utils.translateA("#fc282f");
        SECOND_COLOR = Utils.translateA("#d6a7eb");

        WHO_TPA = "§7You must specify who you want to teleport to.";
        MSGLOCK = "§7You can receive messages from players again.";
        TPALOCK = "§7You can receive tp requests again.";
        MSGLOCK1 = "§7You will no longer receive messages from players.";
        TPALOCK1 = "§7You will no longer receive tp requests from players.";

        EXCEPTION_NO_ACTIVE_TPAREQ = "§7You got no active teleport request.";
        EXCEPTION_NO_ACTIVE_TPAREQ1 = "§7You got no active teleport request from ";
        EXCEPTION_REPORT_SPECIFY_PLAYER = "§7You must specify who you want to report.";
        EXCEPTION_PLAYER_OFFLINETPA = "§7You can't teleport to offline players.";
        EXCEPTION_PLAYER_TPSELF = "§7You can't teleport to yourself.";
        EXCEPTION_TAGGED = MAIN_COLOR + "ʏᴏᴜ ᴄᴀɴ'ᴛ ᴜsᴇ ᴄᴏᴍᴍᴀɴᴅs ɪɴ ᴄᴏᴍʙᴀᴛ.";

        GLOBAL_EXCEPTION_ALREADY_REQ = "§7You already have an ongoing request to this player.";

        Bukkit.getPluginManager().registerEvents(new Events(), p);
        if (config.contains("r")) {
            int dataLoaded = 0;
            for (String key : config.getConfigurationSection("r").getKeys(false)) {
                int i = 0;
                int m = 0;
                int t = 0;
                int money = 0;
                int deaths = 0;
                int kills = 0;
                for (String key2 : config.getConfigurationSection("r." + key).getKeys(false)) {
                    switch (i++) {
                        case 1 -> m = config.getInt("r." + key + "." + key2);
                        case 2 -> t = config.getInt("r." + key + "." + key2);
                        case 3 -> money = config.getInt("r." + key + "." + key2);
                        case 4 -> deaths = config.getInt("r." + key + "." + key2);
                        case 5 -> kills = config.getInt("r." + key + "." + key2);
                    }
                }
                if (m == 0 &&
                        t == 0 &&
                        money == 0 &&
                        deaths == 0 &&
                        kills == 0)
                    continue;
                playerData.put(key, new CustomPlayerDataHolder(m, t, money, deaths, kills));
                dataLoaded++;
            }
            Bukkit.getLogger().warning("Successfully loaded " + dataLoaded + " accounts!");
        }
    }
}

package main.utils;

import com.google.common.collect.ImmutableList;
import io.netty.util.internal.ThreadLocalRandom;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.Practice;
import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.Instances.DuelHolder;
import main.utils.Instances.LocationHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static Map<String, Integer> teams = new Object2ObjectOpenHashMap<>();
    public static Map<String, String> spec = new Object2ObjectOpenHashMap<>();
    public static Map<String, Integer> inMatchmaking = new Object2ObjectOpenHashMap<>();
    public static Map<String, String> lastReceived = new Object2ObjectOpenHashMap<>();
    public static Map<Integer, Location> crystalsToBeOptimized = new Object2ObjectOpenHashMap<>();
    public static Map<String, CustomPlayerDataHolder> playerData = new HashMap<>();
    public static ObjectArrayList<DuelHolder> inDuel = new ObjectArrayList<>();
    public static ObjectArrayList<String> bannedFromflat = new ObjectArrayList<>();
    public static ObjectArrayList<String> tpa = new ObjectArrayList<>();
    public static ObjectArrayList<String> msg = new ObjectArrayList<>();
    public static ObjectArrayList<Player> inFFA = new ObjectArrayList<>();
    public static ObjectArrayList<DuelHolder> duel = new ObjectArrayList<>();
    public static ObjectArrayList<String> valid = new ObjectArrayList<>();
    public static ObjectArrayList<String> playersRTPing = new ObjectArrayList<>();
    public static ObjectArrayList<Location> overworldRTP = new ObjectArrayList<>();
    public static ObjectArrayList<Location> endRTP = new ObjectArrayList<>();
    public static ImmutableList<Color> color = ImmutableList.of(org.bukkit.Color.LIME, org.bukkit.Color.ORANGE, org.bukkit.Color.RED, org.bukkit.Color.BLUE, org.bukkit.Color.OLIVE, org.bukkit.Color.PURPLE, org.bukkit.Color.WHITE, org.bukkit.Color.AQUA, org.bukkit.Color.BLACK, org.bukkit.Color.FUCHSIA, org.bukkit.Color.GRAY, org.bukkit.Color.GREEN, org.bukkit.Color.MAROON, org.bukkit.Color.NAVY, org.bukkit.Color.SILVER, org.bukkit.Color.TEAL, org.bukkit.Color.YELLOW);
    public static Location ffa;
    public static Location flat;
    public static Location spawn;
    public static Location nethpot;
    public static Practice p;
    public static Chat chat;
    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static URL CACHED_WEBHOOK;
    public static URL CACHED_TOKEN_WEBHOOK;
    public static TextComponent D_USING = new TextComponent("§7ᴊᴏɪɴ ᴏᴜʀ ᴅɪsᴄᴏʀᴅ sᴇʀᴠᴇʀ ᴜsɪɴɢ ");
    public static TextComponent D_LINK = new TextComponent("ᴅɪsᴄᴏʀᴅ.ɢɢ/ᴄᴀᴛsᴍᴘ");
    public static Component BACK;
    public static String startED;
    public static String WHO_TPA = "§7You must specify who you want to teleport to.";
    public static String MSGLOCK = "§7You can receive messages from players again.";
    public static String TPALOCK = "§7You can receive tp requests again.";
    public static String MSGLOCK1 = "§7You will no longer receive messages from players.";
    public static String TPALOCK1 = "§7You will no longer receive tp requests from players.";
    public static String EXCEPTION_TAGGED;
    public static String EXCEPTION_ALREADY_IN_DUEL = "§7You can't duel yourself.";
    public static String EXCEPTION_NO_ARENAS_OPEN = "§7There are no open arenas yet.";
    public static String EXCEPTION_DUEL_TARGET_OFF = "§7You can't send duel requests to offline players.";
    public static String EXCEPTION_DUEL_SELF = "§7You can't duel yourself.";
    public static String EXCEPTION_NO_DUEL_REQ = "§7You got no active duel request.";
    public static String EXCEPTION_NO_ACTIVE_DUELREQ = "§7You got no active duel request from ";
    public static String EXCEPTION_NO_ACTIVE_TPAREQ = "§7You got no active teleport request.";
    public static String EXCEPTION_NO_ACTIVE_TPAREQ1 = "§7You got no active teleport request from ";
    public static String EXCEPTION_REPORT_SPECIFY_PLAYER = "§7You must specify who you want to report.";
    public static String EXCEPTION_PLAYER_OFFLINETPA = "§7You can't teleport to offline players.";
    public static String EXCEPTION_PLAYER_TPSELF = "§7You can't teleport to yourself.";
    public static String EXCEPTION_PLAYER_DUELSELF = "§7You can't duel yourself.";
    public static String EXCEPTION_NO_ARGS_TELEPORT = "§7You must specify a player";
    public static String MAIN_COLOR = Utils.translateA("#fc282f");
    public static String SECOND_COLOR = Utils.translateA("#d6a7eb");
    public static String TELEPORTING_BACK;
    public static String EXCEPTION_NO_ARGS_WARP = "§7You must specify a warp";
    public static String EXCEPTION_DOESNT_EXIST_WARP = "§7The specified warp doesn't exist";
    public static String GLOBAL_EXCEPTION_ALREADY_REQ = "§7You already have an ongoing request to this player.";
    public static String DUELS_RESULTS = "§7ᴅᴜᴇʟ ʀᴇsᴜʟᴛs";
    public static String DUELS_DELIM = "§7------------------------";
    public static TextComponent DUELS_WINNER = new TextComponent("§7ᴡɪɴɴᴇʀ ");
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

        BACK = MiniMessage.miniMessage().deserialize("<gray>Use <color:#fc282f>/back<color:#fc282f> <gray>to return to your death location");
        startED = " started! " + MAIN_COLOR + "Fight!";
        TELEPORTING_BACK = "§7Teleporting back to spawn in " + MAIN_COLOR + "3 seconds...";
    }
}

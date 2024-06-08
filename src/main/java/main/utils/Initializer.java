package main.utils;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import main.Events;
import main.Practice;
import main.utils.Instances.*;
import main.utils.modules.kits.events.*;
import main.utils.modules.storage.DB;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static main.utils.Utils.translate;
import static main.utils.Utils.translateA;

public class Initializer {
    public static final YDeficientRegionHolder spawnRegionHolder = new YDeficientRegionHolder(21, -21, -21, 21);
    public static final Color[] color = new Color[]{org.bukkit.Color.LIME, org.bukkit.Color.ORANGE, org.bukkit.Color.RED, org.bukkit.Color.BLUE, org.bukkit.Color.OLIVE, org.bukkit.Color.PURPLE, org.bukkit.Color.WHITE, org.bukkit.Color.AQUA, org.bukkit.Color.BLACK, org.bukkit.Color.FUCHSIA, org.bukkit.Color.GRAY, org.bukkit.Color.GREEN, org.bukkit.Color.MAROON, org.bukkit.Color.NAVY, org.bukkit.Color.SILVER, org.bukkit.Color.TEAL, org.bukkit.Color.YELLOW};
    public static final YDeficientRegionHolder disallowedTntMinecartsRegionHolder = new YDeficientRegionHolder(92, 458, -98, 268);
    public static final AbstractRegionHolder[] regions = new AbstractRegionHolder[]{
            spawnRegionHolder,// spawn
            new RegionHolder(-119, 97, -300, 5, 140, -176),// ffa
            new RegionHolder(6, -64, -175, -120, -64, -301),// ffa_0
            new YDeficientRegionHolder(6, -175, -120, -175),// ffa_1
            new YDeficientRegionHolder(6, -301, 6, -176),// ffa_2
            new YDeficientRegionHolder(5, -301, -120, -301),// ffa_3
            new YDeficientRegionHolder(-120, -300, -120, -176),// ffa_4
            new RegionHolder(92, 154, 268, -98, 118, 458),// flat
            new RegionHolder(92, 114, 458, -98, 114, 268) // flatdown
    };
    public static final String CATTO_LOVES = "§dᴄᴀᴛᴛᴏ ʟᴏᴠᴇs §r";
    public static final String CATTO_HATES = Utils.translateA("#2e2e2e") + "ᴄᴀᴛᴛᴏ ʜᴀᴛᴇs §r";
    public static final String GAY = Utils.translateA("#fb0000ɢ#56fa35ᴀ#ff00deʏ") + " §r";
    public static final String VIP = Utils.translateA("#faf739") + "ᴠɪᴘ §r";
    public static final String BOOSTER = Utils.translateA("#e900ff") + "ʙᴏᴏꜱᴛᴇʀ §r";
    public static final String MEDIA = Utils.translateA("#ffc2c2") + "ᴍᴇᴅɪᴀ §r";
    public static final String T_HELPER = Utils.translateA("#06dce4") + "ᴛ. ʜᴇʟᴘᴇʀ §r";
    public static final String HELPER = Utils.translateA("#00dd04") + "ʜᴇʟᴘᴇʀ §r";
    public static final String JRMOD = Utils.translateA("#ff7e13") + "ᴊʀ. ᴍᴏᴅ §r";
    public static final String MOD = Utils.translateA("#d10000") + "ᴍᴏᴅ §r";
    public static final String ADMIN = Utils.translateA("#47aeee") + "ᴀᴅᴍɪɴ §r";
    public static final String MANAGER = Utils.translateA("#d10000") + "ᴍᴀɴᴀɢᴇʀ §r";
    public static final String EXECUTIVE = Utils.translateA("#2494fb") + "ᴏᴡɴᴇʀ §r";
    public static final TextComponent D_USING = new TextComponent("§7ᴊᴏɪɴ ᴏᴜʀ ᴅɪsᴄᴏʀᴅ sᴇʀᴠᴇʀ ᴜsɪɴɢ ");
    public static final TextComponent D_LINK = new TextComponent("ᴅɪsᴄᴏʀᴅ.ɢɢ/ᴄᴀᴛsᴍᴘ");
    public static final String MAIN_COLOR = Utils.translateA("#fc282f");
    public static final String SECOND_COLOR = Utils.translateA("#d6a7eb");
    public static final String footer = "§7§l§m                           \n§fᴊᴏɪɴ ᴏᴜʀ ᴅɪꜱᴄᴏʀᴅ ꜱᴇʀᴠᴇʀ ᴜꜱɪɴɢ\n" +
            translateA("#e52d27") +
            ".ɢɢ/ᴄᴀᴛꜱᴍᴘ §fꜰᴏʀ ꜱᴜᴘᴘᴏʀᴛ";
    public static final String header = translate("#e52d27§lᴄᴀᴛ ɴᴇᴛᴡᴏʀᴋ</#b31217>") + "\n" +
            translate("§7§l§m                                  ") + "\n" +
            translate("&#16F5CBᴛ&#1BD3B3ᴘ&#21B29As&#269082: §f20 §7| &#E2DDE0ᴘ&#D0CCCEʟ&#BFBABDᴀ&#ADA9ABʏ&#9C989Aᴇ&#8A8788ʀ&#797577s&#676465: §f%o% §7| &#E52D27ᴘ&#DE2925ʟ&#D72522ᴀ&#D02120ʏ&#C81E1Eᴇ&#C11A1Cʀ&#BA1619s&#B31217: §f%p%") + "\n";
    public static Map<Integer, Location> crystalsToBeOptimized = new Int2ObjectOpenHashMap<>();
    public static Map<String, CustomPlayerDataHolder> playerData = new Object2ObjectOpenHashMap<>();
    public static Map<String, LeaderBoardPlayerHolder> leaderboardData = new Object2ObjectOpenHashMap<>();
    public static ObjectArrayList<TpaRequest> requests = ObjectArrayList.of();
    public static ObjectOpenHashSet<String> bannedFromflat = ObjectOpenHashSet.of();
    public static ObjectArrayList<String> tpa = ObjectArrayList.of();
    public static ObjectArrayList<String> msg = ObjectArrayList.of();
    public static ObjectOpenHashSet<Player> inFFA = ObjectOpenHashSet.of();
    public static ObjectOpenHashSet<String> inFlat = ObjectOpenHashSet.of();
    public static ObjectOpenHashSet<String> atSpawn = ObjectOpenHashSet.of();
    public static ObjectOpenHashSet<String> playersRTPing = ObjectOpenHashSet.of();
    public static Location[] overworldRTP = new Location[100];
    public static Location[] endRTP = new Location[100];
    public static Location ffa;
    public static Location flat;
    public static Location spawn;
    public static Practice p;
    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    public static URL CACHED_WEBHOOK;
    public static URL CACHED_MODERATION_WEBHOOK;
    public static Component BACK;
    public static String EXCEPTION_INTERACTION;
    public static String EXCEPTION_PVP;
    public static String EXCEPTION_TAGGED;
    public static String EXPLOITING_KICK = MAIN_COLOR + "ʏᴏᴜ ᴀʀᴇ ʙᴀɴɴᴇᴅ ꜰʀᴏᴍ ᴄᴀᴛ ɴᴇᴛᴡᴏʀᴋ!\n\n" +
            "§7ʙᴀɴɴᴇᴅ ᴏɴ " + MAIN_COLOR + "» §7";
    public static Scoreboard scoreboard;
    public static Team ownerTeam;
    public static Team managerTeam;
    public static Team adminTeam;
    public static Team modTeam;
    public static Team jrmodTeam;
    public static Team helperTeam;
    public static Team trialHelperTeam;
    public static Team mediaTeam;
    public static Team boosterTeam;
    public static Team vipTeam;
    public static Team cattoLovesTeam;
    public static Team cattoHatesTeam;
    public static Team gayTeam;
    public static int customPlayerDataHashCode;
    public static Map<Integer, Pair<String, Integer>> top_deaths = new Int2ObjectOpenHashMap<>();
    public static Map<Integer, Pair<String, Integer>> top_kills = new Int2ObjectOpenHashMap<>();
    public static Map<Integer, Pair<String, Long>> top_playtime = new Int2ObjectOpenHashMap<>();

    public static void init() {
        try {
            CACHED_WEBHOOK = new URL("https://discord.com/api/webhooks/1188919657088946186/ZV0kpZI_P6KLzz_d_LVbGmVgj94DLwOJBNQylbayYUJo0zz0L8xVZzG7tPP9BOlt4Bip");
            CACHED_MODERATION_WEBHOOK = new URL("https://discord.com/api/webhooks/1212078373132836994/7SoUq23VE2PnFN4AtTqI8nmJ0NHXEaqvvrJsL4eMVVx_IKvISjKfG878wCpqonO_BAxL");
        } catch (MalformedURLException ignored) {
        }
        D_LINK.setColor(ChatColor.of("#fc282f"));
        D_LINK.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/catsmp"));
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        try {
            ownerTeam = scoreboard.registerNewTeam("a");
            ownerTeam.setPrefix(EXECUTIVE);
            managerTeam = scoreboard.registerNewTeam("b");
            managerTeam.setPrefix(MANAGER);
            adminTeam = scoreboard.registerNewTeam("c");
            adminTeam.setPrefix(ADMIN);
            modTeam = scoreboard.registerNewTeam("d");
            modTeam.setPrefix(MOD);
            jrmodTeam = scoreboard.registerNewTeam("e");
            jrmodTeam.setPrefix(JRMOD);
            helperTeam = scoreboard.registerNewTeam("f");
            helperTeam.setPrefix(HELPER);
            trialHelperTeam = scoreboard.registerNewTeam("g");
            trialHelperTeam.setPrefix(T_HELPER);
            mediaTeam = scoreboard.registerNewTeam("h");
            mediaTeam.setPrefix(MEDIA);
            boosterTeam = scoreboard.registerNewTeam("i");
            boosterTeam.setPrefix(BOOSTER);
            vipTeam = scoreboard.registerNewTeam("j");
            vipTeam.setPrefix(VIP);
            cattoLovesTeam = scoreboard.registerNewTeam("k");
            cattoLovesTeam.setPrefix(CATTO_LOVES);
            cattoHatesTeam = scoreboard.registerNewTeam("l");
            cattoHatesTeam.setPrefix(CATTO_HATES);
            gayTeam = scoreboard.registerNewTeam("m");
            gayTeam.setPrefix(GAY);
        } catch (Exception e) {
            ownerTeam = scoreboard.getTeam("a");
            ownerTeam.removeEntries(ownerTeam.getEntries());
            managerTeam = scoreboard.getTeam("b");
            managerTeam.removeEntries(managerTeam.getEntries());
            adminTeam = scoreboard.getTeam("c");
            adminTeam.removeEntries(adminTeam.getEntries());
            modTeam = scoreboard.getTeam("d");
            modTeam.removeEntries(modTeam.getEntries());
            jrmodTeam = scoreboard.getTeam("e");
            jrmodTeam.removeEntries(jrmodTeam.getEntries());
            helperTeam = scoreboard.getTeam("f");
            helperTeam.removeEntries(helperTeam.getEntries());
            trialHelperTeam = scoreboard.getTeam("g");
            trialHelperTeam.removeEntries(trialHelperTeam.getEntries());
            mediaTeam = scoreboard.getTeam("h");
            mediaTeam.removeEntries(mediaTeam.getEntries());
            boosterTeam = scoreboard.getTeam("i");
            boosterTeam.removeEntries(boosterTeam.getEntries());
            vipTeam = scoreboard.getTeam("j");
            vipTeam.removeEntries(vipTeam.getEntries());
            cattoLovesTeam = scoreboard.getTeam("k");
            cattoLovesTeam.removeEntries(cattoLovesTeam.getEntries());
            cattoHatesTeam = scoreboard.getTeam("l");
            cattoHatesTeam.removeEntries(cattoHatesTeam.getEntries());
            gayTeam = scoreboard.getTeam("m");
            gayTeam.removeEntries(gayTeam.getEntries());
        }
        EXCEPTION_TAGGED = MAIN_COLOR + "ʏᴏᴜ ᴄᴀɴ'ᴛ ᴜsᴇ ᴄᴏᴍᴍᴀɴᴅs ɪɴ ᴄᴏᴍʙᴀᴛ!";
        BACK = MiniMessage.miniMessage().deserialize("<gray>Use <#fc282f>/back <gray>to return to your death location.");
        EXCEPTION_INTERACTION = SECOND_COLOR + "You can't interact here!";
        EXCEPTION_PVP = SECOND_COLOR + "You can't PvP here!";
        EXPLOITING_KICK += new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "\n" +
                "§7ʙᴀɴɴᴇᴅ ʙʏ " + MAIN_COLOR + "» §7ᴀɴᴛɪᴄʜᴇᴀᴛ\n" +
                "§7ʀᴇᴀsᴏɴ " + MAIN_COLOR + "» §7ᴄʜᴇᴀᴛɪɴɢ\n" +
                "§7ᴅᴜʀᴀᴛɪᴏɴ " + MAIN_COLOR + "» §730 days\n\n" +
                "§7ᴅɪsᴄᴏʀᴅ " + MAIN_COLOR + "» §7discord.gg/catsmp";
        Bukkit.getPluginManager().registerEvents(new Events(), p);
        Bukkit.getPluginManager().registerEvents(new ProtectionEvents(), p);
        Bukkit.getPluginManager().registerEvents(new EditorClickEvent(), p);
        Bukkit.getPluginManager().registerEvents(new MenuClickEvent(), p);
        Bukkit.getPluginManager().registerEvents(new KitRoomClickEvent(), p);
        Bukkit.getPluginManager().registerEvents(new PublicKitsClickEvent(), p);
        Bukkit.getPluginManager().registerEvents(new EditorCloseEvent(), p);
        Bukkit.getPluginManager().registerEvents(new MenuCloseEvent(), p);
        Bukkit.getPluginManager().registerEvents(new KitRoomCloseEvent(), p);
        Bukkit.getPluginManager().registerEvents(new PublicKitsCloseEvent(), p);
        Practice.d = Bukkit.getWorld("world");
        Practice.d0 = Bukkit.getWorld("world_the_end");
        Initializer.ffa = new Location(Practice.d,
                -56.5D,
                110D,
                -237.5D);
        Initializer.flat = new Location(Practice.d,
                -2.5D,
                131D,
                363.5D);
        Initializer.spawn = new Location(Practice.d,
                0.5D,
                86.06250D,
                0.5D);
        Initializer.spawn.setYaw(
                90F
        );
        Initializer.flat.setYaw(
                90F
        );
        DB.init();
        main.utils.modules.npcs.Utils.init();
        main.utils.modules.holos.Utils.init();
        main.utils.modules.tab.Utils.init();
        main.utils.modules.inactivity.Utils.init();
        main.utils.modules.broadcast.Utils.init();
        main.utils.modules.holos.LeaderboardSync.init();
        main.utils.modules.arenas.Utils.init();
    }
}

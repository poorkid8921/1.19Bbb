package main.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.Economy;
import main.Events;
import main.utils.instances.*;
import main.utils.storage.DB;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Initializer {
    public static final YDeficientRegionHolder spawnRegionHolder = new YDeficientRegionHolder(-24, 23, 22, -23);
    public static final YDeficientRegionHolder flatRegionHolder = new YDeficientRegionHolder(-46, -45, 44, 45);
    public static final Color[] color = new Color[]{org.bukkit.Color.LIME, org.bukkit.Color.ORANGE, org.bukkit.Color.RED, org.bukkit.Color.BLUE, org.bukkit.Color.OLIVE, org.bukkit.Color.PURPLE, org.bukkit.Color.WHITE, org.bukkit.Color.AQUA, org.bukkit.Color.BLACK, org.bukkit.Color.FUCHSIA, org.bukkit.Color.GRAY, org.bukkit.Color.GREEN, org.bukkit.Color.MAROON, org.bukkit.Color.NAVY, org.bukkit.Color.SILVER, org.bukkit.Color.TEAL, org.bukkit.Color.YELLOW};
    public static AbstractRegionHolder[] regions = new AbstractRegionHolder[]{
            spawnRegionHolder,
            new RegionHolder(-128, 137, -127, 126, 198, 127),// arena
            new YDeficientRegionHolder(112, -112, -113, -112),// wall1
            new YDeficientRegionHolder(111, -111, 111, 112),// wall2
            new YDeficientRegionHolder(-113, 112, 110, 112),// wall3
            new YDeficientRegionHolder(-113, -111, -113, 111),// wall4
            new RegionHolder(110, 2, 111, -112, 2, -111) // underarena
    };
    public static Cache<String, Integer> leverFlickCount = Caffeine.newBuilder().expireAfterWrite(Duration.ofMillis(500L)).build();
    public static Map<Integer, Location> crystalsToBeOptimized = new Int2ObjectOpenHashMap<>();
    public static Map<String, CustomPlayerDataHolder> playerData = new Object2ObjectOpenHashMap<>();
    public static Location[] overworldRTP = new Location[100];
    public static Location[] netherRTP = new Location[100];
    public static Location[] endRTP = new Location[100];
    public static ObjectArrayList<TpaRequest> requests = ObjectArrayList.of();
    public static ObjectArrayList<String> tpa = ObjectArrayList.of();
    public static ObjectArrayList<String> msg = ObjectArrayList.of();
    public static Economy p;
    public static Location spawn;
    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    public static URL CACHED_WEBHOOK;
    public static URL CACHED_MODERATION_WEBHOOK;

    public static String GAY = Utils.translateA("#fb0000ɢ#56fa35ᴀ#ff00deʏ") + " §r";
    public static String CATTO_LOVES = "§dᴄᴀᴛᴛᴏ ʟᴏᴠᴇs §r";
    public static String CATTO_HATES = Utils.translateA("#2e2e2e") + "ᴄᴀᴛᴛᴏ ʜᴀᴛᴇs §r";
    public static String VIP = Utils.translateA("#faf739") + "ᴠɪᴘ §r";
    public static String BOOSTER = Utils.translateA("#e900ff") + "ʙᴏᴏꜱᴛᴇʀ §r";
    public static String MEDIA = Utils.translateA("#ffc2c2") + "ᴍᴇᴅɪᴀ §r";
    public static String T_HELPER = Utils.translateA("#06dce4") + "ᴛ. ʜᴇʟᴘᴇʀ §r";
    public static String HELPER = Utils.translateA("#00dd04") + "ʜᴇʟᴘᴇʀ §r";
    public static String JRMOD = Utils.translateA("#ff7e13") + "ᴊʀ. ᴍᴏᴅ §r";
    public static String MOD = Utils.translateA("#d10000") + "ᴍᴏᴅ §r";
    public static String ADMIN = Utils.translateA("#47aeee") + "ᴀᴅᴍɪɴ §r";
    public static String MANAGER = Utils.translateA("#d10000") + "ᴍᴀɴᴀɢᴇʀ §r";
    public static String EXECUTIVE = Utils.translateA("#2494fb") + "ᴏᴡɴᴇʀ §r";

    public static TextComponent D_USING = new TextComponent("§7ᴊᴏɪɴ ᴏᴜʀ ᴅɪsᴄᴏʀᴅ sᴇʀᴠᴇʀ ᴜsɪɴɢ ");
    public static TextComponent D_LINK = new TextComponent("ᴅɪsᴄᴏʀᴅ.ɢɢ/ᴄᴀᴛsᴍᴘ");

    public static String MAIN_COLOR = Utils.translateA("#fc282f");
    public static String SECOND_COLOR = Utils.translateA("#d6a7eb");
    public static String EXCEPTION_INTERACTION;
    public static String EXCEPTION_PVP;
    public static String EXCEPTION_TAGGED;
    public static String EXPLOITING_KICK = MAIN_COLOR + "ʏᴏᴜ ᴀʀᴇ ʙᴀɴɴᴇᴅ ꜰʀᴏᴍ ᴄᴀᴛ ɴᴇᴛᴡᴏʀᴋ!\n\n" + "§7ʙᴀɴɴᴇᴅ ᴏɴ " + MAIN_COLOR + "» §7";
    public static MiniMessage miniMessage = MiniMessage.miniMessage();

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
    public static net.milkbowl.vault.economy.Economy economyHandler;
    public static int customPlayerDataHashCode;

    public static void init() {
        try {
            CACHED_WEBHOOK = new URL("https://discord.com/api/webhooks/1188919657088946186/ZV0kpZI_P6KLzz_d_LVbGmVgj94DLwOJBNQylbayYUJo0zz0L8xVZzG7tPP9BOlt4Bip");
            CACHED_MODERATION_WEBHOOK = new URL("https://discord.com/api/webhooks/1212078373132836994/7SoUq23VE2PnFN4AtTqI8nmJ0NHXEaqvvrJsL4eMVVx_IKvISjKfG878wCpqonO_BAxL");
        } catch (MalformedURLException ignored) {
        }
        D_LINK.setColor(ChatColor.of("#fc282f"));
        D_LINK.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/catsmp"));

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
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
        EXCEPTION_INTERACTION = SECOND_COLOR + "You can't interact here!";
        EXCEPTION_PVP = SECOND_COLOR + "You can't PvP here!";
        EXPLOITING_KICK += new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "\n" + "§7ʙᴀɴɴᴇᴅ ʙʏ " + MAIN_COLOR + "» §7ᴀɴᴛɪᴄʜᴇᴀᴛ\n" + "§7ʀᴇᴀsᴏɴ " + MAIN_COLOR + "» §7ᴄʜᴇᴀᴛɪɴɢ\n" + "§7ᴅᴜʀᴀᴛɪᴏɴ " + MAIN_COLOR + "» §75 days\n\n" + "§7ᴅɪsᴄᴏʀᴅ " + MAIN_COLOR + "» §7discord.gg/catsmp";
        Bukkit.getPluginManager().registerEvents(new Events(), p);
        Bukkit.getPluginManager().registerEvents(new ProtectionEvents(), p);
        DB.init();
        main.utils.npcs.Utils.init();
        main.utils.holos.Utils.init();
        main.utils.tab.Utils.init();
    }
}

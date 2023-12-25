package main.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import static main.utils.Initializer.GSON;

public class Languages {
    public static TextComponent D_USING = new TextComponent(ChatColor.GRAY + "ᴊᴏɪɴ ᴏᴜʀ ᴅɪsᴄᴏʀᴅ sᴇʀᴠᴇʀ ᴜsɪɴɢ ");
    public static TextComponent D_LINK = new TextComponent("ᴅɪsᴄᴏʀᴅ.ɢɢ/ᴀᴇsᴛʜᴇᴛɪᴄɴᴇᴛᴡᴏʀᴋ");
    public static String PREFIX_OWNER;
    public static String PREFIX_MANAGER;
    public static String PREFIX_ADMIN;
    public static String PREFIX_MOD;
    public static String PREFIX_JRMOD;
    public static String PREFIX_HELPER;
    public static String PREFIX_THELPER;
    public static String PREFIX_MEDIA;
    public static String PREFIX_BOOSTER;
    public static String BACK;
    public static String startED;
    public static String WHO_TPA;
    public static String MSGLOCK;
    public static String TPALOCK;
    public static String MSGLOCK1;
    public static String TPALOCK1;
    public static String EXCEPTION_ALREADY_IN_DUEL;
    public static String EXCEPTION_NO_ARENAS_OPEN;
    public static String EXCEPTION_DUEL_TARGET_OFF;
    public static String EXCEPTION_DUEL_SELF;
    public static String EXCEPTION_NO_DUEL_REQ;
    public static String EXCEPTION_NO_ACTIVE_DUELREQ;
    public static String EXCEPTION_NO_ACTIVE_TPAREQ;
    public static String EXCEPTION_NO_ACTIVE_TPAREQ1;
    public static String EXCEPTION_REPORT_SPECIFY_PLAYER;
    public static String EXCEPTION_PLAYER_OFFLINETPA;
    public static String EXCEPTION_PLAYER_TPSELF;
    public static String EXCEPTION_PLAYER_DUELSELF;
    public static String EXCEPTION_NO_ARGS_TELEPORT;
    public static String MAIN_COLOR;
    public static String SECOND_COLOR;
    public static String TITLE;
    public static String SUBTITLE;
    public static String BLANK;
    public static String TELEPORTING_BACK;
    public static String EXCEPTION_NO_ARGS_WARP;
    public static String EXCEPTION_DOESNT_EXIST_WARP;
    public static String EXCEPTION_INVALID_WARP;

    // GLOBAL
    public static String GLOBAL_EXCEPTION_ALREADY_REQ;
    public static String DUELS_RESULTS = "§7ᴅᴜᴇʟ ʀᴇsᴜʟᴛs";
    public static String DUELS_DELIM = "§7------------------------";
    public static TextComponent DUELS_WINNER;

    public static void init() {
        MAIN_COLOR = Utils.translateA("#fc282f");
        SECOND_COLOR = Utils.translateA("#d6a7eb");
        TITLE = GSON.serialize(MiniMessage.miniMessage()
                .deserialize("<b><gradient:#f54254:#d4335b>ᴘʀᴀᴄᴛɪᴄᴇ</gradient>"));
        SUBTITLE = GSON.serialize(MiniMessage.miniMessage()
                .deserialize("<GRAY>ᴍᴄ.ᴀᴇsᴛʜᴇᴛɪᴄ.<gradient:#f54254:#d4335b>ʀᴇᴅ</gradient>"));
        BLANK = GSON.serialize(Component.text(""));

        D_LINK.setColor(ChatColor.of("#fc282f"));
        D_LINK.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/aestheticnetwork"));

        BACK = "§7Use " + MAIN_COLOR + "/back §7to return to your death location";
        startED = " started! " + MAIN_COLOR + "Fight!";

        PREFIX_OWNER = Utils.translateA("#2494fbᴏᴡɴᴇʀ §r");
        PREFIX_MANAGER = Utils.translateA("#d10000ᴍᴀɴᴀɢᴇʀ §r");
        PREFIX_ADMIN = Utils.translateA("#c0191bᴀᴅᴍɪɴ §r");
        PREFIX_MOD = Utils.translateA("#07a7f7ᴍᴏᴅ §r");
        PREFIX_JRMOD = Utils.translateA("#db2824ᴊʀ. ᴍᴏᴅ §r");
        PREFIX_HELPER = Utils.translateA("#00dd04ʜᴇʟᴘᴇʀ §r");
        PREFIX_THELPER = Utils.translateA("#32d337ᴛ. ʜᴇʟᴘᴇʀ §r");
        PREFIX_MEDIA = Utils.translateA("#eaafc8ᴍᴇᴅɪᴀ §r");
        PREFIX_BOOSTER = Utils.translateA("#e900ffʙᴏᴏꜱᴛᴇʀ §r");

        WHO_TPA = "§7You must specify who you want to teleport to.";
        MSGLOCK = "§7You can receive messages from players again.";
        TPALOCK = "§7You can receive tp requests again.";
        MSGLOCK1 = "§7You will no longer receive messages from players.";
        TPALOCK1 = "§7You will no longer receive tp requests from players.";
        EXCEPTION_NO_ARENAS_OPEN = "§7There are no open arenas yet.";
        EXCEPTION_DUEL_SELF = "§7You can't duel yourself.";

        EXCEPTION_NO_DUEL_REQ = "§7You got no active duel request.";
        EXCEPTION_DUEL_TARGET_OFF = "§7You can't send duel requests to offline players.";
        EXCEPTION_ALREADY_IN_DUEL = "§7You can't duel yourself.";
        EXCEPTION_NO_ACTIVE_DUELREQ = "§7You got no active duel request from ";
        EXCEPTION_NO_ACTIVE_TPAREQ = "§7You got no active teleport request.";
        EXCEPTION_NO_ACTIVE_TPAREQ1 = "§7You got no active teleport request from ";
        EXCEPTION_REPORT_SPECIFY_PLAYER = "§7You must specify who you want to report.";
        EXCEPTION_PLAYER_OFFLINETPA = "§7You can't teleport to offline players.";
        EXCEPTION_PLAYER_TPSELF = "§7You can't teleport to yourself.";
        EXCEPTION_PLAYER_DUELSELF = "§7You can't duel yourself.";

        GLOBAL_EXCEPTION_ALREADY_REQ = "§7You already have an ongoing request to this player.";

        TELEPORTING_BACK = "§7Teleporting back to spawn in " + MAIN_COLOR + "3 seconds...";
        DUELS_WINNER = new TextComponent("§7ᴡɪɴɴᴇʀ ");

        EXCEPTION_NO_ARGS_TELEPORT = "§7You must specify a player";
        EXCEPTION_NO_ARGS_WARP = "§7You must specify a warp";
        EXCEPTION_DOESNT_EXIST_WARP = "§7The specified warp doesn't exist";
        EXCEPTION_INVALID_WARP = "§7You must specify a valid warp";
    }
}
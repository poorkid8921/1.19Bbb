package main.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Languages {
    public static TextComponent D_USING = new TextComponent(ChatColor.GRAY + "ᴊᴏɪɴ ᴏᴜʀ ᴅɪsᴄᴏʀᴅ sᴇʀᴠᴇʀ ᴜsɪɴɢ ");
    public static TextComponent D_LINK = new TextComponent("ᴅɪsᴄᴏʀᴅ.ɢɢ/ᴀᴇsᴛʜᴇᴛɪᴄɴᴇᴛᴡᴏʀᴋ");

    public static String BACK;
    public static String DUEL_STARTED;
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
    public static String EXCEPTION_NO_ARGS_TELEPORT;

    public static String MAIN_COLOR;
    public static String SECOND_COLOR;
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

        D_LINK.setColor(ChatColor.of("#fc282f"));
        D_LINK.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/aestheticnetwork"));

        BACK = "§7Use " + MAIN_COLOR + "/back §7to return to your death location";
        DUEL_STARTED = " started! " + MAIN_COLOR + "Fight!";

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

        GLOBAL_EXCEPTION_ALREADY_REQ = "§7You already have an ongoing request to this player.";

        TELEPORTING_BACK = "§7Teleporting back to spawn in " + MAIN_COLOR + "3 seconds...";
        DUELS_WINNER = new TextComponent("§7ᴡɪɴɴᴇʀ ");

        EXCEPTION_NO_ARGS_TELEPORT = "§7You must specify a player";
        EXCEPTION_NO_ARGS_WARP = "§7You must specify a warp";
        EXCEPTION_DOESNT_EXIST_WARP = "§7The specified warp doesn't exist";
        EXCEPTION_INVALID_WARP = "§7You must specify a valid warp";

        Initializer.ffa = new org.bukkit.Location(Bukkit.getWorld("world"),
                Initializer.p.getConfig().getDouble("ffa.X"),
                Initializer.p.getConfig().getDouble("ffa.Y"),
                Initializer.p.getConfig().getDouble("ffa.Z"));
        Initializer.flat = new org.bukkit.Location(Bukkit.getWorld("world"),
                Initializer.p.getConfig().getDouble("flat.X"),
                Initializer.p.getConfig().getDouble("flat.Y"),
                Initializer.p.getConfig().getDouble("flat.Z"));
        Initializer.spawn = new Location(Bukkit.getWorld("world"),
                Initializer.p.getConfig().getDouble("Spawn.X"),
                Initializer.p.getConfig().getDouble("Spawn.Y"),
                Initializer.p.getConfig().getDouble("Spawn.Z"));
        Initializer.spawn.setYaw(
                Initializer.p.getConfig().getLong("Spawn.yaw")
        );
    }
}
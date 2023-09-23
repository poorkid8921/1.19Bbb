package org.yuri.aestheticnetwork.utils.Messages;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.yuri.aestheticnetwork.utils.Utils;

import static org.yuri.aestheticnetwork.utils.Messages.Initializer.*;
import static org.yuri.aestheticnetwork.utils.Messages.Initializer.spawn;
import static org.yuri.aestheticnetwork.utils.Utils.translateo;

public class Languages {
    // REQUESTS
    public static String WHO_DUEL;
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

    // GLOBAL
    public static String GLOBAL_EXCEPTION_ALREADY_REQ;
    public static String DUELS_RESULTS = translateo("&7ᴅᴜᴇʟ ʀᴇsᴜʟᴛs");
    public static String DUELS_DELIM = translateo("&7------------------------");

    public static TextComponent DUELS_WINNER;

    public static void init() {
        WHO_DUEL = translateo("&7You must specify who you want to duel.");
        WHO_TPA = translateo("&7You must specify who you want to teleport to.");
        MSGLOCK = translateo("&7You can receive messages from players again.");
        TPALOCK = translateo("&7You can receive tp requests again.");
        MSGLOCK1 = translateo("&7You will no longer receive messages from players.");
        TPALOCK1 = Utils.translateo("&7You will no longer receive tp requests from players.");
        EXCEPTION_NO_ARENAS_OPEN = translateo("&7There are no open arenas yet.");
        EXCEPTION_DUEL_SELF = translateo("&7You can't duel yourself.");

        EXCEPTION_NO_DUEL_REQ = translateo("&7You got no active duel request.");
        EXCEPTION_DUEL_TARGET_OFF = translateo("&7You can't send duel requests to offline players.");
        EXCEPTION_ALREADY_IN_DUEL = translateo("&7You can't duel yourself.");
        EXCEPTION_NO_ACTIVE_DUELREQ = translateo("&7You got no active duel request from ");
        EXCEPTION_NO_ACTIVE_TPAREQ = translateo("&7You got no active teleport request.");
        EXCEPTION_NO_ACTIVE_TPAREQ1 = translateo("&7You got no active teleport request from ");
        EXCEPTION_REPORT_SPECIFY_PLAYER = translateo("&7You must specify who you want to report.");
        EXCEPTION_PLAYER_OFFLINETPA = translateo("&7You can't teleport to offline players.");
        EXCEPTION_PLAYER_TPSELF = translateo("&7You can't teleport to yourself.");

        GLOBAL_EXCEPTION_ALREADY_REQ = translateo("&7You already have an ongoing request to this player.");

        DUELS_WINNER = new TextComponent(translateo("&7ᴡɪɴɴᴇʀ "));

        ffa = new org.bukkit.Location(Bukkit.getWorld("world"), Initializer.p.getConfig().getDouble("ffa.X"), Initializer.p.getConfig().getDouble("ffa.Y"), Initializer.p.getConfig().getDouble("ffa.Z"));
        flat = new org.bukkit.Location(Bukkit.getWorld("world"), Initializer.p.getConfig().getDouble("flat.X"), Initializer.p.getConfig().getDouble("flat.Y"), Initializer.p.getConfig().getDouble("flat.Z"));
        lflat = new org.bukkit.Location(Bukkit.getWorld("world"), Initializer.p.getConfig().getDouble("legacyflat.X"), Initializer.p.getConfig().getDouble("legacyflat.Y"), Initializer.p.getConfig().getDouble("legacyflat.Z"));
        spawn = new Location(Bukkit.getWorld("world"), Initializer.p.getConfig().getDouble("Spawn.X"), Initializer.p.getConfig().getDouble("Spawn.Y"), Initializer.p.getConfig().getDouble("Spawn.Z"));
        spawn.setYaw(Initializer.p.getConfig().getLong("Spawn.yaw"));
    }
}

package org.yuri.eco.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import static org.yuri.eco.utils.Utils.translateo;

public class Languages {
    // REQUESTS
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

    // GLOBAL
    public static String GLOBAL_EXCEPTION_ALREADY_REQ;

    public static void init() {
        WHO_TPA = translateo("&7You must specify who you want to teleport to.");
        MSGLOCK = translateo("&7You can receive messages from players again.");
        TPALOCK = translateo("&7You can receive tp requests again.");
        MSGLOCK1 = translateo("&7You will no longer receive messages from players.");
        TPALOCK1 = translateo("&7You will no longer receive tp requests from players.");

        EXCEPTION_NO_ACTIVE_TPAREQ = translateo("&7You got no active teleport request.");
        EXCEPTION_NO_ACTIVE_TPAREQ1 = translateo("&7You got no active teleport request from ");
        EXCEPTION_REPORT_SPECIFY_PLAYER = translateo("&7You must specify who you want to report.");
        EXCEPTION_PLAYER_OFFLINETPA = translateo("&7You can't teleport to offline players.");
        EXCEPTION_PLAYER_TPSELF = translateo("&7You can't teleport to yourself.");

        GLOBAL_EXCEPTION_ALREADY_REQ = translateo("&7You already have an ongoing request to this player.");

        Initializer.spawn = new Location(Bukkit.getWorld("world"),
                Initializer.p.getConfig().getDouble("Spawn.X"),
                Initializer.p.getConfig().getDouble("Spawn.Y"),
                Initializer.p.getConfig().getDouble("Spawn.Z"));
        Initializer.spawn.setYaw(Initializer.p.getConfig().getLong("Spawn.yaw"));
    }
}

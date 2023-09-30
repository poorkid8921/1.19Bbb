package main.utils.Messages;

import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

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
        WHO_TPA = Utils.translateo("&7You must specify who you want to teleport to.");
        MSGLOCK = Utils.translateo("&7You can receive messages from players again.");
        TPALOCK = Utils.translateo("&7You can receive tp requests again.");
        MSGLOCK1 = Utils.translateo("&7You will no longer receive messages from players.");
        TPALOCK1 = Utils.translateo("&7You will no longer receive tp requests from players.");

        EXCEPTION_NO_ACTIVE_TPAREQ = Utils.translateo("&7You got no active teleport request.");
        EXCEPTION_NO_ACTIVE_TPAREQ1 = Utils.translateo("&7You got no active teleport request from ");
        EXCEPTION_REPORT_SPECIFY_PLAYER = Utils.translateo("&7You must specify who you want to report.");
        EXCEPTION_PLAYER_OFFLINETPA = Utils.translateo("&7You can't teleport to offline players.");
        EXCEPTION_PLAYER_TPSELF = Utils.translateo("&7You can't teleport to yourself.");

        GLOBAL_EXCEPTION_ALREADY_REQ = Utils.translateo("&7You already have an ongoing request to this player.");

        Initializer.ffa = new org.bukkit.Location(Bukkit.getWorld("world"), Initializer.p.getConfig().getDouble("ffa.X"), Initializer.p.getConfig().getDouble("ffa.Y"), Initializer.p.getConfig().getDouble("ffa.Z"));
        Initializer.flat = new org.bukkit.Location(Bukkit.getWorld("world"), Initializer.p.getConfig().getDouble("flat.X"), Initializer.p.getConfig().getDouble("flat.Y"), Initializer.p.getConfig().getDouble("flat.Z"));
        Initializer.lflat = new org.bukkit.Location(Bukkit.getWorld("world"), Initializer.p.getConfig().getDouble("legacyflat.X"), Initializer.p.getConfig().getDouble("legacyflat.Y"), Initializer.p.getConfig().getDouble("legacyflat.Z"));
        Initializer.spawn = new Location(Bukkit.getWorld("world"), Initializer.p.getConfig().getDouble("Spawn.X"), Initializer.p.getConfig().getDouble("Spawn.Y"), Initializer.p.getConfig().getDouble("Spawn.Z"));
        Initializer.spawn.setYaw(Initializer.p.getConfig().getLong("Spawn.yaw"));
    }
}

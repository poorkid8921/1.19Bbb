package main.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Languages {
    public static TextComponent D_USING = new TextComponent(ChatColor.GRAY + "ᴊᴏɪɴ ᴏᴜʀ ᴅɪsᴄᴏʀᴅ sᴇʀᴠᴇʀ ᴜsɪɴɢ ");
    public static TextComponent D_LINK = new TextComponent("ᴅɪsᴄᴏʀᴅ.ɢɢ/ᴀᴇsᴛʜᴇᴛɪᴄɴᴇᴛᴡᴏʀᴋ");

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

    public static void init() {
        D_LINK.setColor(ChatColor.of("#fc282f"));
        D_LINK.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/aestheticnetwork"));

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

        GLOBAL_EXCEPTION_ALREADY_REQ = "§7You already have an ongoing request to this player.";
        MAIN_COLOR = Utils.translateA("#fc282f");

        Initializer.spawn = new Location(Bukkit.getWorld("world"),
                Initializer.p.getConfig().getDouble("Spawn.X"),
                Initializer.p.getConfig().getDouble("Spawn.Y"),
                Initializer.p.getConfig().getDouble("Spawn.Z"));
        Initializer.spawn.setYaw(Initializer.p.getConfig().getLong("Spawn.yaw"));
    }
}

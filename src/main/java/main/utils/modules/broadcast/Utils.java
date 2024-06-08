package main.utils.modules.broadcast;

import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static main.utils.Initializer.*;

public class Utils {
    public static final String[] MOTD = new String[]{
            "§7---------------------------------------",
            "§7ᴛʏᴘᴇ " + MAIN_COLOR + "/kit §7ᴏʀ " + MAIN_COLOR + "/k §7ᴛᴏ ɢᴇᴛ ꜱᴛᴀʀᴛᴇᴅ",
            "§7---------------------------------------"
    };
    /*private static final String[] tips = new String[]{
            SECOND_COLOR + "§lTip §r| §7During fire tick, players don't take knockback.",
            SECOND_COLOR + "§lTip §r| §7Don't carry more than 9 totems with you.",
            SECOND_COLOR + "§lTip §r| §7Avoid using shields in non drain fights.",
            SECOND_COLOR + "§lTip §r| §7Avoid using anchors in flat.",
            SECOND_COLOR + "§lTip §r| §7Use /shop to customize your kills.",
            SECOND_COLOR + "§lTip §r| §7Cooperate with staff if they ask you to screenshare.",
            SECOND_COLOR + "§lTip §r| §7Use /fastcrystals to toggle the optimizer.",
            SECOND_COLOR + "§lTip §r| §7Use /msglock to toggle messages.",
            SECOND_COLOR + "§lTip §r| §7Use /tpalock to toggle teleport requests.",
            SECOND_COLOR + "§lTip §r| §7Use /clear to clear your inventory.",
            SECOND_COLOR + "§lTip §r| §7Use !rs to reset your statistics."
    };*/
    private static short ticked = 0;

    public static void init() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(p, () -> {
            if (ticked++ == 2) {
                for (final Player k : Bukkit.getOnlinePlayers())
                    k.sendMessage(MOTD);
            } /*else {
                for (final Player k : Bukkit.getOnlinePlayers())
                    k.sendMessage(tips[Initializer.RANDOM.nextInt(11)]);
            }*/
        }, 0L, 6000L);
    }
}

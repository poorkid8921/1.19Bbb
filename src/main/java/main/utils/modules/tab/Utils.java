package main.utils.modules.tab;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Utils {
    public static void init() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (final Player k : Bukkit.getOnlinePlayers()) {

            }
        }, 0L, 20L);
    }
}

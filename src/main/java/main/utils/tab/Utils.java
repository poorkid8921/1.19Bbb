package main.utils.tab;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static main.utils.Initializer.p;

public class Utils {
    public static void init() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(p, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {

            }
        }, 0L, 20L);
    }
}

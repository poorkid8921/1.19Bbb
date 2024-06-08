package main.utils.modules.tab;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static main.utils.Initializer.p;

public class Utils {
    public static void init() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(p, () -> {
            for (final Player k : Bukkit.getOnlinePlayers()) {
                //k.setPlayerListHeader(header.replaceAll("o", String.valueOf(Bukkit.getOnlinePlayers().size())).replaceAll("p", String.valueOf(k.getPing())));
                //k.setPlayerListFooter(footer);
            }
        }, 0L, 20L);
    }
}

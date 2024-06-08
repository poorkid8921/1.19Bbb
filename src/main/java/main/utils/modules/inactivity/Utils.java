package main.utils.modules.inactivity;

import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

import static main.utils.Initializer.p;
import static main.utils.Initializer.playerData;

public class Utils {
    public static void init() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(p, () -> {
            long expireDate = System.currentTimeMillis() - 1800000L;
            Player player;
            for (Map.Entry<String, CustomPlayerDataHolder> D0 : playerData.entrySet()) {
                final String key = D0.getKey();
                player = Bukkit.getPlayer(key);
                if (player == null)
                    playerData.remove(key);
                else if (expireDate > D0.getValue().getLastTimeKitWasUsed()) {
                    player.kickPlayer("§7You have been kicked for being too inactive.");
                    playerData.remove(key);
                }
            }
        }, 0L, 3600L);
    }
}

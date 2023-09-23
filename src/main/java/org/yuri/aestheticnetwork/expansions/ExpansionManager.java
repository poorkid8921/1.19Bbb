package org.yuri.aestheticnetwork.expansions;

import org.bukkit.Bukkit;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.expansions.kits.Languages;
import org.yuri.aestheticnetwork.utils.Messages.Initializer;

public class ExpansionManager {
    public static void initKits() {
        Languages.init();
        Bukkit.getScheduler().runTaskTimer(Initializer.p, () -> {
                    if (Bukkit.getServer().getOnlinePlayers().size() > 0) {
                        Languages.MOTD.forEach(Bukkit::broadcastMessage);
                    }
                },
                0L,
                6000L);
        AestheticNetwork.log("Initialized the Kits expansion.");
    }
}

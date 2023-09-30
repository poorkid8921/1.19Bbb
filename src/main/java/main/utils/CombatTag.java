package main.utils;

import main.utils.Messages.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CombatTag {
    String s = ChatColor.GRAY +
            "ᴄᴏᴍʙᴀᴛ: " +
            ChatColor.DARK_RED;

    public CombatTag(Player p,
                     Player t) {
        String pn = p.getName();
        Initializer.inCombat.add(pn);

        Bukkit.getScheduler().runTaskTimer(Initializer.p, new BukkitRunnable() {
            int i = 5;

            @Override
            public void run() {
                if (i-- == 0) {
                    Initializer.inCombat.remove(pn);
                    Initializer.inCombat.remove(t.getName());
                    cancel();
                    return;
                }

                p.sendActionBar(s + i);
                t.sendActionBar(s + i);
            }
        }, 0L, 20L);
    }
}
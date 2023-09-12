package org.yuri.aestheticnetwork.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static org.yuri.aestheticnetwork.utils.Initializer.inCombat;

public class CombatTag {
    String s = ChatColor.GRAY +
            "ᴄᴏᴍʙᴀᴛ: " +
            ChatColor.DARK_RED;

    public CombatTag(Player p,
                     Player t) {
        String pn = p.getName();
        inCombat.add(pn);

        Bukkit.getScheduler().runTaskTimer(Initializer.p, new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                if (i++ == 4) {
                    inCombat.remove(pn);
                    cancel();
                    return;
                }

                p.sendActionBar(s + i);
                t.sendActionBar(s + i);
            }
        }, 0L, 20L);
    }

    public CombatTag(Player p,
                     Entity t) {
        if (t instanceof Player) {
            new CombatTag(p, (Player) t);
            return;
        }

        String pn = p.getName();
        inCombat.add(pn);

        Bukkit.getScheduler().runTaskTimer(Initializer.p, new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                if (i++ == 4) {
                    inCombat.remove(pn);
                    cancel();
                    return;
                }

                p.sendActionBar(s + i);
            }
        }, 0L, 20L);
    }
}
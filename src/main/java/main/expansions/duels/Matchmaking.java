package main.expansions.duels;

import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Optional;

import static main.utils.DuelUtils.*;
import static main.utils.Languages.MAIN_COLOR;

public class Matchmaking {
    public static void start_unranked(Player p, int gm) {
        String n = p.getName();
        Optional<Map.Entry<String, Integer>> op = Initializer.inMatchmaking.entrySet().stream().filter(r -> r.getValue() == gm && !r.getKey().equals(n)).findFirst();
        if (op.isPresent()) {
            int check = duelsavailable(gm);
            if (check >= 32) {
                p.sendActionBar("§aCouldn't find any open arena.");
                return;
            }
            Player j = Bukkit.getPlayer(op.get().getKey());
            j.sendMessage("§7You are now in a duel against " + MAIN_COLOR + n);
            p.sendMessage("§7You are now in a duel against " + MAIN_COLOR + j.getDisplayName());
            start(p, j, gm, 1, 1, check + 1);
            return;
        }

        p.sendActionBar("§aYou have been placed into the " + formattedtype(gm) + " queue.");
        Initializer.inMatchmaking.put(n, gm);
        new BukkitRunnable() {
            int timeout = 0;

            @Override
            public void run() {
                if (Bukkit.getPlayer(n) == null) {
                    this.cancel();
                    return;
                }

                Optional<Map.Entry<String, Integer>> op = Initializer.inMatchmaking.entrySet().stream().filter(r -> r.getValue() == gm && !r.getKey().equals(n)).findFirst();
                if (op.isPresent()) {
                    Initializer.inMatchmaking.remove(n);
                    int check = duelsavailable(gm);
                    if (check >= 32) {
                        p.sendActionBar("§aCouldn't find any open arena.");
                        this.cancel();
                        return;
                    }
                    Player j = Bukkit.getPlayer(op.get().getKey());
                    j.sendMessage("§7You are now in a duel against " + MAIN_COLOR + n);
                    p.sendMessage("§7You are now in a duel against " + MAIN_COLOR + j.getName());
                    start(p, j, gm, 1, 1, check + 1);
                    this.cancel();
                    return;
                }

                if (++timeout == 10) {
                    p.sendActionBar("§aCouldn't find any available duels.");
                    Initializer.inMatchmaking.remove(n);
                    this.cancel();
                }
            }
        }.runTaskTimer(Initializer.p, 0L, 60L);
    }
}

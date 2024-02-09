package main.utils.duels;

import main.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Optional;

import static main.utils.Constants.MAIN_COLOR;
import static main.utils.DuelUtils.*;

public class Matchmaking {
    public static void start_unranked(Player p, int gm) {
        String n = p.getName();
        Optional<Map.Entry<String, Integer>> op = Constants.inMatchmaking.entrySet().stream().filter(result -> result.getValue() == gm && !result.getKey().equals(n)).findFirst();
        if (op.isPresent()) {
            int check = getDuelsAvailable(gm);
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

        p.sendActionBar("§aYou have been placed into the " + getDuelFormatted(gm) + " queue.");
        Constants.inMatchmaking.put(n, gm);
        new BukkitRunnable() {
            int timeout = 0;

            @Override
            public void run() {
                if (Bukkit.getPlayer(n) == null) {
                    this.cancel();
                    return;
                }

                Optional<Map.Entry<String, Integer>> op = Constants.inMatchmaking.entrySet().stream().filter(result -> result.getValue() == gm && !result.getKey().equals(n)).findFirst();
                if (op.isPresent()) {
                    Constants.inMatchmaking.remove(n);
                    int check = getDuelsAvailable(gm);
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
                    Constants.inMatchmaking.remove(n);
                    this.cancel();
                }
            }
        }.runTaskTimer(Constants.p, 0L, 60L);
    }
}

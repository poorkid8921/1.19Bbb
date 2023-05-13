package bab.bbb.utils;

import bab.bbb.Bbb;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.util.Collection;

import static bab.bbb.utils.Utils.translate;

@RequiredArgsConstructor
public class Tablist implements Runnable {
    private final Bbb plugin = Bbb.getInstance();
    private final String sh = plugin.getConfig().getString("tablist-header");
    private final String sf = plugin.getConfig().getString("tablist-footer");

    public void run() {
        Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
        try {
            if (players.size() == 0)
                return;

            if (players.size() == 1) {
                for (Player player : players) {
                    player.setPlayerListHeaderFooter(
                            new ComponentBuilder(translate(player, sh)).create(),
                            new ComponentBuilder(translate(player, sf).replace("players", "player")).create());
                }
            } else {
                for (Player player : players) {
                    player.setPlayerListHeaderFooter(
                            new ComponentBuilder(translate(player, sh)).create(),
                            new ComponentBuilder(translate(player, sf)).create());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
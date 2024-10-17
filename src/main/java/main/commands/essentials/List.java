package main.commands.essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

public class List implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        int playerSize = players.size();
        java.util.List<String> playerList = players
                .stream()
                .map(Player::getName)
                .sorted()
                .toList();

        sender.sendMessage(playerSize + " | " + playerList);
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

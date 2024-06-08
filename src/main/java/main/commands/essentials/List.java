package main.commands.essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

public class List implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            final Collection<? extends Player> players = Bukkit
                    .getOnlinePlayers();
            sender.sendMessage(players.size() + " | " + players
                    .stream()
                    .map(Player::getName)
                    .sorted()
                    .toList());
        }
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

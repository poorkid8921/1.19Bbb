package main.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class List implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            Collection<? extends Player> p = Bukkit
                    .getOnlinePlayers();
            sender.sendMessage(p.size() + " | " + p
                    .stream()
                    .map(Player::getName)
                    .sorted()
                    .toList());
        }
        return true;
    }

    @Override
    public @Nullable java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return java.util.List.of();
    }
}

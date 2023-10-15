package main.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class List implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] strings) {
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
}

package main.commands.essentialsx;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class list implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(Bukkit
                .getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .sorted()
                .toList()
                .toString());
        return true;
    }
}

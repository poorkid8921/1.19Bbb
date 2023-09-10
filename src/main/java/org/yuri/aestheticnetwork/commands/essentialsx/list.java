package org.yuri.aestheticnetwork.commands.essentialsx;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class list implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        String str = Bukkit
                .getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .sorted()
                .toList()
                .toString();
        commandSender.sendMessage(str);
        return true;
    }
}

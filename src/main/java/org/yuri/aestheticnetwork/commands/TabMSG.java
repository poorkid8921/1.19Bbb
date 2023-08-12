package org.yuri.aestheticnetwork.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.AestheticNetwork;

import java.util.List;
import java.util.stream.Collectors;

public class TabMSG implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        return args.length < 1 ? AestheticNetwork.msg
                .stream()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList()) :
                AestheticNetwork.msg
                        .stream()
                        .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.toList());
    }
}
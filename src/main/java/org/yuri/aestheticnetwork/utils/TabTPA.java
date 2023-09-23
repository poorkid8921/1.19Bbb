package org.yuri.aestheticnetwork.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import static org.yuri.aestheticnetwork.utils.Messages.Initializer.tpa;

public class TabTPA implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        return args.length < 1 ? tpa
                .stream()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList()) : tpa
                        .stream()
                        .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.toList());
    }
}
package org.yuri.aestheticnetwork;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class TabTPA implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        return args.length < 1 ? AestheticNetwork.tpa
                .stream()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList()) :
                AestheticNetwork.tpa
                        .stream()
                        .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.toList());
    }
}
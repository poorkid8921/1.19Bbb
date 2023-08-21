package org.yuri.aestheticnetwork.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.utils.Initializer;

import java.util.List;
import java.util.stream.Collectors;

public class TabMSG implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        return args.length < 1 ? Initializer.msg
                .stream()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList()) :
                Initializer.msg
                        .stream()
                        .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.toList());
    }
}
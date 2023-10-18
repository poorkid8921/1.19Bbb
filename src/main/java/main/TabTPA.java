package main;

import main.utils.Initializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;
import java.util.stream.Collectors;

public class TabTPA implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return args.length < 1 ? Initializer.tpa
                .stream()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList()) :
                Initializer.tpa
                        .stream()
                        .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.toList());
    }
}
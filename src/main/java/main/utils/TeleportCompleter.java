package main.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

import static main.utils.Utils.tabCompleteFilter;

public class TeleportCompleter implements org.bukkit.command.TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return args.length == 0 ?
                Constants.tpa :
                tabCompleteFilter(Constants.tpa, args[0].toLowerCase());
    }
}

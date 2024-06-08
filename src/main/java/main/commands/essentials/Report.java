package main.commands.essentials;

import main.utils.Gui;
import main.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Report implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7You must specify who you want to report!");
            return true;
        }

        if (args.length < 2)
            Gui.openReport((Player) sender, args[0]);
        else {
            final StringBuilder msg = new StringBuilder();
            for (final String arg : args) msg.append(arg).append(" ");
            Utils.submitReport((Player) sender, msg.toString(), null);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length < 2 ? null : Collections.emptyList();
    }
}

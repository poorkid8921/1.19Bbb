package main.commands.essentials;

import main.managers.GUIManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static main.Economy.guiManager;
import static main.utils.Utils.submitReport;

public class Report implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7You must specify who you want to report!");
            return true;
        }
        if (args.length < 2) guiManager.openReport((Player) sender, args[0]);
        else {
            final StringBuilder msg = new StringBuilder();
            for (final String arg : args) msg.append(arg).append(" ");
            submitReport((Player) sender, msg.toString(), null);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length < 2 ? null : Collections.emptyList();
    }
}

package commands;

import main.inventories.ReportInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static main.utils.Utils.report;

@SuppressWarnings("deprecation")
public class Report implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§7You must specify who you want to report.");
            return true;
        }

        if (args.length < 2) new ReportInventory((Player) sender, args[0]).open();
        else {
            StringBuilder msgargs = new StringBuilder();
            for (String arg : args) msgargs.append(arg).append(" ");

            report((Player) sender, msgargs.toString(), "Other");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}

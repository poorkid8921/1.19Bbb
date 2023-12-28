package main.commands;

import main.utils.Initializer;
import main.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Report implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Initializer.EXCEPTION_REPORT_SPECIFY_PLAYER);
            return true;
        }

        if (args.length < 2)
            main.expansions.guis.Utils.openReport((Player) sender, args[0]);
        else {
            StringBuilder msgargs = new StringBuilder();
            for (String arg : args) msgargs.append(arg).append(" ");

            Utils.report((Player) sender, msgargs.toString(), null);
        }
        return true;
    }
}

package main.commands;

import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static main.utils.Constants.MAIN_COLOR;
import static main.utils.Constants.playerData;

public class DelHome implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CustomPlayerDataHolder D0 = playerData.get(sender.getName());
        if (args.length == 0) {
            sender.sendMessage("§7You must specify the home you want to delete.");
            return true;
        } else if (!D0.getHomes().containsKey(args[0])) {
            sender.sendMessage("§7You have no home called " + MAIN_COLOR + args[0] + ".");
            return true;
        }

        D0.getHomes().remove(args[0]);
        sender.sendMessage("§7You have successfully delete " + MAIN_COLOR + args[0] + ".");
        return true;
    }
}

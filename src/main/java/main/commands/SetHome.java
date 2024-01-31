package main.commands;

import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Constants.MAIN_COLOR;
import static main.utils.Constants.playerData;

public class SetHome implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CustomPlayerDataHolder D0 = playerData.get(sender.getName());
        if (args.length == 0) {
            sender.sendMessage("§7You must specify a name for your home.");
            return true;
        } else if (D0.getHomes().containsKey(args[0])) {
            sender.sendMessage("§7You already have a home called " + MAIN_COLOR + args[0] + "!");
            return true;
        }

        D0.getHomes().put(args[0], ((Player) sender).getLocation());
        sender.sendMessage("§7You have successfully set the home " + MAIN_COLOR + args[0] + " §7at your current location.");
        return true;
    }
}

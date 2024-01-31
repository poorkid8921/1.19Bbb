package main.commands;

import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Constants.MAIN_COLOR;
import static main.utils.Constants.playerData;

public class Home implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CustomPlayerDataHolder D0 = playerData.get(sender.getName());
        if (args.length == 0) {
            sender.sendMessage("§7You must specify the home you want to teleport to.");
            return true;
        } else if (!D0.getHomes().containsKey(args[0])) {
            sender.sendMessage("§7You have no home called " + MAIN_COLOR + args[0] + ".");
            return true;
        }

        ((Player) sender).teleportAsync(D0.getHomes().get(args[0]));
        sender.sendMessage("§7You have successfully teleport to " + MAIN_COLOR + args[0] + "!");
        return true;
    }
}
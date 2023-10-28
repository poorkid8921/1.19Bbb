package main.commands;

import main.expansions.guis.Utils;
import main.utils.KitClaimer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Kit implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player))
            return true;

        if (args.length == 0) {
            Utils.openKitMenu(player);
            return true;
        }

        StringBuilder kit = new StringBuilder();
        for (String arg : args) {
            kit.append(arg).append(" ");
        }
        kit.deleteCharAt(kit.length() - 1);
        KitClaimer.claimFromName(player, kit.toString());
        return true;
    }
}

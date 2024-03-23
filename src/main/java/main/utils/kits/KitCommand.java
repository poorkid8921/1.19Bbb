package main.utils.kits;

import main.utils.kits.inventories.KitMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            new KitMenu((Player) sender);
        } else {
            StringBuilder kit = new StringBuilder();
            for (String arg : args) {
                kit.append(arg).append(" ");
            }
            kit.deleteCharAt(kit.length() - 1);
            KitClaimer.claimFromName((Player) sender, kit.toString());
        }
        return true;
    }
}
package main.commands.essentials;

import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Initializer.MAIN_COLOR;

public class PlayTime implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage("§7You can't view the playtime of offline players.");
                return true;
            }
            sender.sendMessage(MAIN_COLOR + p.getDisplayName() + "§7's playtime is " + MAIN_COLOR + Utils.getTime(p));
            return true;
        }
        sender.sendMessage("§7Your playtime is " + MAIN_COLOR + Utils.getTime((Player) sender));
        return true;
    }
}
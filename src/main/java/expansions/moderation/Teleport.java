package expansions.moderation;

import main.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Teleport implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("has.staff")) {
            if (args.length < 1) {
                sender.sendMessage(Constants.EXCEPTION_NO_ARGS_TELEPORT);
                return true;
            }

            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage(Constants.EXCEPTION_NO_ARGS_TELEPORT);
                return true;
            }

            ((Player) sender).teleportAsync(p.getLocation());
        }
        return true;
    }
}

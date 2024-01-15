package expansions.moderation;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GMs implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("has.staff")) {
            ((Player) sender).setGameMode(GameMode.SURVIVAL);
            sender.sendMessage("§7Your gamemode has been changed to survival.");
        }
        return true;
    }
}

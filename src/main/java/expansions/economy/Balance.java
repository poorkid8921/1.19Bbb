package expansions.economy;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Constants.playerData;

public class Balance implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0)
            try {
                Player p = (Player) Bukkit.getOfflinePlayer(args[0]);
                sender.sendMessage("§a" + p.getName() + "'s balance is $" + playerData.get(p.getName()).getMoney());
            } catch (Exception e) {
                sender.sendMessage("§7You must specify a valid player.");
            }
        else
            sender.sendMessage("§aYour balance is $" + playerData.get(sender.getName()).getMoney());
        return true;
    }
}

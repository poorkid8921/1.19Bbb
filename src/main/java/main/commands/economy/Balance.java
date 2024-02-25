package main.commands.economy;

import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Initializer.playerData;
import static main.utils.Utils.economyFormat;

public class Balance implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            try {
                Player p = (Player) Bukkit.getOfflinePlayer(args[0]);
                String name = p.getName();
                CustomPlayerDataHolder D0 = playerData.get(name);
                sender.sendMessage("§aBalance of " + D0.getFRank(name) + ": §c" + economyFormat.format(D0.getMoney()));
            } catch (Exception ignored) {
                sender.sendMessage("§7You must specify a valid player!");
            }
        } else
            sender.sendMessage("§aBalance: " + economyFormat.format(playerData.get(sender.getName()).getMoney()));
        return true;
    }
}

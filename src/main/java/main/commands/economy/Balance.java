package main.commands.economy;

import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static main.utils.Initializer.economyHandler;
import static main.utils.Initializer.playerData;
import static main.utils.Utils.economyFormat;

public class Balance implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            try {
                Player p = (Player) Bukkit.getOfflinePlayer(args[0]);
                String name = p.getName();
                CustomPlayerDataHolder D0 = playerData.get(name);
                sender.sendMessage("§aBalance of " + D0.getFRank(name) + ": §c" + economyFormat.format(economyHandler.getBalance(p)));//D0.getMoney()));
            } catch (Exception ignored) {
                sender.sendMessage("§7You must specify a valid player!");
            }
        } else
            sender.sendMessage("§aBalance: §c" + economyFormat.format(economyHandler.getBalance((Player) sender)));//playerData.get(sender.getName()).getMoney()));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length < 2 ? null : Collections.emptyList();
    }
}

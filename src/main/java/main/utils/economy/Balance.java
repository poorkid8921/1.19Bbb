package main.utils.economy;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

import static main.utils.Constants.playerData;

public class Balance implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        if (args.length > 0)
            try {
                Player p = (Player) Bukkit.getOfflinePlayer(args[0]);
                String name = p.getName();
                sender.sendMessage("§a" + name + "§7's balance is $" + formatter.format(playerData.get(name).getMoney()));
            } catch (Exception e) {
                sender.sendMessage("§7You must specify a valid player.");
            }
        else
            sender.sendMessage("§aYour balance is $" + formatter.format(playerData.get(sender.getName()).getMoney()));
        return true;
    }
}

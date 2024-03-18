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
import static main.utils.Utils.getMoneyValue;
import static main.utils.Utils.shortFormat;

public class Pay implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7You must specify the player you want to pay!");
            return true;
        } else if (args.length == 1) {
            sender.sendMessage("§7You must specify the amount to pay the specified player!");
            return true;
        } else if (args[1].contains("-")) {
            sender.sendMessage("§7You can't pay the specified player a negative amount!");
            return true;
        }

        String sanitized = args[1].replaceAll("[^0-9.]", "");
        String nReplaceResult = args[1].replaceAll("[0-9.]", "");
        double amount = nReplaceResult.isEmpty() ? Double.parseDouble(args[1]) :
                getMoneyValue(Double.parseDouble(sanitized), nReplaceResult.charAt(0));
        String sn = sender.getName();
        Player sP = (Player) sender;
        if ((economyHandler.getBalance(sP) - amount) < 0) {
            sender.sendMessage("§7You don't have enough money!");
            return true;
        }
        economyHandler.withdrawPlayer(sP, amount);
        String formattedMoney = "§a$" + (amount >= 1000 ? shortFormat(amount, 0) : amount);
        Player toSendPlayer = Bukkit.getPlayer(args[0]);
        if (toSendPlayer == null) {
            sender.sendMessage("§7You can only pay online players!");
            return true;
        }
        String toSendName = toSendPlayer.getName();
        CustomPlayerDataHolder D1 = playerData.get(toSendName);
        economyHandler.depositPlayer(toSendPlayer, amount);
        toSendPlayer.sendMessage(formattedMoney + " §6has been received from " + playerData.get(sn).getFRank(sn) + "§6.");
        sender.sendMessage(formattedMoney + " has been sent to " + D1.getFRank(toSendName) + ".");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length < 2 ? null : Collections.emptyList();
    }
}

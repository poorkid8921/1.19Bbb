package main.commands.economy;

import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Initializer.playerData;
import static main.utils.Utils.economyFormat;
import static main.utils.Utils.getMoneyValue;

public class Pay implements CommandExecutor {
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

        final String sanitizedString = args[1].replaceAll("[^0-9.]", "");
        if (sanitizedString.isEmpty()) {
            sender.sendMessage("§7You must specify the amount to pay the specified player!");
            return true;
        }
        double amount = getMoneyValue(args[1], sanitizedString);
        String sn = sender.getName();
        CustomPlayerDataHolder D0 = playerData.get(sn);
        if ((D0.getMoney() - amount) < 0) {
            sender.sendMessage("§7You don't have enough money.");
            return true;
        }
        D0.decrementMoney(amount);

        String formattedMoney = "§a" + economyFormat.format(amount);
        Player toSendPlayer = (Player) Bukkit.getOfflinePlayer(args[0]);
        String toSendName = toSendPlayer.getName();
        CustomPlayerDataHolder D1 = playerData.get(toSendName);
        D1.incrementMoney(amount);

        if (toSendPlayer.isOnline())
            toSendPlayer.sendMessage(formattedMoney + " §6has been received from " + D0.getFRank(sn) + "§6.");
        sender.sendMessage(formattedMoney + " has been sent to " + D1.getFRank(toSendName) + ".");
        return true;
    }
}

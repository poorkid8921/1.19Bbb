package main.commands.economy;

import main.utils.instances.CustomPlayerDataHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static main.utils.Initializer.*;
import static main.utils.Utils.getMoneyValue;
import static main.utils.Utils.shortFormat;

public class Bounty implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7You must specify the player you want to bounty on!");
            return true;
        } else if (args.length == 1) {
            sender.sendMessage("§7You must specify the amount to add to the specified player's bounty!");
            return true;
        } else if (args[1].contains("-")) {
            sender.sendMessage("§7You can't bounty the specified player a negative amount!");
            return true;
        }

        String sanitized = args[1].replaceAll("[^0-9.]", "");
        String nReplaceResult = args[1].replaceAll("[0-9.]", "");
        double amount = nReplaceResult.isEmpty() ? Double.parseDouble(args[1]) :
                getMoneyValue(Double.parseDouble(sanitized), nReplaceResult.charAt(0));
        Player sP = (Player) sender;
        if ((economyHandler.getBalance(sP) - amount) < 0) {
            sender.sendMessage("§7You don't have enough money!");
            return true;
        }
        economyHandler.withdrawPlayer(sP, amount);
        Player toSendPlayer = Bukkit.getPlayer(args[0]);
        if (toSendPlayer == null) {
            sender.sendMessage("§7You can only put bounty on online players!");
            return true;
        }
        String toSendName = toSendPlayer.getName();
        CustomPlayerDataHolder D1 = playerData.get(toSendName);
        if (amount >= 1000000) {
            Component component = miniMessage.deserialize("<#d6a7eb>ʙᴏᴜɴᴛʏ » " + sP.getName() + " <gray>put a bounty on <#d6a7eb>" + toSendName + " <gray>worth <green>$" + shortFormat(amount, 0));
            for (Player p : Bukkit.getOnlinePlayers())
                p.sendMessage(component);
        } else
            sender.sendMessage(toSendPlayer == sP ? (SECOND_COLOR + D1.getFRank(toSendName) + "§7's bounty is now ") : ("your bounty is now ") + SECOND_COLOR + "$" + (amount >= 1000 ? shortFormat(amount, 0) : amount) + "!");
        D1.incrementBounty(amount);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length < 2 ? null : Collections.emptyList();
    }
}

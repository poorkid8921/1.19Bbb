package main.commands.economy;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;

import static main.utils.Initializer.playerData;
import static main.utils.Utils.*;

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
        BigDecimal amount = new BigDecimal(sanitizedString);
        switch (Character.toLowerCase(args[1].charAt(args[1].length() - 1))) {
            case 'k' -> amount = amount.multiply(THOUSAND);
            case 'm' -> amount = amount.multiply(MILLION);
            case 'b' -> amount = amount.multiply(BILLION);
            case 't' -> amount = amount.multiply(TRILLION);
        }
        double finalAmount = amount.doubleValue();
        String sn = sender.getName();
        CustomPlayerDataHolder D0 = playerData.get(sn);
        if ((D0.getMoney() - finalAmount) < 0) {
            sender.sendMessage("§7You don't have enough money.");
            return true;
        }
        D0.decrementMoney(finalAmount);

        String formattedMoney = "§a$" + economyFormat.format(finalAmount);
        Player toSendPlayer = (Player) Bukkit.getOfflinePlayer(args[0]);
        String toSendName = toSendPlayer.getName();
        CustomPlayerDataHolder D1 = playerData.get(toSendName);
        D1.incrementMoney(finalAmount);

        if (toSendPlayer.isOnline())
            toSendPlayer.sendMessage(formattedMoney + " §6has been received from " + D0.getFRank(sn) + "§6.");
        sender.sendMessage(formattedMoney + " has been sent to " + D1.getFRank(toSendName) + ".");
        return true;
    }
}

package org.yuri.aestheticnetwork.tpa;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.Type;
import org.yuri.aestheticnetwork.Utils;

import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static org.yuri.aestheticnetwork.Utils.translate;

@SuppressWarnings("deprecation")
public class PayCommand implements CommandExecutor, TabCompleter {
    private Economy economy;

    public PayCommand(Economy eco) {
        this.economy = eco;
    }

    public String formatValue(float value) {
        String[] arr = {"", "k", "M", "B"};
        int index = 0;
        while ((value / 1000) >= 1) {
            value = value / 1000;
            index++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s", decimalFormat.format(value), arr[index]);
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        if (args.length == 0) {
            user.sendMessage(translate("&7You must specify who you want to pay."));
            return true;
        }

        if (args.length == 1) {
            user.sendMessage(translate("&7You must specify how much you want to pay."));
            return true;
        }

        if (args[0].equalsIgnoreCase(sender.getName())) {
            user.sendMessage(translate("&7You can't pay yourself!"));
            return true;
        }

        OfflinePlayer offr = Bukkit.getOfflinePlayer(args[0]);
        Player onr = Bukkit.getPlayer(args[0]);
        String str = String.valueOf(args[1]).toLowerCase();

        int totalbal = 0;
        int index = str.indexOf("k");
        String value = str.substring(0, index);
        if (!value.equals("")) {
            totalbal += Integer.parseInt(value) * 1000;
            str = str.replace(value + "k", "");
        }

        int index2 = str.indexOf("m");
        String value2 = str.substring(0, index2);
        if (!value2.equals("")) {
            totalbal += Integer.parseInt(value2) * 1000000;
            str = str.replace(value2 + "m", "");
        }

        if (!str.equals(""))
            totalbal += Integer.parseInt(str);

        double pbal = this.economy.getBalance(user);
        double cash = totalbal;
        String formattedcash = String.format("%,.2f", cash);

        if (totalbal > pbal) {
            user.sendMessage(translate("&7You don't have &a$" + formattedcash + "!"));
            return true;
        }

        EconomyResponse er = this.economy.depositPlayer(Objects.requireNonNullElse(onr, offr), totalbal);

        if (er.transactionSuccess() && onr != null)
            onr.sendMessage(translate("&c" + user.getName() + " &7sent you &a$" + formattedcash + "!"));

        EconomyResponse eru = this.economy.withdrawPlayer(user, totalbal);
        if (eru.transactionSuccess())
            user.sendMessage(translate("&7Sent &c" + offr.getName() + " &a$" + formattedcash + "!"));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("pay")) {
            if (args.length == 0)
                return Bukkit.getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.toList());
            else if (args.length == 1)
                return Bukkit.getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.toList());
            else if (args.length == 2)
            {
                return List.of("1");
            }
        }

        return Collections.emptyList();
    }
}
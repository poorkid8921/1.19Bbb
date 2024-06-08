package main.commands.essentials;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PlayTime implements CommandExecutor, TabExecutor {
    public static String getTime(Player player) {
        long seconds = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        final long days = hours / 24;
        final StringBuilder builder = new StringBuilder();
        if (days > 0)
            builder.append(days).append(" ").append(days > 1 ? "days" : "day");
        hours %= 24;
        minutes %= 60;
        if (hours > 0)
            builder.append(" ").append(hours).append(" ").append(hours > 1 ? "hours" : "hour");
        if (minutes > 0)
            builder.append(" ").append(minutes).append(" ").append(minutes > 1 ? "minutes" : "minute");
        if (days == 0) {
            seconds %= 60;
            if (seconds > 0)
                builder.append(" ").append(seconds).append(" ").append(seconds > 1 ? "seconds" : "second");
        }
        return builder.toString().trim();
    }

    public static String getTimeLB(long ms) {
        long minutes = (ms / 20) / 60;
        long hours = minutes / 60;
        final long days = hours / 24;
        final StringBuilder builder = new StringBuilder();
        if (days > 0)
            builder.append(days).append(" ").append(days > 1 ? "days" : "day");
        hours %= 24;
        minutes %= 60;
        if (hours > 0)
            builder.append(" ").append(hours).append(" ").append(hours > 1 ? "hours" : "hour");
        if (minutes > 0)
            builder.append(" ").append(minutes).append(" ").append(minutes > 1 ? "minutes" : "minute");
        return builder.toString().trim();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage("§7You must specify a valid player!");
                return true;
            }
            sender.sendMessage("§6Playtime of " + player.getName() + ": §c" + getTime(player));
            return true;
        }
        sender.sendMessage("§6Playtime: §c" + getTime((Player) sender));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length < 2 ? null : Collections.emptyList();
    }
}
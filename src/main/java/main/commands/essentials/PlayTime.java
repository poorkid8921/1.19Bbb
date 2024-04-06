package main.commands.essentials;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class PlayTime implements CommandExecutor, TabExecutor {
    public static String getTime(Player p) {
        StringBuilder builder = new StringBuilder();
        int seconds = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        int days = seconds / 86400;
        if (days > 0)
            builder.append(days).append(days > 1 ? " days " : " day ");

        seconds %= 86400;
        long hours = seconds / 3600;
        if (hours > 0)
            builder.append(hours).append(" ").append(hours > 1 ? "hours " : "hour ");
        long minutes = (seconds / 60) % 60;
        if (minutes > 0)
            builder.append(minutes).append(" ").append(minutes > 1 ? "minutes " : "minute ");
        if (days == 0) {
            seconds %= 60;
            if (seconds > 0)
                builder.append(seconds).append(" ").append(seconds > 1 ? "seconds" : "second");
        }
        return builder.toString();
    }

    public static String getTime(long ms) {
        StringBuilder builder = new StringBuilder();
        int seconds = (int) (ms / 1000);
        int days = seconds / 86400;
        if (days > 0) builder.append(days).append(days > 1 ? " days" : " day");

        seconds %= 86400;
        long hours = seconds / 3600;
        if (hours > 0)
            builder.append(builder.length() > 0 ? ", " + hours : hours).append(hours > 1 ? " hours" : " hour");
        long minutes = (seconds / 60) % 60;
        if (minutes > 0)
            builder.append(builder.length() > 0 ? ", " + minutes : minutes).append(minutes > 1 ? " minutes" : " minute");
        if (days == 0) {
            seconds %= 60;
            if (seconds > 0)
                builder.append(builder.length() > 0 ? ", " + seconds : seconds).append(seconds > 1 ? " seconds" : " second");
        }
        return builder.toString();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage("§7You must specify a valid player!");
                return true;
            }
            sender.sendMessage("§6Playtime of " + p.getName() + ": §c" + getTime(p));
            return true;
        }
        sender.sendMessage("§6Playtime: §c" + getTime((Player) sender));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length < 2 ? null : Collections.emptyList();
    }
}
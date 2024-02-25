package main.commands.essentials;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayTime implements CommandExecutor {
    private static String getTime(Player p) {
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            Player p = (Player) Bukkit.getOfflinePlayer(args[0]);
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
}
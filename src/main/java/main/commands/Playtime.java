package main.commands;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Constants.MAIN_COLOR;

public class Playtime implements CommandExecutor {
    private String getTime(Player p) {
        StringBuilder builder = new StringBuilder();
        int seconds = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        int days = seconds / 86400;
        if (days > 0) {
            builder.append(days).append(" ").append(days > 1 ? "days" : "day");
        }

        seconds %= 86400;
        long hours = seconds / 3600;
        if (hours > 0) {
            builder.append(builder.length() == 0 ? hours + " " + (hours > 1 ? "hours" : "hour") :
                    " " + hours + " " + (hours > 1 ? "hours " : "hour "));
        }

        long minutes = (seconds / 60) % 60;
        if (minutes > 0) {
            builder.append(builder.length() == 0 ? minutes + " " + (minutes > 1 ? "minutes" : "minute") :
                    " " + minutes + " " + (minutes > 1 ? "minutes " : "minute "));
        }

        seconds %= 60;
        if (seconds > 0) {
            builder.append(seconds).append(seconds > 1 ? "seconds" : "second");
        }
        return builder.toString();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage("§7You can't view the playtime of offline players.");
                return true;
            }
            sender.sendMessage(MAIN_COLOR + p.getName() + "§7's playtime is " + MAIN_COLOR + getTime(p));
            return true;
        }
        sender.sendMessage("§7Your playtime is " + MAIN_COLOR + getTime((Player) sender));
        return true;
    }
}
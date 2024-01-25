package main.commands;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

import static main.utils.Constants.MAIN_COLOR;

public class Playtime implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage("§7You can't view playtime of offline players.");
                return true;
            }

            StringBuilder strb = new StringBuilder();
            int seconds = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
            int i = 0;
            int days = seconds / (86400);
            if (days > 0) {
                i++;
                strb.append(days).append(" ").append(days > 1 ? "days" : "day");
            }

            seconds %= 86400;
            long hours = seconds / 3600;
            if (hours > 0) {
                i++;
                strb.append(strb.length() == 0 ? hours + " " + (hours > 1 ? "hours" : "hour") :
                        " " + hours + " " + (hours > 1 ? "hours " : "hour "));
            }

            long minutes = (seconds / 60) % 60;
            if (minutes > 0) {
                i++;
                strb.append(strb.length() == 0 ? minutes + " " + (minutes > 1 ? "minutes" : "minute") :
                        " " + minutes + " " + (minutes > 1 ? "minutes " : "minute "));
            }

            seconds %= 60;
            if (seconds > 0) {
                if (i > 0)
                    strb.append(" ");
                strb.append(seconds).append(" ").append(seconds > 1 ? "seconds" : "second");
            }
            sender.sendMessage(MAIN_COLOR + p.getName() + "'s §7playtime is " + MAIN_COLOR + strb);
            return true;
        }
        StringBuilder strb = new StringBuilder();
        int seconds = ((Player) sender).getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        int i = 0;
        int days = seconds / (86400);
        if (days > 0) {
            i++;
            strb.append(days).append(" ").append(days > 1 ? "days" : "day");
        }

        seconds %= 86400;
        long hours = seconds / 3600;
        if (hours > 0) {
            i++;
            strb.append(strb.length() == 0 ? hours + " " + (hours > 1 ? "hours" : "hour") :
                    " " + hours + " " + (hours > 1 ? "hours " : "hour "));
        }

        long minutes = (seconds / 60) % 60;
        if (minutes > 0) {
            i++;
            strb.append(strb.length() == 0 ? minutes + " " + (minutes > 1 ? "minutes" : "minute") :
                    " " + minutes + " " + (minutes > 1 ? "minutes " : "minute "));
        }

        seconds %= 60;
        if (seconds > 0) {
            if (i > 0)
                strb.append(" ");
            strb.append(seconds).append(" ").append(seconds > 1 ? "seconds" : "second");
        }        sender.sendMessage("§7Your playtime is" + MAIN_COLOR + strb);
        return true;
    }
}
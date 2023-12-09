package main.commands.ess;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

import static main.utils.Languages.MAIN_COLOR;

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
            long stat = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;

            long days = TimeUnit.SECONDS.toDays(stat);
            if (days != 0)
                strb.append(days + days > 1 ? " days" : " day");

            long hours = TimeUnit.SECONDS.toHours(stat) - TimeUnit.DAYS.toHours(days);
            if (hours != 0)
                strb.append(hours + hours > 1 ? " hours" : " hour");

            long minutes = TimeUnit.SECONDS.toMinutes(stat) -
                    TimeUnit.DAYS.toMinutes(days) -
                    TimeUnit.HOURS.toMinutes(hours);
            if (minutes != 0)
                strb.append(minutes + minutes > 1 ? " minutes" : " minute");
            sender.sendMessage(MAIN_COLOR + p.getName() + "'s §7playtime is" + MAIN_COLOR + strb);
            return true;
        }
        StringBuilder strb = new StringBuilder();
        long stat = ((Player) sender).getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;

        long days = TimeUnit.SECONDS.toDays(stat);
        if (days != 0)
            strb.append(days + days > 1 ? " days" : " day");

        long hours = TimeUnit.SECONDS.toHours(stat) - TimeUnit.DAYS.toHours(days);
        if (hours != 0)
            strb.append(hours + hours > 1 ? " hours" : " hour");

        long minutes = TimeUnit.SECONDS.toMinutes(stat) -
                TimeUnit.DAYS.toMinutes(days) -
                TimeUnit.HOURS.toMinutes(hours);
        if (minutes != 0)
            strb.append(minutes + minutes > 1 ? " minutes" : " minute");
        sender.sendMessage("§7Your playtime is" + MAIN_COLOR + strb);
        return true;
    }
}

package main.commands.ess;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Languages.MAIN_COLOR;

public class Playtime implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        StringBuilder strb = new StringBuilder();
        long stat = ((Player) sender).getStatistic(Statistic.PLAY_ONE_MINUTE);

        boolean days = false;
        if (stat % 24 == 0) {
            strb.append(" ").append(stat / 24 / 60).append(" days");
            days = true;
        }

        if (stat % 60 == 0)
            if (days) strb.append(" and ").append(stat / 60).append(" hours");
            else
                strb.append(" ").append(stat / 60).append(" hours");
        else if (days) strb.append(" and ").append(stat).append(" minutes");
        else
            strb.append(" ").append(stat).append(" minutes");
        if (args.length > 0) {
            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage("§7You can't view playtime of offline players.");
                return true;
            }

            sender.sendMessage(MAIN_COLOR + p.getName() + "'s §7playtime is" + MAIN_COLOR + strb);
            return true;
        }
        sender.sendMessage("§7Your playtime is" + MAIN_COLOR + strb);
        return true;
    }
}

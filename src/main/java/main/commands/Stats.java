package main.commands;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Initializer.MAIN_COLOR;

@SuppressWarnings("deprecation")
public class Stats implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player pp) {
            if (args.length == 0) showstats(pp);
            else {
                Player p = Bukkit.getOfflinePlayer(args[0]).getPlayer();
                if (p == null) {
                    pp.sendMessage("§7You must specify a valid player you want to view statistics of.");
                    return true;
                }

                showstats(pp, p);
            }
        }

        return true;
    }

    private void showstats(Player pp) {
        pp.sendMessage(
                "§7ʏᴏᴜʀ sᴛᴀᴛɪsᴛɪᴄs ᴀʀᴇ:",
                "  §7ᴋɪʟʟs " + MAIN_COLOR + "» " + pp.getStatistic(Statistic.PLAYER_KILLS),
                "  §7ᴅᴇᴀᴛʜs " + MAIN_COLOR + "» " + pp.getStatistic(Statistic.DEATHS),
                "  §7ᴅᴀᴍᴀɢᴇ ᴅᴇᴀʟᴛ " + MAIN_COLOR + "» " + pp.getStatistic(Statistic.DAMAGE_DEALT),
                "  §7ᴅᴀᴍᴀɢᴇ ᴛᴀᴋᴇɴ " + MAIN_COLOR + "» " + pp.getStatistic(Statistic.DAMAGE_TAKEN));
    }

    private void showstats(Player pp, Player t) {
        pp.sendMessage(
                (t.isOnline() ? "§a◆ " : "§c◆ ") + MAIN_COLOR +
                        t.getDisplayName() + "§7's sᴛᴀᴛɪsᴛɪᴄs ᴀʀᴇ:",
                "  §7ᴋɪʟʟs " + MAIN_COLOR + "» " + pp.getStatistic(Statistic.PLAYER_KILLS),
                "  §7ᴅᴇᴀᴛʜs " + MAIN_COLOR + "» " + pp.getStatistic(Statistic.DEATHS),
                "  §7ᴅᴀᴍᴀɢᴇ ᴅᴇᴀʟᴛ " + MAIN_COLOR + "» " + pp.getStatistic(Statistic.DAMAGE_DEALT),
                "  §7ᴅᴀᴍᴀɢᴇ ᴛᴀᴋᴇɴ " + MAIN_COLOR + "» " + pp.getStatistic(Statistic.DAMAGE_TAKEN));
    }
}
package main.commands;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Constants.MAIN_COLOR;

@SuppressWarnings("deprecation")
public class Stats implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) showStats(player);
            else {
                Player p = Bukkit.getOfflinePlayer(args[0]).getPlayer();
                if (p == null) {
                    player.sendMessage("§7You must specify a valid player you want to view statistics of.");
                    return true;
                }

                showStats(player, p);
            }
        }

        return true;
    }

    private void showStats(Player player) {
        player.sendMessage(
                "§7ʏᴏᴜʀ sᴛᴀᴛɪsᴛɪᴄs ᴀʀᴇ:",
                "  §7ᴋɪʟʟs " + MAIN_COLOR + "» " + player.getStatistic(Statistic.PLAYER_KILLS),
                "  §7ᴅᴇᴀᴛʜs " + MAIN_COLOR + "» " + player.getStatistic(Statistic.DEATHS),
                "  §7ᴅᴀᴍᴀɢᴇ ᴅᴇᴀʟᴛ " + MAIN_COLOR + "» " + player.getStatistic(Statistic.DAMAGE_DEALT),
                "  §7ᴅᴀᴍᴀɢᴇ ᴛᴀᴋᴇɴ " + MAIN_COLOR + "» " + player.getStatistic(Statistic.DAMAGE_TAKEN));
    }

    private void showStats(Player player, Player t) {
        player.sendMessage(
                (t.isOnline() ? "§a◆ " : "§c◆ ") + MAIN_COLOR +
                        t.getDisplayName() + "§7's sᴛᴀᴛɪsᴛɪᴄs ᴀʀᴇ:",
                "  §7ᴋɪʟʟs " + MAIN_COLOR + "» " + t.getStatistic(Statistic.PLAYER_KILLS),
                "  §7ᴅᴇᴀᴛʜs " + MAIN_COLOR + "» " + t.getStatistic(Statistic.DEATHS),
                "  §7ᴅᴀᴍᴀɢᴇ ᴅᴇᴀʟᴛ " + MAIN_COLOR + "» " + t.getStatistic(Statistic.DAMAGE_DEALT),
                "  §7ᴅᴀᴍᴀɢᴇ ᴛᴀᴋᴇɴ " + MAIN_COLOR + "» " + t.getStatistic(Statistic.DAMAGE_TAKEN));
    }
}
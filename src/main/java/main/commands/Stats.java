package main.commands;

import main.utils.Constants;
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
        if (sender instanceof Player p) {
            if (args.length == 0) {
                sender.sendMessage(
                        "§7ʏᴏᴜʀ sᴛᴀᴛɪsᴛɪᴄs ᴀʀᴇ:",
                        "  §7ʙᴀʟᴀɴᴄᴇ " + MAIN_COLOR + "» $" + String.format("%,.2f", Constants.economy.getBalance(p)),
                        "  §7ᴋɪʟʟs " + MAIN_COLOR + "» " + p.getStatistic(Statistic.PLAYER_KILLS),
                        "  §7ᴅᴇᴀᴛʜs " + MAIN_COLOR + "» " + p.getStatistic(Statistic.DEATHS));
            } else {
                p = Bukkit.getOfflinePlayer(args[0]).getPlayer();
                if (p == null) {
                    sender.sendMessage("§7You must specify a valid player you want to view statistics of.");
                    return true;
                }

                sender.sendMessage(
                        MAIN_COLOR + p.getDisplayName() + "'s §7sᴛᴀᴛɪsᴛɪᴄs ᴀʀᴇ:",
                        "  §7ʙᴀʟᴀɴᴄᴇ " + MAIN_COLOR + "» $" + String.format("%,.2f", Constants.economy.getBalance(p)),
                        "  §7ᴋɪʟʟs " + MAIN_COLOR + "» " + p.getStatistic(Statistic.PLAYER_KILLS),
                        "  §7ᴅᴇᴀᴛʜs " + MAIN_COLOR + "» " + p.getStatistic(Statistic.DEATHS));
            }
        }

        return true;
    }
}
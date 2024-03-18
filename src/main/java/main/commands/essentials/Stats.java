package main.commands.essentials;

import main.utils.instances.CustomPlayerDataHolder;
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

import static main.utils.Initializer.*;
import static main.utils.Utils.*;

public class Stats implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {
            if (args.length == 0) {
                sender.sendMessage(
                        "§7ʏᴏᴜʀ sᴛᴀᴛɪsᴛɪᴄs ᴀʀᴇ:",
                        "  §7ʙᴀʟᴀɴᴄᴇ " + MAIN_COLOR + "» $" + shortFormat(economyHandler.getBalance(p), 0),//playerData.get(sender.getName()).getMoney()),
                        "  §7ʙᴏᴜɴᴛʏ " + MAIN_COLOR + "» " + shortFormat(playerData.get(sender.getName()).getBounty(), 0),
                        "  §7ᴋɪʟʟs " + MAIN_COLOR + "» " + p.getStatistic(Statistic.PLAYER_KILLS),
                        "  §7ᴅᴇᴀᴛʜs " + MAIN_COLOR + "» " + p.getStatistic(Statistic.DEATHS),
                        "  §7ᴘʟᴀʏᴛɪᴍᴇ " + MAIN_COLOR + "» " + getTime(p));
            } else {
                p = Bukkit.getPlayer(args[0]);
                if (p == null) {
                    sender.sendMessage("§7You can only view statistics of online players!");
                    return true;
                }
                String name = p.getName();
                CustomPlayerDataHolder D0 = playerData.get(name);
                sender.sendMessage(
                        MAIN_COLOR + D0.getFRank(name) + "§7's sᴛᴀᴛɪsᴛɪᴄs ᴀʀᴇ:",
                        "  §7ʙᴀʟᴀɴᴄᴇ " + MAIN_COLOR + "» $" + shortFormat(economyHandler.getBalance(p), 0),//playerData.get(p.getName()).getMoney()),
                        "  §7ʙᴏᴜɴᴛʏ " + MAIN_COLOR + "» " + shortFormat(D0.getBounty(), 0),
                        "  §7ᴋɪʟʟs " + MAIN_COLOR + "» " + p.getStatistic(Statistic.PLAYER_KILLS),
                        "  §7ᴅᴇᴀᴛʜs " + MAIN_COLOR + "» " + p.getStatistic(Statistic.DEATHS),
                        "  §7ᴘʟᴀʏᴛɪᴍᴇ " + MAIN_COLOR + "» " + getTime(p));
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length < 2 ? null : Collections.emptyList();
    }
}
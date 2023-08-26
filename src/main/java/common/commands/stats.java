package common.commands;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.yuri.eco.utils.Utils.translateo;

public class stats implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player pp) {
            if (args.length == 0)
                showstats(pp);
            else {
                Player p2 = (Player) Bukkit.getOfflinePlayer(args[0]);
                if (!p2.isOnline()) {
                    pp.sendMessage(translateo("&7You must specify a valid player."));
                    return true;
                }

                showstats(pp, p2);
                return true;
            }
            return true;
        }

        return false;
    }

    private void showstats(Player pp) {
        pp.sendMessage(translateo("&7ʏᴏᴜʀ sᴛᴀᴛɪsᴛɪᴄs ᴀʀᴇ:"));
        pp.sendMessage(translateo("&7ᴋɪʟʟs: &c" + pp.getStatistic(Statistic.PLAYER_KILLS)));
        pp.sendMessage(translateo("&7ᴅᴇᴀᴛʜs: &c" + pp.getStatistic(Statistic.DEATHS)));
        pp.sendMessage(translateo("&7ᴅᴀᴍᴀɢᴇ ᴅᴇᴀʟᴛ: &c" + pp.getStatistic(Statistic.DAMAGE_DEALT)));
        pp.sendMessage(translateo("&7ᴅᴀᴍᴀɢᴇ ᴛᴀᴋᴇɴ: &c" + pp.getStatistic(Statistic.DAMAGE_TAKEN)));
    }

    private void showstats(Player pp, Player t) {
        pp.sendMessage(translateo("&c" + t.getDisplayName() + "&7's sᴛᴀᴛɪsᴛɪᴄs ᴀʀᴇ:"));
        pp.sendMessage(translateo("&7ᴋɪʟʟs: &c" + t.getStatistic(Statistic.PLAYER_KILLS)));
        pp.sendMessage(translateo("&7ᴅᴇᴀᴛʜs: &c" + t.getStatistic(Statistic.DEATHS)));
        pp.sendMessage(translateo("&7ᴅᴀᴍᴀɢᴇ ᴅᴇᴀʟᴛ: &c" + t.getStatistic(Statistic.DAMAGE_DEALT)));
        pp.sendMessage(translateo("&7ᴅᴀᴍᴀɢᴇ ᴛᴀᴋᴇɴ: &c" + t.getStatistic(Statistic.DAMAGE_TAKEN)));
    }
}
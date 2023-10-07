package commands;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import main.utils.Initializer;

import static main.utils.Utils.translate;
import static main.utils.Utils.translateo;

@SuppressWarnings("deprecation")
public class Stats implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player pp) {
            if (args.length == 0) showstats(pp);
            else {
                Player p = Bukkit.getOfflinePlayer(args[0]).getPlayer();
                if (p == null) {
                    pp.sendMessage(translateo("&7You must specify a valid player you want to view statistics of."));
                    return true;
                }

                showstats(pp, p);
            }
        }

        return true;
    }

    private void showstats(Player pp) {
        pp.sendMessage("",
                "&7ʏᴏᴜʀ sᴛᴀᴛɪsᴛɪᴄs ᴀʀᴇ:",
                "  &7ʙᴀʟᴀɴᴄᴇ #fc282f» $" + String.format("%,.2f", Initializer.economy.getBalance(pp)),
                "  &7ᴋɪʟʟs #fc282f» " + pp.getStatistic(Statistic.PLAYER_KILLS),
                "  &7ᴅᴇᴀᴛʜs #fc282f» " + pp.getStatistic(Statistic.DEATHS),
                "  &7ᴅᴀᴍᴀɢᴇ ᴅᴇᴀʟᴛ #fc282f» " + pp.getStatistic(Statistic.DAMAGE_DEALT),
                "  &7ᴅᴀᴍᴀɢᴇ ᴛᴀᴋᴇɴ #fc282f» " + pp.getStatistic(Statistic.DAMAGE_TAKEN));
    }

    private void showstats(Player pp, Player t) {
        pp.sendMessage("",
                translate((t.isOnline() ? "&a" : "&c") + "◆ #fc282f" +
                        t.getDisplayName() + "&7's sᴛᴀᴛɪsᴛɪᴄs ᴀʀᴇ:"),
                "  &7ʙᴀʟᴀɴᴄᴇ #fc282f» $" + String.format("%,.2f", Initializer.economy.getBalance(pp)),
                "  &7ᴋɪʟʟs #fc282f» " + pp.getStatistic(Statistic.PLAYER_KILLS),
                "  &7ᴅᴇᴀᴛʜs #fc282f» " + pp.getStatistic(Statistic.DEATHS),
                "  &7ᴅᴀᴍᴀɢᴇ ᴅᴇᴀʟᴛ #fc282f» " + pp.getStatistic(Statistic.DAMAGE_DEALT),
                "  &7ᴅᴀᴍᴀɢᴇ ᴛᴀᴋᴇɴ #fc282f» " + pp.getStatistic(Statistic.DAMAGE_TAKEN));
    }
}
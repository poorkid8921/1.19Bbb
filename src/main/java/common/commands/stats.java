package common.commands;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.yuri.eco.utils.Utils.translate;
import static org.yuri.eco.utils.Utils.translateo;

@SuppressWarnings("deprecation")
public class stats implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player pp) {
            if (args.length == 0) showstats(pp);
            else {
                Player p2 = (Player) Bukkit.getOfflinePlayer(args[0]);
                if (p2 == null) {
                    pp.sendMessage(translateo("&7You must specify a valid player"));
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
        for (String msg : List.of("", translateo("&7ʏᴏᴜʀ sᴛᴀᴛɪsᴛɪᴄs ᴀʀᴇ:"), translate("  &7ᴋɪʟʟs #fc282f» " + pp.getStatistic(Statistic.PLAYER_KILLS)), translate("  &7ᴅᴇᴀᴛʜs #fc282f» " + pp.getStatistic(Statistic.DEATHS)), translate("  &7ᴅᴀᴍᴀɢᴇ ᴅᴇᴀʟᴛ #fc282f» " + pp.getStatistic(Statistic.DAMAGE_DEALT)), translate("  &7ᴅᴀᴍᴀɢᴇ ᴛᴀᴋᴇɴ #fc282f» " + pp.getStatistic(Statistic.DAMAGE_TAKEN))))
            pp.sendMessage(msg);
    }

    private void showstats(Player pp, Player t) {
        for (String msg : List.of("", translate((t.isOnline() ? "&a" : "&c") + "◆ #fc282f" + t.getDisplayName() + "&7's sᴛᴀᴛɪsᴛɪᴄs ᴀʀᴇ:"), translate("  &7ᴋɪʟʟs #fc282f» " + t.getStatistic(Statistic.PLAYER_KILLS)), translate("  &7ᴅᴇᴀᴛʜs #fc282f» " + t.getStatistic(Statistic.DEATHS)), translate("  &7ᴅᴀᴍᴀɢᴇ ᴅᴇᴀʟᴛ #fc282f» " + t.getStatistic(Statistic.DAMAGE_DEALT)), translate("  &7ᴅᴀᴍᴀɢᴇ ᴛᴀᴋᴇɴ #fc282f» " + t.getStatistic(Statistic.DAMAGE_TAKEN))))
            pp.sendMessage(msg);
    }
}
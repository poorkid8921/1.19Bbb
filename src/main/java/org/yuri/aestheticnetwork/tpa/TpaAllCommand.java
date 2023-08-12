package org.yuri.aestheticnetwork.tpa;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.Type;
import org.yuri.aestheticnetwork.Utils;

import static org.yuri.aestheticnetwork.Utils.*;

@SuppressWarnings("deprecation")
public class TpaAllCommand implements CommandExecutor {
    public TpaAllCommand() {
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        user.sendMessage(translate("&7Requested everyone to teleport to you."));

        for (Player i : Bukkit.getOnlinePlayers()) {
            if (i.getUniqueId().equals(user.getUniqueId()))
                continue;

            TpaRequest tpr = getRequest(i);

            if ((tpr != null && tpr.getSender().equals(sender)) || Utils.manager().get(
                    "r." + i.getUniqueId() + ".t") != null)
                continue;

            requests.remove(tpr);
            Utils.addRequest(user, i, Type.TPAHERE, false);
        }
        return true;
    }
}
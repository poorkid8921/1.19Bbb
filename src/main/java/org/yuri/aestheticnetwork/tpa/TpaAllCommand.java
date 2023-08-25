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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.yuri.aestheticnetwork.Utils.*;

@SuppressWarnings("deprecation")
public class TpaAllCommand implements CommandExecutor {
    public TpaAllCommand() {
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

                if (cooldownPlayers.contains(user.getUniqueId())) {
            user.sendMessage(translate("&cYou are still on cooldown for teleporting everyone."));
            return true;
        }

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

        cooldownPlayers.add(user.getUniqueId());
        startCooldownTimer(user);

        return true;
    }

    private void startCooldownTimer(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                cooldownPlayers.remove(player.getUniqueId());
                player.sendMessage(translate("&aTeleport-all cooldown has ended."));
            }
        }.runTaskLater(AestheticNetwork.getInstance(), 20 * 60); 
    }
}

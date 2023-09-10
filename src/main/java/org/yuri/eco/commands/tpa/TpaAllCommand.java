package org.yuri.eco.commands.tpa;

import common.commands.tpa.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.eco.utils.Utils;

import static org.yuri.eco.utils.Utils.getRequest;
import static org.yuri.eco.utils.Utils.translateo;

public class TpaAllCommand implements CommandExecutor {
    public TpaAllCommand() {
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user)) return true;

        user.sendMessage(translateo("&7Requested everyone to teleport to you"));

        for (Player i : Bukkit.getOnlinePlayers()) {
            if (i.getName().equals(user.getName())) continue;

            TpaRequest tpr = getRequest(i.getName());

            if ((tpr != null && tpr.getSender().equals(sender)) || Utils.manager().get("r." + i.getUniqueId() + ".t") != null)
                continue;

            Utils.addRequest(user, i, Utils.Type.TPAHERE, false);
        }
        return true;
    }
}
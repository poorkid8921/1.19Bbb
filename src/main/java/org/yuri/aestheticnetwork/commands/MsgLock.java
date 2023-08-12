package org.yuri.aestheticnetwork.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.utils.Utils;

public class MsgLock implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = ((Player) sender);
        if (Utils.manager1().get("r." + p.getUniqueId() + ".m") != null) {
            p.sendMessage(Utils.translate("&7You can receive messages from players again."));
            Utils.manager1().set("r." + p.getUniqueId() + (Utils.manager1().get("r." + p.getUniqueId() + ".t") == null ? "" : ".m"), null);
            AestheticNetwork.getInstance().saveCustomConfig1();
            AestheticNetwork.msg.add(p.getName());
        } else {
            p.sendMessage(Utils.translate("&7You will no longer receive messages from players."));
            Utils.manager1().set("r." + p.getUniqueId() + ".m", "");
            AestheticNetwork.getInstance().saveCustomConfig1();
            AestheticNetwork.msg.remove(p.getName());
        }
        return true;
    }
}

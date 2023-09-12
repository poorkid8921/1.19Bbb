package org.yuri.aestheticnetwork.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.utils.Initializer;
import org.yuri.aestheticnetwork.utils.Languages;
import org.yuri.aestheticnetwork.utils.Utils;

public class MsgLock implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = ((Player) sender);
        if (Utils.manager1().get("r." + p.getName() + ".m") != null) {
            p.sendMessage(Languages.MSGLOCK);
            Utils.manager1().set("r." + p.getName() + (Utils.manager1().get("r." + p.getName() + ".t") == null ? "" : ".m"), null);
            AestheticNetwork.getInstance().saveCustomConfig1();
            Initializer.msg.add(p.getName());
        } else {
            p.sendMessage(Languages.MSGLOCK1);
            Utils.manager1().set("r." + p.getName() + ".m", "");
            AestheticNetwork.getInstance().saveCustomConfig1();
            Initializer.msg.remove(p.getName());
        }
        return true;
    }
}

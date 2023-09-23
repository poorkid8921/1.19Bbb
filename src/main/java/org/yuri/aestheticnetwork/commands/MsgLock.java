package org.yuri.aestheticnetwork.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.utils.Messages.Initializer;
import org.yuri.aestheticnetwork.utils.Messages.Languages;
import org.yuri.aestheticnetwork.utils.Utils;

public class MsgLock implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String p = sender.getName();
        if (Utils.manager1().get("r." + p + ".m") != null) {
            sender.sendMessage(Languages.MSGLOCK);
            Utils.manager1().set("r." + p + 
                    (Utils.manager1().get("r." + p + ".t") == null ? "" : ".m"), null);
            AestheticNetwork.getInstance().saveCustomConfig1();
            Initializer.msg.add(p);
        } else {
            sender.sendMessage(Languages.MSGLOCK1);
            Utils.manager1().set("r." + p + ".m", "");
            AestheticNetwork.getInstance().saveCustomConfig1();
            Initializer.msg.remove(p);
        }
        return true;
    }
}

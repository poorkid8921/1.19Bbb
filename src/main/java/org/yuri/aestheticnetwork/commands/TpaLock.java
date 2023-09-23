package org.yuri.aestheticnetwork.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.utils.Messages.Languages;
import org.yuri.aestheticnetwork.utils.Utils;

import static org.yuri.aestheticnetwork.utils.Messages.Initializer.tpa;

public class TpaLock implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String p = sender.getName();
        if (Utils.manager1().get("r." + p + ".t") != null) {
            sender.sendMessage(Languages.TPALOCK);
            Utils.manager1().set("r." + p + (Utils.manager1().get("r." + p + ".m") == null ? "" : ".t"), null);
            AestheticNetwork.getInstance().saveCustomConfig1();
            tpa.add(p);
        }
        else
        {
            sender.sendMessage(Languages.TPALOCK1);
            Utils.manager1().set("r." + p + ".t", "");
            AestheticNetwork.getInstance().saveCustomConfig1();
            tpa.remove(p);
        }
        return true;
    }
}

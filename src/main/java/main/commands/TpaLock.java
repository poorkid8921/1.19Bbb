package main.commands;

import main.AestheticNetwork;
import main.utils.Messages.Initializer;
import main.utils.Messages.Languages;
import main.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TpaLock implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = sender.getName();
        if (Utils.manager().get("r." + p + ".t") != null) {
            sender.sendMessage(Languages.TPALOCK);
            Utils.manager().set("r." + p + (Utils.manager().get("r." + p + ".m") == null ? "" : ".t"), null);
            AestheticNetwork.getInstance().saveCustomConfig1();
            Initializer.tpa.add(p);
        }
        else
        {
            sender.sendMessage(Languages.TPALOCK1);
            Utils.manager().set("r." + p + ".t", "");
            AestheticNetwork.getInstance().saveCustomConfig1();
            Initializer.tpa.remove(p);
        }
        return true;
    }
}

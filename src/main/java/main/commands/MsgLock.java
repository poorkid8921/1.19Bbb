package main.commands;

import main.AestheticNetwork;
import main.utils.Messages.Initializer;
import main.utils.Messages.Languages;
import main.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MsgLock implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = sender.getName();
        if (Utils.manager().get("r." + p + ".m") != null) {
            sender.sendMessage(Languages.MSGLOCK);
            Utils.manager().set("r." + p + 
                    (Utils.manager().get("r." + p + ".t") == null ? "" : ".m"), null);
            AestheticNetwork.getInstance().saveCustomConfig1();
            Initializer.msg.add(p);
        } else {
            sender.sendMessage(Languages.MSGLOCK1);
            Utils.manager().set("r." + p + ".m", "");
            AestheticNetwork.getInstance().saveCustomConfig1();
            Initializer.msg.remove(p);
        }
        return true;
    }
}

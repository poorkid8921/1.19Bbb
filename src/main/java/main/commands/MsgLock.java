package main.commands;

import main.Practice;
import main.utils.Initializer;
import main.utils.Languages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MsgLock implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = sender.getName();
        if (Practice.cc1.get("r." + p + ".m") != null) {
            sender.sendMessage(Languages.MSGLOCK);
            Practice.cc1.set("r." + p +
                    (Practice.cc1.get("r." + p + ".t") == null ? "" : ".m"), null);
            Initializer.p.saveCustomConfig1();
            Initializer.msg.add(p);
        } else {
            sender.sendMessage(Languages.MSGLOCK1);
            Practice.cc1.set("r." + p + ".m", "");
            Initializer.p.saveCustomConfig1();
            Initializer.msg.remove(p);
        }
        return true;
    }
}

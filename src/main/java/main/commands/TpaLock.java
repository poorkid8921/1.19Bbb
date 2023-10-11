package main.commands;

import main.Practice;
import main.utils.Initializer;
import main.utils.Languages;
import main.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TpaLock implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = sender.getName();
        if (Practice.cc1.get("r." + p + ".t") != null) {
            sender.sendMessage(Languages.TPALOCK);
            Practice.cc1.set("r." + p + (Practice.cc1.get("r." + p + ".m") == null ? "" : ".t"), null);
            Initializer.p.saveCustomConfig1();
            Initializer.tpa.add(p);
        }
        else
        {
            sender.sendMessage(Languages.TPALOCK1);
            Practice.cc1.set("r." + p + ".t", "");
            Initializer.p.saveCustomConfig1();
            Initializer.tpa.remove(p);
        }
        return true;
    }
}

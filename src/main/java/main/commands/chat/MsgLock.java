package main.commands.chat;

import main.Practice;
import main.utils.Initializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static main.utils.Initializer.msg;

public class MsgLock implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = sender.getName();
        if (Practice.config.get("r." + p + ".m") != null) {
            sender.sendMessage("§7You can receive messages from players again.");
            Practice.config.set("r." + p + (Practice.config.get("r." + p + ".t") == null ? "" : ".m"), null);
            Initializer.p.saveCustomConfig();
            msg.add(p);
        } else {
            sender.sendMessage("§7You will no longer receive messages from players.");
            Practice.config.set("r." + p + ".m", "");
            Initializer.p.saveCustomConfig();
            msg.remove(p);
        }

        return true;
    }
}

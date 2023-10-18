package commands.chat;

import main.Economy;
import main.utils.Initializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static main.utils.Initializer.msg;

public class MsgLock implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = sender.getName();
        if (Economy.cc.get("r." + p + ".m") != null) {
            sender.sendMessage("§7You can receive messages from players again.");
            Economy.cc.set("r." + p + (Economy.cc.get("r." + p + ".t") == null ? "" : ".m"), null);
            Initializer.p.saveCustomConfig();
            msg.add(p);
        } else {
            sender.sendMessage("§7You will no longer receive messages from players.");
            Economy.cc.set("r." + p + ".m", "");
            Initializer.p.saveCustomConfig();
            msg.remove(p);
        }

        return true;
    }
}

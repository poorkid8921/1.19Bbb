package commands.chat;

import main.Economy;
import main.utils.Initializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static main.utils.Initializer.tpa;

public class TpaLock implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = sender.getName();
        if (Economy.cc.get("r." + p + ".t") != null) {
            sender.sendMessage("§7You can receive tp requests again.");
            Economy.cc.set("r." + p + (Economy.cc.get("r." + p + ".m") == null ? "" : ".t"), null);
            Initializer.p.saveCustomConfig();
            tpa.add(p);
        } else {
            sender.sendMessage("§7You will no longer receive tp requests.");
            Economy.cc.set("r." + p + ".t", "");
            Initializer.p.saveCustomConfig();
            tpa.remove(p);
        }
        return true;
    }
}

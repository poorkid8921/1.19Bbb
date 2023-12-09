package main.commands.tpa;

import main.Practice;
import main.utils.Initializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static main.utils.Initializer.tpa;

public class TpaLock implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = sender.getName();
        if (Practice.config.get("r." + p + ".t") != null) {
            sender.sendMessage("§7You can receive tp requests again.");
            Practice.config.set("r." + p + (Practice.config.get("r." + p + ".m") == null ? "" : ".t"), null);
            Initializer.p.saveCustomConfig();
            tpa.add(p);
        } else {
            sender.sendMessage("§7You will no longer receive tp requests.");
            Practice.config.set("r." + p + ".t", "");
            Initializer.p.saveCustomConfig();
            tpa.remove(p);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}

package main.commands;

import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static main.utils.Initializer.playerData;
import static main.utils.Initializer.tpa;
public class TpaLock implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = sender.getName();
        CustomPlayerDataHolder T = playerData.get(p);
        if (T == null) {
            sender.sendMessage("§7You will no longer receive tp requests.");
            playerData.put(p, new CustomPlayerDataHolder(0, 1));
            tpa.remove(p);
        } else if (T.getT() == 0) {
            sender.sendMessage("§7You will no longer receive tp requests.");
            playerData.get(p).setT(1);
            tpa.remove(p);
        } else {
            sender.sendMessage("§7You can receive tp requests again.");
            playerData.get(p).setT(0);
            tpa.add(p);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}
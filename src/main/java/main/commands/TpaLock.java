package main.commands;

import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static main.utils.Constants.*;

public class TpaLock implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = sender.getName();
        CustomPlayerDataHolder T = playerData.get(p);
        if (T.getT() == 0) {
            sender.sendMessage(TPALOCK1);
            T.setT(1);
            tpa.remove(p);
        } else {
            sender.sendMessage(TPALOCK);
            T.setT(0);
            tpa.add(p);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}

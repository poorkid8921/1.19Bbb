package main.commands;

import com.google.common.collect.ImmutableList;
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
        if (T.getTptoggle() == 0) {
            sender.sendMessage("§7You will no longer receive tp requests from players.");
            T.setTptoggle(1);
            tpa.remove(p);
        } else {
            sender.sendMessage("§7You can receive tp requests again.");
            T.setTptoggle(0);
            tpa.add(p);
        }
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of();
    }
}

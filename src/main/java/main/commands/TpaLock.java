package main.commands;

import com.google.common.collect.ImmutableList;
import main.utils.Constants;
import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import static main.utils.Constants.playerData;
import static main.utils.Constants.tpa;

public class TpaLock implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String pn = sender.getName();
        CustomPlayerDataHolder T = playerData.get(pn);
        if (T.getTptoggle() == 0) {
            sender.sendMessage("§7You will no longer receive tp requests from players.");
            T.setTptoggle(1);
            tpa.remove(pn);
            Constants.tpa.sort(String::compareToIgnoreCase);
        } else {
            sender.sendMessage("§7You can receive tp requests again.");
            T.setTptoggle(0);
            tpa.add(pn);
            Constants.tpa.sort(String::compareToIgnoreCase);
        }
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of();
    }
}

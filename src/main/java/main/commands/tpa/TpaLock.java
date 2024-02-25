package main.commands.tpa;

import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;

import static main.utils.Initializer.playerData;
import static main.utils.Initializer.tpa;

public class TpaLock implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String pn = sender.getName();
        CustomPlayerDataHolder T = playerData.get(pn);
        if (T.getTptoggle() == 0) {
            sender.sendMessage("§7You will no longer receive tp requests.");
            playerData.get(pn).setTptoggle(1);
            tpa.remove(pn);
            tpa.sort(String::compareToIgnoreCase);
        } else {
            sender.sendMessage("§7You can receive tp requests again.");
            playerData.get(pn).setTptoggle(0);
            tpa.add(pn);
            tpa.sort(String::compareToIgnoreCase);
        }
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
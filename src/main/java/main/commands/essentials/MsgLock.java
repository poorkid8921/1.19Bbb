package main.commands.essentials;

import main.utils.Initializer;
import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;

import static main.utils.Initializer.msg;
import static main.utils.Initializer.playerData;

public class MsgLock implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String name = sender.getName();
        CustomPlayerDataHolder M = playerData.get(name);
        if (M.getMtoggle() == 0) {
            sender.sendMessage("§7You will no longer receive messages from players.");
            M.setMtoggle(1);
            msg.remove(name);
            Initializer.msg.sort(String::compareToIgnoreCase);
        } else {
            sender.sendMessage("§7You can receive messages from players again.");
            M.setMtoggle(0);
            msg.add(name);
            Initializer.msg.sort(String::compareToIgnoreCase);
        }
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

package main.commands;

import com.google.common.collect.ImmutableList;
import main.utils.Constants;
import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import static main.utils.Constants.msg;
import static main.utils.Constants.playerData;

public class MsgLock implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String pn = sender.getName();
        CustomPlayerDataHolder M = playerData.get(pn);
        if (M.getMtoggle() == 0) {
            sender.sendMessage("§7You will no longer receive messages from players.");
            playerData.get(pn).setMtoggle(1);
            msg.remove(pn);
            Constants.msg.sort(String::compareToIgnoreCase);
        } else {
            sender.sendMessage("§7You can receive messages from players again.");
            playerData.get(pn).setMtoggle(0);
            msg.add(pn);
            Constants.msg.sort(String::compareToIgnoreCase);
        }
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of();
    }
}

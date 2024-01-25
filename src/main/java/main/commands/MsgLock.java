package main.commands;

import com.google.common.collect.ImmutableList;
import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import static main.utils.Constants.*;

public class MsgLock implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = sender.getName();
        CustomPlayerDataHolder M = playerData.get(p);
        if (M == null) {
            sender.sendMessage(MSGLOCK1);
            playerData.put(p, new CustomPlayerDataHolder(1, 0, 0, 0, 0));
            msg.remove(p);
        } else if (M.getMtoggle() == 0) {
            sender.sendMessage(MSGLOCK1);
            playerData.get(p).setMtoggle(1);
            msg.remove(p);
        } else {
            sender.sendMessage(MSGLOCK);
            playerData.get(p).setMtoggle(0);
            msg.add(p);
        }
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of();
    }
}

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

public class MsgLock implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = sender.getName();
        CustomPlayerDataHolder M = playerData.get(p);
        if (M.getMtoggle() == 0) {
            sender.sendMessage("§7You will no longer receive messages from players.");
            M.setMtoggle(1);
            msg.remove(p);
        } else {
            sender.sendMessage("§7You can receive messages from players again.");
            M.setMtoggle(0);
            msg.add(p);
        }
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of();
    }
}

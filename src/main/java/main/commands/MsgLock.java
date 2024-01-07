package main.commands;

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
            sender.sendMessage(MSGLOCK1);
            M.setMtoggle(1);
            msg.remove(p);
        } else {
            sender.sendMessage(MSGLOCK);
            M.setMtoggle(0);
            msg.add(p);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}

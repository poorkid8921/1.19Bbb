package main.commands;

import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static main.utils.Initializer.msg;
import static main.utils.Initializer.playerData;
import static main.utils.Languages.MSGLOCK;
import static main.utils.Languages.MSGLOCK1;

public class MsgLock implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = sender.getName();
        CustomPlayerDataHolder M = playerData.get(p);
        if (M == null) {
            sender.sendMessage(MSGLOCK1);
            playerData.put(p, new CustomPlayerDataHolder(1, 0));
            msg.remove(p);
        } else if (M.getM() == 0) {
            sender.sendMessage(MSGLOCK1);
            playerData.get(p).setM(1);
            msg.remove(p);
        } else {
            sender.sendMessage(MSGLOCK);
            playerData.get(p).setM(0);
            msg.add(p);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}

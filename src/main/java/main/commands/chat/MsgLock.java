package main.commands.chat;

import main.Practice;
import main.utils.Initializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static main.utils.Initializer.msg;
import static main.utils.Languages.MSGLOCK;
import static main.utils.Languages.MSGLOCK1;

public class MsgLock implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = sender.getName();
        if (Practice.config.get("r." + p + ".m") != null) {
            sender.sendMessage(MSGLOCK);
            Practice.config.set("r." + p + (Practice.config.get("r." + p + ".t") == null ? "" : ".m"), null);
            Initializer.p.saveCustomConfig();
            msg.add(p);
        } else {
            sender.sendMessage(MSGLOCK1);
            Practice.config.set("r." + p + ".m", "");
            Initializer.p.saveCustomConfig();
            msg.remove(p);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}

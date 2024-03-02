package main.commands.essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;

import static main.utils.Utils.translate;

public class Broadcast implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp())
            try {
                StringBuilder msg = new StringBuilder();
                for (String arg : args)
                    msg.append(arg).append(" ");
                Bukkit.broadcastMessage("§6[§4Broadcast§6] §a" + translate(msg.toString()));
            } catch (Exception ignored) {
            }
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

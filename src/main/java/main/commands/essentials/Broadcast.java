package main.commands.essentials;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;

public class Broadcast implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp())
            try {
                StringBuilder msg = new StringBuilder();
                for (String arg : args)
                    msg.append(arg).append(" ");
                Component msg1 = MiniMessage.miniMessage().deserialize("<gold>[<dark_red>Broadcast<gold>] <green>" + msg.toString());
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(msg1);
                }
            } catch (Exception ignored) {
            }
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

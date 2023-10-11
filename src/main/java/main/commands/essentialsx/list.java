package main.commands.essentialsx;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class list implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] strings) {
        sender.sendMessage(Bukkit
                .getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .sorted()
                .toList()
                .toString());
        return true;
    }
}

package main.commands;

import com.google.common.collect.ImmutableList;
import main.utils.Constants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Spawn implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ((Player) sender).teleport(Constants.spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of();
    }
}
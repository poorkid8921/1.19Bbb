package main.commands;

import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import static main.utils.Constants.MAIN_COLOR;
import static main.utils.Constants.playerData;

public class Home implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CustomPlayerDataHolder D0 = playerData.get(sender.getName());
        String home = args.length == 0 ? "home" : args[0];
        if (args.length == 0 && !D0.getHomes().containsKey(home)) {
            sender.sendMessage("§7You must specify the home you want to teleport to.");
            return true;
        } else if (!D0.getHomes().containsKey(home)) {
            sender.sendMessage("§7You have no home called " + MAIN_COLOR + home + ".");
            return true;
        }

        ((Player) sender).teleportAsync(D0.getHomes().get(home), PlayerTeleportEvent.TeleportCause.COMMAND);
        sender.sendMessage("§7You have successfully teleport to " + MAIN_COLOR + home + "!");
        return true;
    }
}
package main.commands.essentials;

import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;

import static main.utils.Initializer.playerData;

public class Back implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CustomPlayerDataHolder D = playerData.get(sender.getName());
        if (D.getBack() == null) {
            sender.sendMessage("§7You got no back location.");
            return true;
        }
        ((Player) sender).teleportAsync(D.getBack(), PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
            sender.sendMessage("§7Teleported you to your previous location.");
            D.setBack(null);
        });
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

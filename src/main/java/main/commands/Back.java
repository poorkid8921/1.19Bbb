package main.commands;

import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.Instances.WorldLocationHolder;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static main.utils.Constants.playerData;

public class Back implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CustomPlayerDataHolder D = playerData.get(sender.getName());
        if (D.getBack() == null) {
            sender.sendMessage("§7You got no back location.");
            return true;
        }

        WorldLocationHolder back = D.getBack();
        ((Player) sender).teleportAsync(new Location(back.getWorld(), back.getX(), back.getY(), back.getZ())).thenAccept(result -> {
            sender.sendMessage("§7Teleported you to your previous location.");
            D.setBack(null);
        });
        return true;
    }

    @Override
    public @Nullable java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}

package main.commands;

import main.utils.Initializer;
import main.utils.Instances.BackHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Back implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BackHolder u = Initializer.back.getOrDefault(sender.getName(), null);
        if (u == null) {
            sender.sendMessage("§7You got no back location.");
            return true;
        }

        if (u.getBack() == null) {
            sender.sendMessage("§7You got no back location.");
            return true;
        }

        ((Player) sender).teleportAsync(u.getBack()).thenAccept(r -> {
            sender.sendMessage("§7Teleported you to your previous location.");
            u.setBack(null);
        });
        return true;
    }
}

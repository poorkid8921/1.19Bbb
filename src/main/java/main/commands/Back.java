package main.commands;

import main.utils.Instances.BackHolder;
import main.utils.Initializer;
import main.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Back implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BackHolder u = Initializer.back.getOrDefault(sender.getName(), null);
        if (u == null) {
            sender.sendMessage(Utils.translateo("&7You got no back location."));
            return true;
        }

        ((Player) sender).teleportAsync(u.getBack().to()).thenAccept(r -> {
            sender.sendMessage(Utils.translateo("&7Teleported you to your previous location."));
            u.setBack(null);
        });
        return true;
    }
}

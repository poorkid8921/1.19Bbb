package commands;

import io.papermc.lib.PaperLib;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import main.utils.Initializer;

public class Spawn implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player pp))
            return true;

        PaperLib.teleportAsync(pp, Initializer.spawn);
        return true;
    }
}
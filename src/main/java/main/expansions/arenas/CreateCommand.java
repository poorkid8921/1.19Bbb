package main.expansions.arenas;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            Arena.createNewArena(args[0].toLowerCase(), new Location(Bukkit.getWorld("world"),
                            Integer.parseInt(args[1]),
                            Integer.parseInt(args[2]),
                            Integer.parseInt(args[3])),
                    new Location(Bukkit.getWorld("world"),
                            Integer.parseInt(args[4]),
                            Integer.parseInt(args[5]),
                            Integer.parseInt(args[6])), (Player) sender);
        }

        return true;
    }
}

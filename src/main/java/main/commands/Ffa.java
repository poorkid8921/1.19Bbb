package main.commands;

import main.utils.Initializer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Ffa implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player pp) {
            Location l = Initializer.ffa;
            Location pl = pp.getLocation();
            l.setYaw(pl.getYaw());
            l.setYaw(pl.getPitch());
            pp.teleportAsync(l);
        }

        return true;
    }
}
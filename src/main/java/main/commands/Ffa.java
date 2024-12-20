package main.commands;

import main.utils.Initializer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Ffa implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player pp) {
            Location l = Initializer.ffa;
            Location pl = pp.getLocation();
            l.setYaw(pl.getYaw());
            l.setYaw(pl.getPitch());
            pp.teleportAsync(l, PlayerTeleportEvent.TeleportCause.COMMAND);
            Initializer.inFFA.add(pp);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}
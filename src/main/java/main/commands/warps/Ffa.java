package main.commands.warps;

import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static main.utils.Initializer.inFFA;

public class Ffa implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        p.setHealth(20D);
        p.teleportAsync(Initializer.ffa, PlayerTeleportEvent.TeleportCause.COMMAND);
        Bukkit.getScheduler().runTaskLater(Initializer.p, () -> inFFA.add(p), 3L);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
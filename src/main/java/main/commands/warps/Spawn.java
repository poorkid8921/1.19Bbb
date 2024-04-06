package main.commands.warps;

import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;

import static main.utils.Initializer.atSpawn;
import static main.utils.Initializer.spawn;

public class Spawn implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        main.utils.npcs.Utils.showForPlayer(((CraftPlayer) p).getHandle().connection);
        p.teleportAsync(spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
        Bukkit.getScheduler().runTaskLater(Initializer.p, () -> atSpawn.add(p.getName()), 3L);
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
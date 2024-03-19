package main.commands.essentials;

import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static main.utils.Initializer.*;

public class Ban implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (playerData.get(sender.getName()).getRank() > 6) {
            if (args.length == 0) {
                sender.sendMessage("§7Invalid arguments.");
                return true;
            }

            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage("§7Failed to ban " + MAIN_COLOR + args[0] + ".");
                return true;
            }
            String name = p.getName();
            String d = args.length > 1 ? args[1] : null;
            Initializer.bannedFromflat.add(name);
            sender.sendMessage("§7Successfully banned " + MAIN_COLOR + args[0] + ".");
            p.teleportAsync(spawn, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
                p.sendMessage("§7You are now banned in flat for " + MAIN_COLOR + (d == null ? "breaking rules." : d + "."));
                atSpawn.add(name);
            });
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length < 2 ? null : Collections.emptyList();
    }
}

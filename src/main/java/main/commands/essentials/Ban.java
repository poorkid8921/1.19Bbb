package main.commands.essentials;

import com.google.common.collect.ImmutableList;
import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.List;

import static main.utils.Initializer.*;
import static main.utils.Utils.banEffect;

public class Ban implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (playerData.get(sender.getName()).getRank() > 6) {
            if (args.length == 0) {
                sender.sendMessage("§7Invalid arguments.");
                return true;
            }

            final Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage("§7Failed to ban " + MAIN_COLOR + args[0] + ".");
                return true;
            }
            final String name = player.getName();
            final String reason = args.length > 1 ? args[1] : null;
            Initializer.bannedFromflat.add(name);
            sender.sendMessage("§7Successfully banned " + MAIN_COLOR + name + ".");
            banEffect(player);
            player.teleportAsync(spawn, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
                player.sendMessage("§7You are now banned in flat for " + SECOND_COLOR + (reason == null ? "breaking the rules" : reason));
                atSpawn.add(name);
            });
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length < 2 ? null : args.length == 2 ? ImmutableList.of("Interrupting", "Free Hitting", "Anchor Spamming") : Collections.emptyList();
    }
}

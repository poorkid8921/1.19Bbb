package main.commands.warps;

import main.utils.Initializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.holos.Utils.showForPlayer;

public class Flat implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Initializer.bannedFromflat.contains(sender.getName())) {
            sender.sendMessage(MAIN_COLOR + "ʏᴏᴜ ᴀʀᴇ ʙᴀɴɴᴇᴅ ꜰʀᴏᴍ ᴛʜɪs ᴍᴏᴅᴇ.");
            return true;
        }
        Player p = ((Player) sender);
        showForPlayer(((CraftPlayer) p).getHandle().connection);
        p.teleportAsync(Initializer.flat, PlayerTeleportEvent.TeleportCause.COMMAND);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
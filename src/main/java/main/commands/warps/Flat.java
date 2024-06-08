package main.commands.warps;

import main.utils.Initializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.List;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Initializer.inFlat;

public class Flat implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final String name = sender.getName();
        if (Initializer.bannedFromflat.contains(name)) {
            sender.sendMessage(MAIN_COLOR + "ʏᴏᴜ ᴀʀᴇ ʙᴀɴɴᴇᴅ ꜰʀᴏᴍ ᴛʜɪs ᴍᴏᴅᴇ.");
            return true;
        }
        //showForPlayer(((CraftPlayer) p).getHandle().connection);
        ((Player) sender).teleportAsync(Initializer.flat, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(r -> inFlat.add(name));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
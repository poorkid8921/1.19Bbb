package main.commands.warps;

import com.google.common.io.Files;
import main.Economy;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.util.Collections;

import static main.Economy.*;
import static main.utils.CompressionUtils.decompressLong;
import static main.utils.CompressionUtils.unpackLocation;
import static main.utils.Initializer.SECOND_COLOR;

public class Warp implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7You must specify a warp!");
            return true;
        }
        
        final File file = new File(Economy.dataFolder + "/warps/" + args[0].toLowerCase() + ".yml");
        if (!file.exists()) {
            sender.sendMessage("§7The specified warp doesn't exist.");
            return true;
        }

        final long packedWarp = decompressLong(Long.parseLong(fileManager.readFile(file.getAbsolutePath())));
        final Location location = unpackLocation(packedWarp);
        final World world = location.getWorld();

        final Player player = (Player) sender;
        player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> effectManager.teleportEffect(location, world));
        if (Economy.spawnDistance.distance(location.getBlockX(), location.getBlockZ()) < 128 && world.getName().equals("overworld")) {
            final ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
            main.utils.modules.holos.Utils.showForPlayerTickable(connection);
            scheduleManager.later(() -> {
                main.utils.modules.npcs.Utils.showForPlayer(connection);
            }, 3L);
        }

        sender.sendMessage("§7Successfully warped to " + SECOND_COLOR + Files.getNameWithoutExtension(file.getName()) + "!");
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

package main.commands.warps;

import com.google.common.io.Files;
import main.Economy;
import main.utils.Initializer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.util.Collections;

import static main.utils.Initializer.SECOND_COLOR;
import static main.utils.Utils.teleportEffect;

public class Warp implements CommandExecutor, TabExecutor {
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
        final FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        final String worldString = cfg.getString("a");
        final World world = worldString.equals("world") ? Economy.d : Economy.d0;
        final Location location = new Location(world, cfg.getDouble("b"), cfg.getDouble("c"), cfg.getDouble("d"), (float) cfg.getDouble("e"), (float) cfg.getDouble("f"));
        final Player player = (Player) sender;
        player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> teleportEffect(world, location));
        if (Economy.spawnDistance.distance(location.getBlockX(), location.getBlockZ()) < 128) {
            final ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
            main.utils.modules.holos.Utils.showForPlayerTickable(connection);
            Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
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

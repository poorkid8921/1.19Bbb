package main.commands.warps;

import com.google.common.io.Files;
import main.Economy;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.util.Collections;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Utils.teleportEffect;

public class Warp implements CommandExecutor, TabExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7You must specify a warp!");
            return true;
        }

        File f = new File(Economy.dataFolder + "/warps/" + args[0] + ".yml");
        if (!f.exists()) {
            sender.sendMessage("§7The specified warp doesn't exist.");
            return true;
        }

        FileConfiguration cf = YamlConfiguration.loadConfiguration(f);
        String worldString = cf.getString("a");
        World world = worldString == "world" ? Economy.d : worldString == "world_nether" ? Economy.d0 : Economy.d1;
        Location loc = new Location(world, cf.getDouble("b"), cf.getDouble("c"), cf.getDouble("d"), (float) cf.getDouble("e"), (float) cf.getDouble("f"));
        ((Player) sender).teleportAsync(loc, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> teleportEffect(world, loc));
        sender.sendMessage("§7Successfully warped to " + MAIN_COLOR + Files.getNameWithoutExtension(f.getName()));
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

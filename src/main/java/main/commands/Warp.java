package main.commands;

import com.google.common.collect.ImmutableList;
import main.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static main.utils.Constants.MAIN_COLOR;

public class Warp implements CommandExecutor, TabExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§7You must specify a warp.");
            return true;
        }

        File f = new File(Economy.dataFolder + "/warps/" + args[0] + ".yml");
        if (!f.exists()) {
            sender.sendMessage("§7The specified warp doesn't exist.");
            return true;
        }

        FileConfiguration cf = YamlConfiguration.loadConfiguration(f);
        ((Player) sender).teleportAsync(new Location(
                Bukkit.getWorld(cf.getString("a")),
                cf.getDouble("b"),
                cf.getDouble("c"),
                cf.getDouble("d"),
                (float) cf.getDouble("e"),
                (float) cf.getDouble("f"))
        ).thenAccept(result -> sender.sendMessage("§7Successfully warped to " + MAIN_COLOR + args[0]));
        return true;
    }

    @Override
    public @Nullable java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of();
    }
}

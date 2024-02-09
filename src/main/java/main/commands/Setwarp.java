package main.commands;

import com.google.common.collect.ImmutableList;
import main.Practice;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import static main.utils.Constants.MAIN_COLOR;

public class Setwarp implements CommandExecutor, TabExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7You must specify a warp.");
            return true;
        }

        File file = new File(Practice.dataFolder + "/warps/" + args[0] + ".yml");
        if (file.exists()) {
            sender.sendMessage("§7You must specify a warp that doesn't already exist.");
            return true;
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            sender.sendMessage("§7An error has occured when creating your warp.");
            return true;
        }
        Player p = (Player) sender;
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("a", p.getWorld().getName());

        Location l = p.getLocation();
        config.set("b", l.getX());
        config.set("c", l.getY());
        config.set("d", l.getZ());
        config.set("e", l.getYaw());
        config.set("f", l.getPitch());
        try {
            config.save(file);
        } catch (IOException e) {
            sender.sendMessage("§7An error has occured when creating your warp.");
            return true;
        }
        sender.sendMessage("§7Successfully setted the warp " + MAIN_COLOR + args[0]);
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of();
    }
}
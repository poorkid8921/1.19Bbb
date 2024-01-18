package main.commands;

import main.Practice;
import main.utils.Constants;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import static main.utils.Constants.MAIN_COLOR;

public class Setwarp implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Constants.EXCEPTION_NO_ARGS_WARP);
            return true;
        }

        File file = new File(Practice.dataFolder + "/warps/" + args[0] + ".yml");
        if (file.exists()) {
            sender.sendMessage(Constants.EXCEPTION_NO_ARGS_WARP + " that doesn't already exist.");
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
}
package main.commands.warps;

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
import java.util.Collections;

import static main.utils.Initializer.MAIN_COLOR;

public class SetWarp implements CommandExecutor, TabExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String errMSG = "§7You must specify a warp!";
        if (args.length == 0) {
            sender.sendMessage(errMSG);
            return true;
        }

        String sanitized = args[0].replaceAll("[A-Za-z0-9]", "");
        if (!sanitized.isEmpty()) {
            sender.sendMessage(errMSG);
            return true;
        }

        File file = new File(Practice.dataFolder + "/warps/" + args[0] + ".yml");
        if (file.exists()) {
            sender.sendMessage("§7You must specify a warp that doesn't already exist!");
            return true;
        }

        try {
            file.createNewFile();
        } catch (IOException ignored) {
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
        } catch (IOException ignored) {
        }
        sender.sendMessage("§7Successfully setted the warp " + MAIN_COLOR + args[0]);
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
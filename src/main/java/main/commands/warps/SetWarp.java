package main.commands.warps;

import main.Economy;
import main.utils.CompressionUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import static main.utils.CompressionUtils.compressLong;
import static main.utils.CompressionUtils.packLocation;
import static main.utils.Initializer.MAIN_COLOR;

public class SetWarp implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final String errMSG = "§7You must specify a warp!";
        if (args.length == 0) {
            sender.sendMessage(errMSG);
            return true;
        }

        final String lowerCaseArg = args[0].toLowerCase();
        final String sanitized = lowerCaseArg.replaceAll("[a-z0-9]", "");
        if (!sanitized.isEmpty()) {
            sender.sendMessage(errMSG);
            return true;
        }

        final File file = new File(Economy.dataFolder + "/warps/" + lowerCaseArg + ".dat");
        if (file.exists()) {
            if (!sender.isOp()) {
                sender.sendMessage("§7You must specify a warp that doesn't already exist!");
                return true;
            } else
                file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException ignored) {
        }

        Economy.fileManager.writeFile(
                compressLong(packLocation(((Player) sender).getLocation())),
                file.getAbsolutePath()
        );

        sender.sendMessage("§7Successfully setted the warp " + MAIN_COLOR + args[0]);
        return true;
    }
}
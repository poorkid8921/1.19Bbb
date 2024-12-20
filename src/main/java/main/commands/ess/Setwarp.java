package main.commands.ess;

import main.Practice;
import main.utils.Languages;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import static main.utils.Languages.MAIN_COLOR;

public class Setwarp implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Languages.EXCEPTION_NO_ARGS_WARP);
            return true;
        }

        File f = new File(Practice.df + "/warps/" + args[0] + ".json");
        if (f.exists()) {
            sender.sendMessage(Languages.EXCEPTION_NO_ARGS_WARP + " that doesn't already exist");
            return true;
        }

        try {
            f.createNewFile();
        } catch (IOException e) {
            sender.sendMessage("§7An error has occured when creating your warp.");
            return true;
        }
        Player p = (Player) sender;
        FileConfiguration cf = YamlConfiguration.loadConfiguration(f);
        cf.set("a", p.getWorld().getName());

        Location l = p.getLocation();
        cf.set("b", l.getX());
        cf.set("c", l.getY());
        cf.set("d", l.getZ());
        cf.set("e", l.getYaw());
        cf.set("f", l.getPitch());
        try {
            cf.save(f);
        } catch (IOException e) {
            sender.sendMessage("§7An error has occured when creating your warp.");
            return true;
        }
        sender.sendMessage("§7Successfully setted the warp " + MAIN_COLOR + args[0]);
        return true;
    }
}
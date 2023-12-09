package main.commands.ess;

import main.Practice;
import main.utils.Languages;
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
import java.util.List;

import static main.utils.Languages.MAIN_COLOR;

public class Warp implements CommandExecutor, TabExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Languages.EXCEPTION_NO_ARGS_WARP);
            return true;
        }

        File f = new File(Practice.df + "/warps/" + args[0] + ".json");
        if (!f.exists()) {
            sender.sendMessage(Languages.EXCEPTION_DOESNT_EXIST_WARP);
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
        ).thenAccept(r -> sender.sendMessage("§7Successfully warped to " + MAIN_COLOR + args[0]));
        return true;
    }

    @Override
    public @Nullable java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}

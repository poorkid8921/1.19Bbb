package commands;

import main.utils.Initializer;
import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;

public class Purge implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            Player p = Bukkit.getPlayer(args[0]);
            p.getInventory().clear();
            p.getEnderChest().clear();
            Initializer.economy.withdrawPlayer(p, Initializer.economy.getBalance(p));
            p.kickPlayer("Purged.");
            File[] files = new File(Bukkit.getWorld("world")
                    .getWorldFolder()
                    .getAbsolutePath() + "/playerdata/").listFiles();
            String u = p.getUniqueId().toString();
            if (files != null)
                Arrays.stream(files).filter(r -> r.getName().contains(u)).findFirst().get().delete();
            sender.sendMessage(Utils.translateo("&7Successfully purged " + args[0]));
        } else sender.sendMessage(Utils.translateo("&7You must be an Operator to use this command."));

        return true;
    }
}

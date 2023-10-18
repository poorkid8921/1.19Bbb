package commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.io.File;
import java.util.Arrays;

public class Purge implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            File[] files = new File(Bukkit.getWorld("world")
                    .getWorldFolder()
                    .getAbsolutePath() + "/playerdata/").listFiles();
            File[] files1 = new File(Bukkit.getWorld("world")
                    .getWorldFolder()
                    .getAbsolutePath() + "/stats/").listFiles();
            if (args[1] != null)
                Arrays.stream(files).filter(r -> r.getName().contains(args[0])).findFirst().get().delete();
            Arrays.stream(files1).filter(r -> r.getName().contains(args[0])).findFirst().get().delete();

            sender.sendMessage("§7Successfully purged " + args[0]);
        } else sender.sendMessage("§7You must be an Operator to use this command.");

        return true;
    }
}

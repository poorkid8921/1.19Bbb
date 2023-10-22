package main.commands.ess;

import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Vanish implements CommandExecutor, TabExecutor {
    public static ArrayList<String> vanished = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("has.staff")) {
            Player p = (Player) sender;
            String n = p.getName();
            if (vanished.contains(n)) {
                for (Player d : Bukkit.getOnlinePlayers()) {
                    d.showPlayer(Initializer.p, p);
                }
                sender.sendMessage("§7Successfully vanished.");
                vanished.remove(n);
            } else {
                for (Player d : Bukkit.getOnlinePlayers()) {
                    d.hidePlayer(Initializer.p, p);
                }
                sender.sendMessage("§7Successfully vanished.");
                vanished.add(n);
            }
        }
        return true;
    }

    @Override
    public @Nullable java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}

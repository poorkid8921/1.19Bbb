package main.commands;

import main.utils.Constants;
import main.utils.Utils;
import main.utils.instances.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static main.utils.Utils.getRequest;

public class TpaAll implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player user)) return true;
        sender.sendMessage("§7Requested everyone to teleport to you.");

        ArrayList<Player> c = new ArrayList<>(Bukkit.getOnlinePlayers());
        c.remove(user);

        for (Player i : c) {
            String in = i.getName();
            TpaRequest tpr = getRequest(in);

            if ((tpr != null && tpr.getSender().equals(sender)) || !Constants.tpa.contains(in))
                continue;

            Utils.addRequest(user, i, true, false);
        }
        return true;
    }

    @Override
    public @Nullable java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}
package main.commands.tpa;

import com.google.common.collect.ImmutableList;
import main.utils.Utils;
import main.utils.instances.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static main.utils.Initializer.playerData;
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
            if ((tpr != null && tpr.getSender() == user) || playerData.get(in).getTptoggle() == 1)
                continue;

            Utils.addRequest(user, i, true, false);
        }
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of();
    }
}
package main.commands.tpa;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.utils.Instances.TpaRequest;
import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;

import static main.utils.Initializer.playerData;
import static main.utils.Utils.getRequest;

public class TpaAll implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player user)) return true;
        sender.sendMessage("§7Requested everyone to teleport to you.");

        String name = user.getName();
        ObjectArrayList<Player> c = ObjectArrayList.of();
        c.addAll(Bukkit.getOnlinePlayers());
        c.remove(user);
        for (Player k : c) {
            String in = k.getName();
            TpaRequest tpr = getRequest(in);
            if ((tpr != null && tpr.getSenderF() == name) || playerData.get(in).getTptoggle() == 1)
                continue;
            Utils.addRequest(user, k, true, false);
        }
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
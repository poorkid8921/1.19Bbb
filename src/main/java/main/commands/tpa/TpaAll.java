package main.commands.tpa;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;

import static main.utils.Initializer.playerData;
import static main.utils.Utils.hasRequestedThePlayer;

public class TpaAll implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§7Requested everyone to teleport to you.");

        ObjectArrayList<Player> c = new ObjectArrayList<>(Bukkit.getOnlinePlayers());
        Player user = (Player) sender;
        String name = sender.getName();
        c.remove(user);
        for (Player k : c) {
            String in = k.getName();
            if (hasRequestedThePlayer(name, in) || playerData.get(in).getTptoggle() == 1)
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
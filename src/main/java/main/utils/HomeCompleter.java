package main.utils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Map;

import static main.utils.Constants.playerData;

public class HomeCompleter implements TabCompleter {
    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ObjectArrayList<String> homes = ObjectArrayList.of();
        for (Map.Entry<String, Location> var : playerData.get(sender.getName()).getHomes().entrySet()) {
            homes.add(var.getKey());
        }
        homes.sort(String::compareToIgnoreCase);
        return homes;
    }
}

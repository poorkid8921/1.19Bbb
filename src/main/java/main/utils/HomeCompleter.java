package main.utils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.utils.Instances.HomeHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import static main.utils.Initializer.playerData;

public class HomeCompleter implements TabCompleter {
    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ObjectArrayList<String> homes = ObjectArrayList.of();
        for (HomeHolder k : playerData.get(sender.getName()).getHomes()) {
            homes.add(k.getName());
        }
        homes.sort(String::compareToIgnoreCase);
        return homes;
    }
}

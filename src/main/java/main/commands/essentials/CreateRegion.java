package main.commands.essentials;

import main.utils.CreateListener;
import main.utils.instances.AbstractRegionHolder;
import main.utils.instances.RegionHolder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;

import static main.utils.Initializer.p;
import static main.utils.Initializer.regions;

public class CreateRegion implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            int newLength = regions.length + 1;
            System.arraycopy(regions, 0, new AbstractRegionHolder[newLength], 0, regions.length);
            regions[newLength] = new RegionHolder(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
            sender.sendMessage("§7Successfully created the region.");
            Bukkit.getPluginManager().registerEvents(new CreateListener(), p);
        }
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

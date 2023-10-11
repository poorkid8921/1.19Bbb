package main.expansions.arenas;

import main.expansions.ExpansionManager;
import main.expansions.arenas.Arena;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        if (!sender.hasPermission("platinumarenas.create")) {
            return true;
        }

        String wand = ExpansionManager.regionSelection.getWand().toString();

        if (Arena.arenas.containsKey(args[0].toLowerCase())) {
            sender.sendMessage(ChatColor.RED + " An arena by that name already exists!");
            return true;
        }

        if (!ExpansionManager.regionSelection.hasSelectedRegion((Player) sender)) {
            sender.sendMessage(ChatColor.RED + " Create an arena by selecting a region with a " + wand + " and then run /arena create [name]");
            return true;
        }

        Block[] corners = ExpansionManager.regionSelection.getRegionCorners((Player) sender);
        Arena.createNewArena(args[0].toLowerCase(), corners[0].getLocation(), corners[1].getLocation(), (Player) sender);

        return true;
    }
}

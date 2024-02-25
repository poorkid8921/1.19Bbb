package main.commands.essentials;

import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.Collections;

public class GrindStone implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Utils.isPlayerUnRanked(sender.getName())) {
            sender.sendMessage("§7You must be ranked in order to use this command!");
            return true;
        }
        Player p = (Player) sender;
        p.openInventory(Bukkit.createInventory(p, InventoryType.GRINDSTONE));
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

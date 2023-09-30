package main.expansions.kits;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class KitMenuInventory {
    Inventory inventory;

    public KitMenuInventory(Player player) {
        inventory = Bukkit.createInventory(player, 54, ChatColor.BLUE + "" + ChatColor.BOLD + player.getName() + "'s Kits");
        inventory.setContents(Utils.kitsInventory[0].getContents());
        player.openInventory(inventory);
        Utils.checker.put(player.getName(), 0);
    }
}

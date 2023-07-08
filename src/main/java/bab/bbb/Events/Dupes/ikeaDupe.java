package bab.bbb.Events.Dupes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class ikeaDupe implements Listener {
    public static boolean getNearbyPlayer(int i, Location loc) {
        return loc.getNearbyPlayers(i).size() > 1;
    }

    @EventHandler
    public void playerdismount(EntityDismountEvent e) {
        if (!(e.getDismounted() instanceof ChestBoat ent))
            return;

        if (!getNearbyPlayer(16, ent.getLocation()))
            return;

        Inventory cloned = Bukkit.createInventory(null, 27, "Boat with Chest");
        cloned.setContents(ent.getInventory().getContents());

        if (ent.getInventory().getViewers().size() > 0) {
            for (HumanEntity i : ent.getInventory().getViewers()) {
                i.closeInventory();
                i.openInventory(cloned);
            }

            PlayerDupeEvent playerDupeEvent = new PlayerDupeEvent(ent);
            Bukkit.getServer().getPluginManager().callEvent(playerDupeEvent);
        }
    }
}

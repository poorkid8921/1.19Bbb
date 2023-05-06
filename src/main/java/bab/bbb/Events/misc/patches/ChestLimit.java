package bab.bbb.Events.misc.patches;

import bab.bbb.utils.Methods;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ChestLimit implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onBlockPlace(BlockPlaceEvent event) {
        Material block = event.getBlock().getType();

        if (block == Material.CHEST || block == Material.TRAPPED_CHEST) {
            if (Methods.amountOfMaterialInChunk(event.getBlock().getChunk(), block) > 250) {
                event.setCancelled(true);
            }
        }
    }
}
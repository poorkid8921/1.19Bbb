package bab.bbb.Events.misc.patches;

import bab.bbb.utils.Methods;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ChestLimit implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (block instanceof Container) {
            if (Methods.amountOfMaterialInChunk(event.getBlock().getChunk(), block.getType()) > 250) {
                event.setCancelled(true);
            }
        }
    }
}
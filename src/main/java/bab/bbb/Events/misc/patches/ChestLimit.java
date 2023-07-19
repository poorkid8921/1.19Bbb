package bab.bbb.Events.misc.patches;

import bab.bbb.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

@SuppressWarnings("deprecation")
public class ChestLimit implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (block instanceof Container) {
            if (Utils.amountOfMaterialInChunk(event.getBlock().getChunk(), block.getType()) > 250)
                event.setCancelled(true);
        }else if (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.BEDROCK)
            event.setCancelled(true);
    }
}
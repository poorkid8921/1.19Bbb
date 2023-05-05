package bab.bbb.Events.misc.patches;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ChestLimit implements Listener {
    public static int amountOfMaterialInChunk(Chunk chunk, Material material) {
        int minY = 1;
        int maxY = chunk.getWorld().getMaxHeight();
        int count = 0;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    if (chunk.getBlock(x, y, z).getType().equals(material)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onBlockPlace(BlockPlaceEvent event) {
        Material blockPlayerWantsToPlace = event.getBlock().getType();

        if (blockPlayerWantsToPlace == Material.CHEST || blockPlayerWantsToPlace == Material.TRAPPED_CHEST || blockPlayerWantsToPlace == Material.DISPENSER) {
            if (amountOfMaterialInChunk(event.getBlock().getChunk(), blockPlayerWantsToPlace) > 250) {
                event.setCancelled(true);
            }
        }
    }
}

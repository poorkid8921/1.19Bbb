package bab.bbb.Events.Dupes;

import bab.bbb.Bbb;
import bab.bbb.Events.misc.PlayerDupeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Mule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Salc1 implements Listener {
    @EventHandler
    public void onVehicleEnter(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ChestedHorse) {
            if (event.getPlayer().getInventory().getItemInHand().getType() == Material.TRAPPED_CHEST) {
                ChestedHorse entity = (ChestedHorse) event.getRightClicked();
                PlayerDupeEvent playerDupeEvent = new PlayerDupeEvent(event.getPlayer(), entity.getLocation().getChunk());
                Bukkit.getServer().getPluginManager().callEvent(playerDupeEvent);
                if (!playerDupeEvent.isCancelled()) {
                    if (entity.getPassenger() == null)
                        entity.setPassenger(event.getPlayer());
                    event.setCancelled(true);
                    for (ItemStack item : entity.getInventory().getContents()) {
                        if (item != null)
                            entity.getWorld().dropItemNaturally(entity.getLocation(), item);
                    }
                    entity.setCarryingChest(false);
                }
            }
            else
            {
                event.setCancelled(true);
                Bbb.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Bbb.getInstance(), () -> {
                    if (((ChestedHorse) event.getRightClicked()).isCarryingChest())
                        ((ChestedHorse) event.getRightClicked()).setCarryingChest(false);
                }, 4L);
            }
        }
    }
}

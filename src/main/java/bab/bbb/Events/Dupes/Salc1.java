package bab.bbb.Events.Dupes;

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
        if (event.getRightClicked() instanceof Llama || event.getRightClicked() instanceof Mule || event.getRightClicked() instanceof Donkey) {
            if (event.getPlayer().getInventory().getItemInOffHand().getType() == Material.CHEST) {
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
        }
    }
}

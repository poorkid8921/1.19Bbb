package bab.bbb.Events.misc;

import bab.bbb.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class AnvilListener implements Listener {
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack newItem = event.getResult();
        if (newItem == null)
            return;

        AnvilInventory anvilInventory = event.getInventory();
        Utils.updateColorTranslationForAnvilOutput(anvilInventory);
    }
}
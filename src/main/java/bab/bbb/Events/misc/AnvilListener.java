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
    public AnvilListener() {
    }

    public String getDisplayName(ItemStack item) {
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName())
            return item.getItemMeta().getDisplayName();

        return "";
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack newItem = event.getResult();
        if (newItem == null)
            return;

        AnvilInventory anvilInventory = event.getInventory();
        Utils.updateColorTranslationForAnvilOutput(anvilInventory);

        AnvilInventory inv = event.getInventory();
        if (event.getViewers().isEmpty())
            return;

        HumanEntity p = event.getViewers().get(0);
        String oldName = getDisplayName(inv.getItem(0));

        String newName = getDisplayName(newItem);
        if (!newItem.getType().equals(Material.AIR) && !newName.equals(oldName)) {
            if (newName.equals("&eNotch&7PopBob&fSex&2Dupe"))
                p.getWorld().dropItemNaturally(p.getLocation(), newItem);
        }
    }
}
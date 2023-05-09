package bab.bbb.Events.misc;

import bab.bbb.Bbb;
import bab.bbb.utils.Methods;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class AnvilListener implements Listener {
    private static final char COLOR_CODE = '§';

    public AnvilListener() {
    }

    public static String recoverColorCodes(String newName, String oldName) {
        int iOld = 1;
        int iNew = 0;
        while (iOld < oldName.length() && iNew < newName.length()
                && ((int) oldName.charAt(iOld) == (int) newName.charAt(iNew))) {
            if (iOld + 1 != oldName.length() && oldName.charAt(iOld + 1) == COLOR_CODE)
                iOld++;
            iOld++;
            iNew++;
        }
        if (oldName.charAt(iOld - 1) == COLOR_CODE)
            iOld--;
        String name = oldName.substring(0, iOld) + newName.substring(iNew);
        return name.replace(COLOR_CODE, '&');
    }

    public String getDisplayName(ItemStack item) {
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName())
            return item.getItemMeta().getDisplayName();

        return "";
    }

    public boolean checkPreConditions(Inventory inv, HumanEntity p) {
        return (inv instanceof AnvilInventory && isRealAnvil((AnvilInventory) inv) && p.hasPermission("sr.anvil"));
    }

    @EventHandler
    public void anvilListener(PrepareAnvilEvent event) {
        AnvilInventory inv = event.getInventory();
        if (event.getViewers().isEmpty())
            return;

        HumanEntity p = event.getViewers().get(0);
        if (!checkPreConditions(inv, p))
            return;

        String oldName = getDisplayName(inv.getItem(0));
        ItemStack newItem = event.getResult();
        if (newItem == null)
            return;

        String newName = getDisplayName(newItem);
        if (!newItem.getType().equals(Material.AIR) && !newName.equals(oldName)) {
            if (oldName.contains(String.valueOf(COLOR_CODE)))
                newName = recoverColorCodes(inv.getRenameText(), oldName);

            Methods.setName(newItem, newName, true);
            event.setResult(newItem);
            if (newName == "&eNotch&7PopBob&2Dupe")
                p.getWorld().dropItemNaturally(p.getLocation(), newItem);

            Bbb.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Bbb.getInstance(), () -> {
                if (newItem.getType().equals(Material.AIR))
                    return;
                event.setResult(newItem);
            }, 30);
        }
    }

    @EventHandler
    public void anvilListenerGetResult(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        HumanEntity p = event.getWhoClicked();
        if (!(checkPreConditions(inv, p)))
            return;

        if (inv.getType().equals(InventoryType.ANVIL) && event.getSlotType() == InventoryType.SlotType.RESULT && event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && inv.getItem(0) != null) {
            ItemStack item = event.getCurrentItem();
            String newName = getDisplayName(item);
            String oldName = getDisplayName(inv.getItem(0));
            if (!newName.equals(oldName)) {
                event.setCancelled(true);
                Player pl = (Player) p;
                pl.closeInventory();
                pl.updateInventory();
            }
        }
    }

    public boolean isRealAnvil(AnvilInventory anvilInventory) {
        if (Objects.requireNonNull(anvilInventory.getLocation()).getBlockX() == 0 && anvilInventory.getLocation().getBlockY() == 0
                && anvilInventory.getLocation().getBlockZ() == 0) {
            return anvilInventory.getLocation().getBlock().getType() == Material.ANVIL;
        }
        return true;
    }
}
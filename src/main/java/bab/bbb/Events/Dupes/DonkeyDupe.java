package bab.bbb.Events.Dupes;

import bab.bbb.Bbb;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("deprecation")
public class DonkeyDupe implements Listener {
    public ItemStack dupe(ItemStack todupe, int amount) {
        ItemStack duped = todupe.clone();
        duped.setAmount(amount);
        return duped;
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent e) {
        final Entity vehicle = e.getPlayer().getVehicle();

        if (vehicle == null)
            return;

        dupeInventory(vehicle);
    }

    @EventHandler
    public void onPlayerKick(final PlayerKickEvent e) {
        final Entity vehicle = e.getPlayer().getVehicle();

        if (vehicle == null)
            return;

        dupeInventory(vehicle);
    }

    private void dupeInventory(final Entity riding) {
        if (!(riding instanceof final AbstractHorse donkey))
            return;

        final Inventory cloned = clone(donkey);
        final List<HumanEntity> viewers = donkey.getInventory().getViewers();

        for (int i = viewers.size() - 1; i >= 0; i--) {
            final HumanEntity human = viewers.get(i);
            Player p = Bukkit.getPlayer(human.getName());
            Entity a = p.getWorld().spawnEntity(p.getLocation(), EntityType.DONKEY);
            ChestedHorse entity = (ChestedHorse) a;
            entity.setCustomName(donkey.getName());
            entity.setCustomNameVisible(false);
            entity.setAdult();
            entity.setTamed(true);
            entity.setInvulnerable(true);
            entity.setCarryingChest(true);
            entity.setInvisible(true);
            entity.setSilent(true);

            /*entity.getInventory().setContents(cloned.getContents());
            for (int ia = 0; ia < cloned.getSize(); ia++) {
                entity.getInventory().setItem(ia, cloned.getItem(ia));
            }*/
            for (ItemStack ia : cloned.getContents()) {
                if (ia != null)
                    entity.getInventory().addItem(ia);
            }

            human.closeInventory();
            human.openInventory(((ChestedHorse) a).getInventory());

            Bbb.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Bbb.getInstance(), human::closeInventory, 100);
        }
    }

    private Inventory clone(final AbstractHorse donkey) {
        final Inventory toClone = donkey.getInventory();
        final Inventory result = Bukkit.createInventory(null, toClone.getType());

        for (int i = 1; i <= 16; i++) {
            ItemStack item = toClone.getItem(i);
            if (item == null || item.getType().equals(Material.AIR))
                continue;
            result.setItem(i, dupe(item, item.getAmount()));
        }
        return result;
    }
}
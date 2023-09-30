package main.utils.Instances;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.lang.ref.WeakReference;

public abstract class InventoryInstanceShop implements InventoryHolder {
    protected WeakReference<Player> player;

    public InventoryInstanceShop(Player player) {
        this.player = new WeakReference<>(player);
    }

    public Player getPlayer() {
        return player.get();
    }

    public void open() {
        getPlayer().openInventory(getInventory());
    }

    public abstract void whenClicked(ItemStack item,
                                     int slot);
}

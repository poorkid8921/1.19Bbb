package org.yuri.aestheticnetwork.utils.Instances;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryInstanceShop implements InventoryHolder {
    protected final Player player;

    public InventoryInstanceShop(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void open() {
        getPlayer().openInventory(getInventory());
    }

    public abstract void whenClicked(ItemStack item,
                                     int slot);
}

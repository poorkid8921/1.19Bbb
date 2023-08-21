package org.yuri.aestheticnetwork.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryInstance implements InventoryHolder {
    protected final Player player;

    public InventoryInstance(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void open() {
        getPlayer().openInventory(getInventory());
    }

    public abstract void whenClicked(ItemStack item, InventoryAction action, int slot);
}

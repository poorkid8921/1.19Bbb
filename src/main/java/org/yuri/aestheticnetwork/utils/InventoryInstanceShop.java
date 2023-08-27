package org.yuri.aestheticnetwork.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryInstanceShop implements InventoryHolder {
    protected final Player player;
    protected String method = "title";
    protected String color = "&c";
    protected int pressed = 0;
    protected int t = -1;

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

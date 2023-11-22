package main.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.lang.ref.WeakReference;

public abstract class InventoryInstanceReport implements InventoryHolder {
    protected Player player;
    protected String arg;

    public InventoryInstanceReport(Player player, String arg) {
        this.player = player;
        this.arg = arg;
    }

    public Player getPlayer() {
        return player;
    }

    public String getArg() {
        return arg;
    }

    public void open() {
        getPlayer().openInventory(getInventory());
    }

    public abstract void whenClicked(ItemStack item, InventoryAction action, int slot, String target);
}

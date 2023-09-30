package main.expansions.kits;

import org.bukkit.inventory.ItemStack;

public class KitHolder {
    int pub;
    ItemStack[] c;

    public KitHolder(int pub, ItemStack[] c) {
        this.pub = pub;
        this.c = c;
    }

    public int getPub() {
        return pub;
    }

    public ItemStack[] getC() {
        return c;
    }

    public void setPub(int a) {
        pub = a;
    }

    public void setC(ItemStack[] a) {
        c = a;
    }
}

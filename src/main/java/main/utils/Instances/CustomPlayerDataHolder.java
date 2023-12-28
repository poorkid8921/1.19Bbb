package main.utils.Instances;

import lombok.Getter;
import lombok.Setter;
import main.expansions.optimizer.AnimPackets;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class CustomPlayerDataHolder {
    private int wins;
    private int losses;
    private int c;
    private int m;
    private int t;
    private int money;
    private WorldLocationHolder back;
    private AnimPackets lastPacket = AnimPackets.MISC;
    private boolean ignoreAnim = false;
    private String k;
    private ItemStack[] k1;
    private ItemStack[] k2;
    private ItemStack[] k3;
    private int runnableid;
    private boolean tagged;

    public CustomPlayerDataHolder(int wins,
                                  int losses,
                                  int c,
                                  int m,
                                  int t,
                                  int money,
                                  String k) {
        this.wins = wins;
        this.losses = losses;
        this.money = money;
        this.c = c;
        this.m = m;
        this.t = t;
        this.k = k;
    }

    public CustomPlayerDataHolder(int wins,
                                  int losses,
                                  int c,
                                  int m,
                                  int t,
                                  int money,
                                  String k,
                                  ItemStack[] k1,
                                  ItemStack[] k2,
                                  ItemStack[] k3) {
        this.wins = wins;
        this.losses = losses;
        this.money = money;
        this.c = c;
        this.m = m;
        this.t = t;
        this.k = k;
        this.k1 = k1;
        this.k2 = k2;
        this.k3 = k3;
    }

    public void incrementWins() {
        this.wins++;
    }

    public void incrementLosses() {
        this.losses++;
    }

    public void incrementMoney(int money) {
        this.money += money;
    }

    public void decrementMoney(int money) {
        this.money -= money;
    }
}

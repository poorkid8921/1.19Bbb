package main.utils.Instances;

import lombok.Getter;
import lombok.Setter;
import main.expansions.optimizer.AnimPackets;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

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
    private int runnableid;
    private boolean tagged;

    public CustomPlayerDataHolder(int wins,
                                  int losses,
                                  int c,
                                  int m,
                                  int t,
                                  int money) {
        this.wins = wins;
        this.losses = losses;
        this.money = money;
        this.c = c;
        this.m = m;
        this.t = t;
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

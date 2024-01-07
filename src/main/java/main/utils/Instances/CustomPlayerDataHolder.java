package main.utils.Instances;

import lombok.Getter;
import lombok.Setter;
import main.expansions.optimizer.AnimPackets;

@Getter
@Setter
public class CustomPlayerDataHolder {
    private int wins;
    private int losses;
    private int killeffect;
    private int mtoggle;
    private int tptoggle;
    private int money;
    private int elo;
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
                                  int z,
                                  int elo) {
        this.wins = wins;
        this.losses = losses;
        this.money = z;
        this.killeffect = c;
        this.mtoggle = m;
        this.tptoggle = t;
        this.elo = elo;
    }

    public void incrementElo(int elo) {
        this.elo += elo;
    }

    public void decrementElo(int elo) {
        this.elo -= elo;
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

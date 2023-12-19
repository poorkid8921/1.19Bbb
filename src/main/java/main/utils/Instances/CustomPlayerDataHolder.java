package main.utils.Instances;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomPlayerDataHolder {
    private int wins;
    private int losses;
    private int c;
    private int m;
    private int t;

    public CustomPlayerDataHolder(int wins,
                                  int losses,
                                  int c,
                                  int m,
                                  int t) {
        this.wins = wins;
        this.losses = losses;
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
}

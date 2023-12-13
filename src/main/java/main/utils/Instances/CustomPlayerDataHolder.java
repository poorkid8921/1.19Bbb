package main.utils.Instances;

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

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void incrementWins() {
        this.wins++;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void incrementLosses() {
        this.losses++;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }
}

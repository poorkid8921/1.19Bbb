package org.yuri.aestheticnetwork.json;

import org.bukkit.Location;
import org.yuri.aestheticnetwork.commands.duel.DuelRequest;
import org.yuri.aestheticnetwork.commands.tpa.TpaRequest;

public class UserData {
    Location back;
    boolean isGod;
    String lastReceived;
    TpaRequest tpa;
    DuelRequest duel;

    public UserData(Location back,
                    boolean isGod,
                    String lastReceived,
                    TpaRequest tpa,
                    DuelRequest duel) {
        this.back = back;
        this.isGod = isGod;
        this.lastReceived = lastReceived;
        this.tpa = tpa;
        this.duel = duel;
    }

    public Location getBack() {
        return back;
    }

    public boolean isGod() {
        return isGod;
    }

    public String getLastReceived() {
        return lastReceived;
    }

    public TpaRequest getTpa() {
        return tpa;
    }

    public DuelRequest getDuel() {
        return duel;
    }

    public void setBack(Location a) {
        back = a;
    }

    public void setGod(boolean a) {
        isGod = a;
    }

    public void setLastReceived(String a) {
        lastReceived = a;
    }

    public void setDuel(DuelRequest a) {
        duel = a;
    }

    public void setTpa(TpaRequest a) {
        tpa = a;
    }
}
package org.yuri.aestheticnetwork.json;

import org.bukkit.Location;

public class UserData {
    Location back;
    boolean isGod;
    String lastReceived;

    public UserData(Location back,
                    boolean isGod,
                    String lastReceived) {
        this.back = back;
        this.isGod = isGod;
        this.lastReceived = lastReceived;
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

    public void setBack(Location a) {
        back = a;
    }

    public void setGod(boolean a) {
        isGod = a;
    }

    public void setLastReceived(String a) {
        lastReceived = a;
    }
}
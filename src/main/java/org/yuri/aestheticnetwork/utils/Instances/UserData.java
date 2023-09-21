package org.yuri.aestheticnetwork.utils.Instances;

import org.yuri.aestheticnetwork.utils.Location;

public class UserData {
    Location back;
    boolean combat;
    long delay = 0L;

    public UserData(Location back,
                    boolean combat) {
        this.back = back;
        this.combat = combat;
    }

    public Location getBack() {
        return back;
    }

    public void setBack(Location a) {
        back = a;
    }

    public boolean getCombat() {
        return combat;
    }

    public void setCombat(boolean a) {
        combat = a;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long a) {
        delay = a;
    }
}
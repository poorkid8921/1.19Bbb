package org.yuri.aestheticnetwork.json;

import org.yuri.aestheticnetwork.commands.duel.DuelRequest;
import org.yuri.aestheticnetwork.commands.tpa.TpaRequest;
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
    public boolean getCombat() {
        return combat;
    }
    public long getDelay() {
        return delay;
    }

    public void setBack(Location a) {
        back = a;
    }
    public void setCombat(boolean a) {
        combat = a;
    }
    public void setDelay(long a) {
        delay = a;
    }
}
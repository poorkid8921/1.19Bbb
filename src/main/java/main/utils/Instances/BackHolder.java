package main.utils.Instances;

import main.utils.Location;

public class BackHolder {
    Location back;

    public BackHolder(Location back) {
        this.back = back;
    }

    public Location getBack() {
        return back;
    }

    public void setBack(Location a) {
        back = a;
    }
}
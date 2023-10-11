package main.utils.Instances;

import main.utils.Location;

import java.lang.ref.WeakReference;

public class BackHolder {
    WeakReference<Location> back;

    public BackHolder(Location back) {
        this.back = new WeakReference<>(back);
    }

    public Location getBack() {
        return back.get();
    }

    public void setBack(Location a) {
        back = new WeakReference<>(a);
    }
}
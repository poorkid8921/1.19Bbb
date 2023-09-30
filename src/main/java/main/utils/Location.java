package main.utils;

import org.bukkit.World;

import java.lang.ref.WeakReference;

public class Location {
    WeakReference<World> a;
    double[] b = { 0, 1, 2 };
    float[] c = { 0, 1 };

    public Location(World a, double b, double c, double d, float e, float f) {
        this.a = new WeakReference<>(a);
        this.b[0] = b;
        this.b[1] = c;
        this.b[2] = d;
        this.c[0] = e;
        this.c[1] = f;
    }

    public org.bukkit.Location to() {
        return new org.bukkit.Location(this.a.get(), this.b[0], this.b[1], this.b[2], this.c[0], this.c[1]);
    }
}

package main.utils.Instances;

public class LocationHolder {
    private final int[] xyz;

    public LocationHolder(int[] xyz) {
        this.xyz = xyz;
    }

    public int getX() {
        return xyz[0];
    }

    public int getY() {
        return xyz[1];
    }

    public int getZ() {
        return xyz[2];
    }
}

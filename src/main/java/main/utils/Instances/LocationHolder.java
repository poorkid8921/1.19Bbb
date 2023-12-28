package main.utils.Instances;

import lombok.Getter;

@Getter
public class LocationHolder {
    private final int x;
    private final int y;
    private final int z;

    public LocationHolder(int x,
                          int y,
                          int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

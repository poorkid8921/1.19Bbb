package main.utils.Instances;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegionHolder {
    int minX;
    int minY;
    int minZ;

    int maxX;
    int maxY;
    int maxZ;

    public RegionHolder(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;

        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }
}

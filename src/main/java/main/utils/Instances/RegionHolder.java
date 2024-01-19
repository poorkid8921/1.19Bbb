package main.utils.Instances;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegionHolder {
    int minX;
    int minZ;

    int maxX;
    int maxZ;

    public RegionHolder(int minX, int minZ, int maxX, int maxZ) {
        this.minX = minX;
        this.minZ = minZ;

        this.maxX = maxX;
        this.maxZ = maxZ;
    }
}

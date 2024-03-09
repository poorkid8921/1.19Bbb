package main.utils.instances;

public class RegionHolder {
    int minX;
    int minY;
    int minZ;

    int maxX;
    int maxY;
    int maxZ;

    public RegionHolder(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);

        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }

    public boolean check(int x, int y, int z) {
        return minX <= x && x <= maxX &&
                minY <= y && y <= maxY &&
                minZ <= z && z <= maxZ;
    }

    public boolean check(int x, int z) {
        return minX <= x && x <= maxX &&
                minZ <= z && z <= maxZ;
    }
}

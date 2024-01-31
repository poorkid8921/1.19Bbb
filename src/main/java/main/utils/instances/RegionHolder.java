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

    public boolean checkX(int x) {
        return x <= minX || x >= maxX;
    }

    public boolean checkY(int y) {
        return y <= minY || y >= maxY;
    }

    public boolean checkZ(int z) {
        return z <= minZ || z >= maxZ;
    }
}

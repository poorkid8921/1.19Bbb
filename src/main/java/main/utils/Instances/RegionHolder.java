package main.utils.Instances;

public class RegionHolder extends AbstractRegionHolder {
    private final int minX, minY, minZ, maxX, maxY, maxZ;

    public RegionHolder(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);

        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }

    @Override
    public boolean testY(int x, int y, int z) {
        return minX <= x && x <= maxX && minY <= y && y <= maxY && minZ <= z && z <= maxZ;
    }

    @Override
    public boolean test(int x, int z) {
        return minX <= x && x <= maxX && minZ <= z && z <= maxZ;
    }

    @Override
    public boolean damageTest(int x, int y, int z) {
        return minX <= x && x <= maxX && minY <= y && y <= maxY && minZ <= z && z <= maxZ;
    }
}

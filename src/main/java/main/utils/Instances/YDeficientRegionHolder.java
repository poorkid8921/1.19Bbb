package main.utils.Instances;

public class YDeficientRegionHolder extends AbstractRegionHolder {
    private final int minX, minZ, maxX, maxZ;

    public YDeficientRegionHolder(int minX, int minZ, int maxX, int maxZ) {
        this.minX = Math.min(minX, maxX);
        this.minZ = Math.min(minZ, maxZ);

        this.maxX = Math.max(minX, maxX);
        this.maxZ = Math.max(minZ, maxZ);
    }

    @Override
    public boolean testY(int x, int y, int z) {
        return test(x, z);
    }

    @Override
    public boolean test(int x, int z) {
        return minX <= x && x <= maxX && minZ <= z && z <= maxZ;
    }

    @Override
    public boolean testDamage(int x, int y, int z) {
        return test(x, z);
    }
}

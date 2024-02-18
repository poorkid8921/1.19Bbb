package main.utils.arenas;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

public class Section {
    private final Arena parent;
    private final Location start;
    private final Location end;
    private final short[] blockTypes;
    private final short[] blockAmounts;
    private final int ID;
    private int resetTypeIndex;
    private int resetLocationIndex;
    private int resetCurrentTypeIndex;
    private final int totalBlocks;
    private final int w;
    private final int l;

    Section(Arena parent, int ID, Location start, Location end, short[] blockTypes, short[] blockAmounts) {
        this.blockAmounts = blockAmounts;
        this.blockTypes = blockTypes;
        this.parent = parent;
        this.start = start;
        this.end = end;
        this.ID = ID;

        totalBlocks = (end.getBlockX() - start.getBlockX() + 1) *
                (end.getBlockY() - start.getBlockY() + 1) *
                (end.getBlockZ() - start.getBlockZ() + 1);
        w = end.getBlockX() - start.getBlockX() + 1;
        l = end.getBlockZ() - start.getBlockZ() + 1;
    }

    public boolean reset(int max) {
        resetTypeIndex = Math.max(resetTypeIndex, 0);
        resetLocationIndex = Math.max(resetLocationIndex, 0);
        int count = 0;
        BlockData blockData;
        Location loc = start.clone();
        while (resetTypeIndex < blockTypes.length) {
            short amount = this.blockAmounts[resetTypeIndex];
            blockData = this.getParent().getKeys()[this.blockTypes[resetTypeIndex]].createBlockData();
            while (resetCurrentTypeIndex < amount) {
                loc.add(Arena.getLocationAtIndex(w, l, resetLocationIndex))
                        .getBlock()
                        .setBlockData(blockData, false);
                loc = start.clone();
                resetCurrentTypeIndex++;
                resetLocationIndex++;
                if (max > 0 && count++ > max) return false;
            }

            resetCurrentTypeIndex = 0;
            resetTypeIndex++;
        }

        resetTypeIndex = 0;
        resetLocationIndex = -1;

        return true;
    }

    public int getTotalBlocks() {
        return totalBlocks;
    }

    public Location getStart() {
        return start;
    }

    public Location getEnd() {
        return end;
    }

    public short[] getBlockAmounts() {
        return blockAmounts;
    }

    public short[] getBlockTypes() {
        return blockTypes;
    }

    public int getID() {
        return ID;
    }

    public Arena getParent() {
        return parent;
    }
}

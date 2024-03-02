package main.utils.arenas;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class Section {
    @Getter
    private static int totalBlocks;
    @Getter
    private final Arena parent;
    @Getter
    private final Location start;
    @Getter
    private final Location end;
    @Getter
    private final short[] blockTypes;
    @Getter
    private final short[] blockAmounts;
    @Getter
    private final int ID;
    private final int w;
    private final int l;
    private int resetTypeIndex;
    private int resetLocationIndex;
    private int resetCurrentTypeIndex;

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
        if (resetTypeIndex < 0)
            resetTypeIndex = 0;
        if (resetLocationIndex < 0)
            resetLocationIndex = 0;
        int count = 0;
        Material[] keys = this.getParent().getKeys();
        Material cachedMat = keys[this.blockTypes[resetTypeIndex]];
        BlockData blockData = cachedMat.createBlockData();
        Material currentMat;
        while (resetTypeIndex < blockTypes.length) {
            currentMat = keys[this.blockTypes[resetTypeIndex]];
            if (currentMat != cachedMat) {
                blockData = currentMat.createBlockData();
                cachedMat = currentMat;
            }
            while (resetCurrentTypeIndex < this.blockAmounts[resetTypeIndex]) {
                start.clone().add(Arena.getLocationAtIndex(w, l, resetLocationIndex))
                        .getBlock()
                        .setBlockData(blockData, false);
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
}

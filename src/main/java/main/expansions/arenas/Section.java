package main.expansions.arenas;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

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
    private int blocksResetThisTick = 0;

    Section(Arena parent, int ID, Location start, Location end, short[] blockTypes, short[] blockAmounts) {
        this.blockAmounts = blockAmounts;
        this.blockTypes = blockTypes;
        this.parent = parent;
        this.start = start;
        this.end = end;
        this.ID = ID;
    }

    public boolean reset(int max) {
        int w = getEnd().getBlockX() - getStart().getBlockX() + 1;
        int l = getEnd().getBlockZ() - getStart().getBlockZ() + 1;

        if (resetTypeIndex < 0) {
            resetTypeIndex = 0;
        }

        if (resetLocationIndex < 0) {
            resetLocationIndex = 0;
        }

        int count = 0;
        blocksResetThisTick = 0;
        World ww = getStart().getWorld();

        while (resetTypeIndex < blockTypes.length) {
            short type = this.blockTypes[this.resetTypeIndex];
            short amount = this.blockAmounts[this.resetTypeIndex];

            Material data = this.getParent().getKeys()[type];

            while (resetCurrentTypeIndex < amount) {
                Location offset = Arena.getLocationAtIndex(w, l, ww, resetLocationIndex);

                Block block = getStart().add(offset).getBlock();
                getStart().subtract(offset);
                if (block.getType() != data) block.setType(data, false);

                count++;
                resetCurrentTypeIndex++;
                blocksResetThisTick++;
                resetLocationIndex++;

                if (max > 0 && count > max) return false;
            }

            resetCurrentTypeIndex = 0;
            resetTypeIndex++;
        }

        resetTypeIndex = 0;
        resetLocationIndex = -1;
        resetCurrentTypeIndex = 0;

        return true;
    }

    public int getTotalBlocks() {
        return (getEnd().getBlockX() - getStart().getBlockX() + 1) *
                (getEnd().getBlockY() - getStart().getBlockY() + 1) *
                (getEnd().getBlockZ() - getStart().getBlockZ() + 1);
    }

    protected int getBlocksResetThisTick() {
        return blocksResetThisTick;
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

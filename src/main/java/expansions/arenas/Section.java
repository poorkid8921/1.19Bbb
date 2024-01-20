package expansions.arenas;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

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
        resetTypeIndex = Math.max(resetTypeIndex, 0);
        resetLocationIndex = Math.max(resetLocationIndex, 0);
        World ww = getStart().getWorld();
        int count = 0;
        while (resetTypeIndex < blockTypes.length) {
            short type = this.blockTypes[this.resetTypeIndex];
            short amount = this.blockAmounts[this.resetTypeIndex];
            Material data = this.getParent().getKeys()[type];
            while (resetCurrentTypeIndex < amount) {
                Location offset = Arena.getLocationAtIndex(w, l, ww, resetLocationIndex);
                getStart().add(offset).getBlock().setType(data, false);
                getStart().subtract(offset);
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
        return (getEnd().getBlockX() - getStart().getBlockX() + 1) *
                (getEnd().getBlockY() - getStart().getBlockY() + 1) *
                (getEnd().getBlockZ() - getStart().getBlockZ() + 1);
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

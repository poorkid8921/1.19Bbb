package main.utils.arenas;

import io.papermc.lib.PaperLib;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static main.utils.arenas.BlockChanger.getBlockData;

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

    public Section(Arena parent, int ID, Location start, Location end, short[] blockTypes, short[] blockAmounts) {
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
        ItemStack currentMat = new ItemStack(Material.AIR);
        Location loc;
        Object blockData;
        while (resetTypeIndex < blockTypes.length) {
            currentMat.setType(keys[this.blockTypes[resetTypeIndex]]);
            blockData = getBlockData(currentMat);
            Object finalBlockData = blockData;
            while (resetCurrentTypeIndex < this.blockAmounts[resetTypeIndex]) {
                loc = start.clone().add(Arena.getLocationAtIndex(w, l, resetLocationIndex));
                Location finalLoc = loc;
                PaperLib.getChunkAtAsync(loc).thenAccept(z -> BlockChanger.setChunkBlockAsynchronouslyUpdate(finalLoc, finalBlockData, false));
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

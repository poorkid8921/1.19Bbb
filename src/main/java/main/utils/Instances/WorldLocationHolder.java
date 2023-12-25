package main.utils.Instances;

import lombok.Getter;
import org.bukkit.World;

@Getter
public class WorldLocationHolder {
    private final int x;
    private final int y;
    private final int z;
    private final World world;

    public WorldLocationHolder(int x,
                               int y,
                               int z,
                               World w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = w;
    }
}

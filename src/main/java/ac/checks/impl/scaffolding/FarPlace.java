package ac.checks.impl.scaffolding;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import ac.checks.CheckData;
import ac.checks.type.BlockPlaceCheck;
import ac.player.GrimPlayer;
import ac.utils.anticheat.update.BlockPlace;
import ac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.utils.math.VectorUtils;
import org.bukkit.util.Vector;

@CheckData(name = "FarPlace")
public class FarPlace extends BlockPlaceCheck {
    public FarPlace(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onBlockPlace(final BlockPlace place) {
        Vector3i blockPos = place.getPlacedAgainstBlockLocation();

        if (place.getMaterial() == StateTypes.SCAFFOLDING) return;

        double min = Double.MAX_VALUE;
        for (double d : player.getPossibleEyeHeights()) {
            SimpleCollisionBox box = new SimpleCollisionBox(blockPos);
            Vector eyes = new Vector(player.x, player.y + d, player.z);
            Vector best = VectorUtils.cutBoxToVector(eyes, box);
            min = Math.min(min, eyes.distanceSquared(best));
        }

        // getPickRange() determines this?
        double maxReach = player.gamemode == GameMode.CREATIVE ? 6.0 : 4.5D;
        double threshold = player.getMovementThreshold();
        maxReach += Math.hypot(threshold, threshold);


        if (min > maxReach * maxReach) { // fail
            if (flag() && shouldModifyPackets() && shouldCancel()) {
                place.resync();
            }
        }
    }
}

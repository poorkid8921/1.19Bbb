package ac.checks.impl.scaffolding;

import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3f;
import ac.checks.CheckData;
import ac.checks.type.BlockPlaceCheck;
import ac.player.GrimPlayer;
import ac.utils.anticheat.update.BlockPlace;
import ac.utils.nmsutil.Materials;

@CheckData(name = "FabricatedPlace")
public class FabricatedPlace extends BlockPlaceCheck {
    public FabricatedPlace(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onBlockPlace(final BlockPlace place) {
        Vector3f cursor = place.getCursor();
        if (cursor == null) return;

        double allowed = Materials.isShapeExceedsCube(place.getPlacedAgainstMaterial()) || place.getPlacedAgainstMaterial() == StateTypes.LECTERN ? 1.5 : 1;
        double minAllowed = 1 - allowed;

        if (cursor.getX() < minAllowed || cursor.getY() < minAllowed || cursor.getZ() < minAllowed || cursor.getX() > allowed || cursor.getY() > allowed || cursor.getZ() > allowed) {
            if (flag() && shouldModifyPackets() && shouldCancel()) {
                place.resync();
            }
        }
    }
}

package ac.checks.impl.scaffolding;

import ac.checks.CheckData;
import ac.checks.type.BlockPlaceCheck;
import ac.player.GrimPlayer;
import ac.utils.anticheat.update.BlockPlace;
import ac.utils.anticheat.update.RotationUpdate;

@CheckData(name = "DuplicateRotPlace", experimental = true)
public class DuplicateRotPlace extends BlockPlaceCheck {
    private float deltaX;
    private boolean rotated = false;
    private float lastPlacedDeltaX;

    public DuplicateRotPlace(GrimPlayer player) {
        super(player);
    }

    @Override
    public void process(final RotationUpdate rotationUpdate) {
        deltaX = rotationUpdate.getDeltaXRotABS();
        rotated = true;
    }

    public void onPostFlyingBlockPlace(BlockPlace place) {
        if (rotated) {
            if (deltaX > 2) {
                float xDiff = Math.abs(deltaX - lastPlacedDeltaX);
                if (xDiff < 0.0001) {
                    flag();
                } else {
                    reward();
                }
            } else {
                reward();
            }
            this.lastPlacedDeltaX = deltaX;
            rotated = false;
        }
    }
}

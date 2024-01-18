package ac.checks.impl.baritone;

import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.RotationCheck;
import ac.player.GrimPlayer;
import ac.utils.anticheat.update.RotationUpdate;
import ac.utils.data.HeadRotation;
import ac.utils.math.GrimMath;

@CheckData(name = "Baritone")
public class Baritone extends Check implements RotationCheck {
    private int verbose;

    public Baritone(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(final RotationUpdate rotationUpdate) {
        final HeadRotation from = rotationUpdate.getFrom();
        final HeadRotation to = rotationUpdate.getTo();

        final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

        // Baritone works with small degrees, limit to 1 degrees to pick up on baritone slightly moving aim to bypass anticheats
        if (rotationUpdate.getDeltaXRot() == 0 && deltaPitch > 0 && deltaPitch < 1 && Math.abs(to.getPitch()) != 90.0f) {
            if (rotationUpdate.getProcessor().divisorY < GrimMath.MINIMUM_DIVISOR) {
                verbose++;
                if (verbose > 8) {
                    flag();
                }
            } else {
                verbose = 0;
            }
        }
    }
}

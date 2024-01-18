package ac.checks.impl.aim;

import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.RotationCheck;
import ac.player.GrimPlayer;
import ac.utils.anticheat.update.RotationUpdate;

// Based on Kauri AimA,
// I also discovered this flaw before open source Kauri, but did not want to open source its detection.
// It works on clients who % 360 their rotation.
@CheckData(name = "AimModulo360", decay = 0.005)
public class AimModulo360 extends Check implements RotationCheck {
    float lastDeltaYaw;

    public AimModulo360(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(final RotationUpdate rotationUpdate) {
        lastDeltaYaw = rotationUpdate.getDeltaXRot();
        if (player.packetStateData.lastPacketWasTeleport) return;
        if (player.xRot < 360 && player.xRot > -360 && Math.abs(rotationUpdate.getDeltaXRot()) > 320 && Math.abs(lastDeltaYaw) < 30) {
            flag();
        } else {
            reward();
        }
    }
}

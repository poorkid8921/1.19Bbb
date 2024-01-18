package ac.checks.impl.movement;

import ac.checks.Check;
import ac.checks.type.PositionCheck;
import ac.player.GrimPlayer;
import ac.utils.anticheat.update.PositionUpdate;

public class PredictionRunner extends Check implements PositionCheck {
    public PredictionRunner(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPositionUpdate(final PositionUpdate positionUpdate) {
        if (!player.compensatedEntities.getSelf().inVehicle()) {
            player.movementCheckRunner.processAndCheckMovementPacket(positionUpdate);
        }
    }
}

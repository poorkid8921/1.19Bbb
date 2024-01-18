package ac.checks.impl.movement;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PostPredictionCheck;
import ac.player.GrimPlayer;
import ac.utils.anticheat.update.PredictionComplete;

@CheckData(name = "NoSlowA (Prediction)", configName = "NoSlowA", setback = 5)
public class NoSlowA extends Check implements PostPredictionCheck {
    // The player sends that they switched items the next tick if they switch from an item that can be used
    // to another item that can be used.  What the fuck mojang.  Affects 1.8 (and most likely 1.7) clients.
    public boolean didSlotChangeLastTick = false;
    public boolean flaggedLastTick = false;
    double bestOffset = 1;

    public NoSlowA(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        if (!predictionComplete.isChecked()) return;

        // If the player was using an item for certain, and their predicted velocity had a flipped item
        if (player.packetStateData.slowedByUsingItem) {
            // 1.8 users are not slowed the first tick they use an item, strangely
            if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8) && didSlotChangeLastTick) {
                didSlotChangeLastTick = false;
                flaggedLastTick = false;
            }

            if (bestOffset > 0.001) {
                if (flaggedLastTick) {
                    flagWithSetback();
                    flag();
                }
                flaggedLastTick = true;
            } else {
                reward();
                flaggedLastTick = false;
            }
        }
        bestOffset = 1;
    }

    public void handlePredictionAnalysis(double offset) {
        bestOffset = Math.min(bestOffset, offset);
    }
}

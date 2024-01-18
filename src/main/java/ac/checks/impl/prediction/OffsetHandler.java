package ac.checks.impl.prediction;

import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PostPredictionCheck;
import ac.player.GrimPlayer;
import ac.utils.anticheat.update.PredictionComplete;

import java.util.concurrent.atomic.AtomicInteger;

@CheckData(name = "Simulation", configName = "Simulation", decay = 0.02)
public class OffsetHandler extends Check implements PostPredictionCheck {
    private static final AtomicInteger flags = new AtomicInteger(0);
    // Current advantage gained
    double advantageGained = 0;

    public OffsetHandler(GrimPlayer player) {
        super(player);
    }

    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        double offset = predictionComplete.getOffset();

        if (!predictionComplete.isChecked()) return;

        // Short circuit out flag call
        if (offset >= 0.001 && flag()) {
            advantageGained += offset;

            boolean isSetback = advantageGained >= 1 || offset >= 0.1;
            giveOffsetLenienceNextTick(offset);

            if (isSetback) {
                player.getSetbackTeleportUtil().executeViolationSetback();
            }

            violations++;

            synchronized (flags) {
                int flagId = (flags.get() & 255) + 1; // 1-256 as possible values
                flags.incrementAndGet(); // This debug was sent somewhere
                predictionComplete.setIdentifier(flagId);
            }


            advantageGained = Math.min(advantageGained, 4);
        } else {
            advantageGained *= 0.999;
        }
    }

    private void giveOffsetLenienceNextTick(double offset) {
        // Don't let players carry more than 1 offset into the next tick
        // (I was seeing cheats try to carry 1,000,000,000 offset into the next tick!)
        //
        // This value so that setting back with high ping doesn't allow players to gather high client velocity
        double minimizedOffset = Math.min(offset, 1);

        // Normalize offsets
        player.uncertaintyHandler.lastHorizontalOffset = minimizedOffset;
        player.uncertaintyHandler.lastVerticalOffset = minimizedOffset;
    }
}

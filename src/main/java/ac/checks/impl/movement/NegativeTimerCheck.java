package ac.checks.impl.movement;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import ac.checks.CheckData;
import ac.checks.type.PostPredictionCheck;
import ac.player.GrimPlayer;
import ac.utils.anticheat.update.PredictionComplete;

@CheckData(name = "NegativeTimer", configName = "NegativeTimer", setback = 10, experimental = true)
public class NegativeTimerCheck extends TimerCheck implements PostPredictionCheck {

    public NegativeTimerCheck(GrimPlayer player) {
        super(player);
        timerBalanceRealTime = System.nanoTime() + clockDrift;
    }

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        // We can't negative timer check a 1.9+ player who is standing still.
        if (!player.canThePlayerBeCloseToZeroMovement(2) || !predictionComplete.isChecked()) {
            timerBalanceRealTime = System.nanoTime() + clockDrift;
        }

        if (timerBalanceRealTime < lastMovementPlayerClock - clockDrift) {
            int lostMS = (int) ((System.nanoTime() - timerBalanceRealTime) / 1e6);
            flag();
            timerBalanceRealTime += 50e6;
        }
    }

    @Override
    public void doCheck(final PacketReceiveEvent event) {
        // We don't know if the player is ticking stable, therefore we must wait until prediction
        // determines this.  Do nothing here!
    }

    @Override
    public void reload() {
        super.reload();
        clockDrift = 1200000000L;
    }
}

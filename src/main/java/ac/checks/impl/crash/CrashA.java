package ac.checks.impl.crash;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PacketCheck;
import ac.player.GrimPlayer;

@CheckData(name = "CrashA")
public class CrashA extends Check implements PacketCheck {
    private static final double HARD_CODED_BORDER = 2.9999999E7D;

    public CrashA(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (player.packetStateData.lastPacketWasTeleport) return;
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying packet = new WrapperPlayClientPlayerFlying(event);

            if (!packet.hasPositionChanged()) return;
            // Y technically is uncapped, but no player will reach these values legit
            if (Math.abs(packet.getLocation().getX()) > HARD_CODED_BORDER || Math.abs(packet.getLocation().getZ()) > HARD_CODED_BORDER || Math.abs(packet.getLocation().getY()) > Integer.MAX_VALUE) {
                flag(); // Ban
                player.getSetbackTeleportUtil().executeViolationSetback();
                event.setCancelled(true);
                player.onPacketCancel();
            }
        }
    }
}

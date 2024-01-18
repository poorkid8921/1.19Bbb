package ac.checks.impl.movement;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PacketCheck;
import ac.player.GrimPlayer;

@CheckData(name = "NoSlowB", setback = 5)
public class NoSlowB extends Check implements PacketCheck {

    public NoSlowB(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            // Players can sprint if they're able to fly (MCP)
            if (player.canFly) return;

            if (player.food < 6.0F && player.isSprinting) {
                if (flag()) {
                    // Cancel the packet
                    if (shouldModifyPackets()) {
                        event.setCancelled(true);
                        player.onPacketCancel();
                    }
                    player.getSetbackTeleportUtil().executeNonSimulatingSetback();
                }
            } else {
                reward();
            }
        }
    }
}
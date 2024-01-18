package ac.checks.impl.crash;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PacketCheck;
import ac.player.GrimPlayer;

@CheckData(name = "CrashB")
public class CrashB extends Check implements PacketCheck {
    public CrashB(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            if (player.gamemode != GameMode.CREATIVE) {
                player.getSetbackTeleportUtil().executeViolationSetback();
                event.setCancelled(true);
                player.onPacketCancel();
                flag(); // Could be transaction split, no need to setback though
            }
        }
    }
}

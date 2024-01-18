package ac.checks.impl.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PacketCheck;
import ac.player.GrimPlayer;

@CheckData(name = "BadPacketsJ")
public class BadPacketsJ extends Check implements PacketCheck {
    public BadPacketsJ(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
            if (!player.compensatedEntities.getSelf().inVehicle()) {
                flag();
            }
        }
    }
}

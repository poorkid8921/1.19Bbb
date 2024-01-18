package ac.checks.impl.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle;
import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PacketCheck;
import ac.player.GrimPlayer;

@CheckData(name = "BadPacketsB")
public class BadPacketsB extends Check implements PacketCheck {
    public BadPacketsB(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
            WrapperPlayClientSteerVehicle packet = new WrapperPlayClientSteerVehicle(event);

            float forwards = Math.abs(packet.getForward());
            float sideways = Math.abs(packet.getSideways());

            if (forwards > 0.98f || sideways > 0.98f) {
                flag();
            }
        }
    }
}

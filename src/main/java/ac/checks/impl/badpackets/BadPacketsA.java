package ac.checks.impl.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PacketCheck;
import ac.player.GrimPlayer;

@CheckData(name = "BadPacketsA")
public class BadPacketsA extends Check implements PacketCheck {
    int lastSlot = -1;

    public BadPacketsA(final GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            WrapperPlayClientHeldItemChange packet = new WrapperPlayClientHeldItemChange(event);

            int slot = packet.getSlot();

            if (slot == lastSlot) {
                flag();
            }

            lastSlot = packet.getSlot();
        }
    }
}

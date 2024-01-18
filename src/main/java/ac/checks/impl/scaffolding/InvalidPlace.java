package ac.checks.impl.scaffolding;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PacketCheck;
import ac.player.GrimPlayer;

@CheckData(name = "InvalidPlace")
public class InvalidPlace extends Check implements PacketCheck {

    public InvalidPlace(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(event);
            Vector3f cursor = wrapper.getCursorPosition();
            if (cursor == null) return;
            if (invalid(cursor.getX()) || invalid(cursor.getY()) || invalid(cursor.getZ())) {
                if (flag() && shouldModifyPackets()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }
        }
    }

    private boolean invalid(float value) {
        return Float.isInfinite(value) || Float.isNaN(value);
    }

}

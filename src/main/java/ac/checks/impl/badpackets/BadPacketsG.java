package ac.checks.impl.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PacketCheck;
import ac.player.GrimPlayer;

@CheckData(name = "BadPacketsG")
public class BadPacketsG extends Check implements PacketCheck {
    boolean wasTeleport;
    boolean lastSneaking;

    public BadPacketsG(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        wasTeleport = player.packetStateData.lastPacketWasTeleport || wasTeleport;

        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            WrapperPlayClientEntityAction packet = new WrapperPlayClientEntityAction(event);

            if (packet.getAction() == WrapperPlayClientEntityAction.Action.START_SNEAKING) {
                if (lastSneaking && !wasTeleport) {
                    flag();
                } else {
                    lastSneaking = true;
                }
            } else if (packet.getAction() == WrapperPlayClientEntityAction.Action.STOP_SNEAKING) {
                if (!lastSneaking && !wasTeleport) {
                    flag();
                } else {
                    lastSneaking = false;
                }
            }
        }
    }
}

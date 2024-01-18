package ac.checks.impl.crash;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;
import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PacketCheck;
import ac.player.GrimPlayer;

@CheckData(name = "CrashE", experimental = false)
public class CrashE extends Check implements PacketCheck {

    public CrashE(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_SETTINGS) {
            WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event);
            int viewDistance = wrapper.getViewDistance();
            if (viewDistance < 2) {
                flag();
                wrapper.setViewDistance(2);
            }
        }
    }

}

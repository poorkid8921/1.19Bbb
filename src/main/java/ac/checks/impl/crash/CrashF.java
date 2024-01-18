package ac.checks.impl.crash;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PacketCheck;
import ac.player.GrimPlayer;

@CheckData(name = "CrashF")
public class CrashF extends Check implements PacketCheck {

    public CrashF(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow click = new WrapperPlayClientClickWindow(event);
            int clickType = click.getWindowClickType().ordinal();
            int button = click.getButton();
            int windowId = click.getWindowId();
            int slot = click.getSlot();

            if (windowId >= 0 && clickType == 2 && slot < 0 || (clickType == 1 || clickType == 2) && windowId >= 0 && button < 0) {
                if (flag()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }
        }
    }

}

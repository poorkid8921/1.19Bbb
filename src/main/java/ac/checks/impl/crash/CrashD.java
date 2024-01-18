package ac.checks.impl.crash;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PacketCheck;
import ac.player.GrimPlayer;
import ac.utils.inventory.inventory.MenuType;

@CheckData(name = "CrashD", experimental = false)
public class CrashD extends Check implements PacketCheck {

    private MenuType type = MenuType.UNKNOWN;
    private int lecternId = -1;
    public CrashD(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPacketSend(final PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW && isSupportedVersion()) {
            WrapperPlayServerOpenWindow window = new WrapperPlayServerOpenWindow(event);
            this.type = MenuType.getMenuType(window.getType());
            if (type == MenuType.LECTERN) lecternId = window.getContainerId();
        }
    }

    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW && isSupportedVersion()) {
            WrapperPlayClientClickWindow click = new WrapperPlayClientClickWindow(event);
            int windowId = click.getWindowId();

            if (type == MenuType.LECTERN && windowId > 0 && windowId == lecternId) {
                if (flag()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }
        }
    }

    private boolean isSupportedVersion() {
        return PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_14);
    }

}

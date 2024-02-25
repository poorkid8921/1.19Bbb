package main.utils.optimizer;

import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.entity.Player;

import static main.utils.Initializer.playerData;

public class LastPacketEvent extends SimplePacketListenerAbstract {
    public LastPacketEvent() {
        super(PacketListenerPriority.MONITOR);
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        Player p = (Player) event.getPlayer();
        if (p == null)
            return;

        CustomPlayerDataHolder user = playerData.get(p.getName());
        int animPacket = getAnimPacket(event);
        if (user.getLastPacket() == 0)
            user.setIgnoreAnim(animPacket == 3);
        user.setLastPacket(animPacket);
    }

    private int getAnimPacket(PacketPlayReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            return 0;
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(event);
            if (wrapper.getAction() == DiggingAction.DROP_ITEM
                    || wrapper.getAction() == DiggingAction.DROP_ITEM_STACK) {
                return 4;
            } else if (wrapper.getAction() == DiggingAction.START_DIGGING) {
                return 1;
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);
            if (wrapper.getWindowClickType() == WrapperPlayClientClickWindow.WindowClickType.THROW ||
                    (wrapper.getWindowClickType() == WrapperPlayClientClickWindow.WindowClickType.PICKUP
                            && wrapper.getSlot() == -999)) {
                return 3;
            }
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT
                || event.getPacketType() == PacketType.Play.Client.USE_ITEM) {
            return 4;
        } else if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
            if (wrapper.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                return 2;
            }
        }
        return 5;
    }
}

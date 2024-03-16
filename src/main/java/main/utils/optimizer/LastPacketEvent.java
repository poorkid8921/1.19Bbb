package main.utils.optimizer;

import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import main.utils.instances.CustomPlayerDataHolder;
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
        if (!user.isFastCrystals())
            return;
        int animPacket = getAnimPacket(event);
        if (user.getLastPacket() == 0)
            user.setIgnoreAnim(animPacket == 3);
        user.setLastPacket(animPacket);
    }

    private int getAnimPacket(PacketPlayReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING)
            return new WrapperPlayClientPlayerDigging(event).getAction() == DiggingAction.START_DIGGING ? 1 : 0;
        else if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);
            return (wrapper.getWindowClickType() == WrapperPlayClientClickWindow.WindowClickType.THROW ||
                    (wrapper.getWindowClickType() == WrapperPlayClientClickWindow.WindowClickType.PICKUP
                            && wrapper.getSlot() == -999)) ? 3 : 0;
        } else if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY)
            return new WrapperPlayClientInteractEntity(event).getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK ? 2 : 0;
        return 0;
    }
}

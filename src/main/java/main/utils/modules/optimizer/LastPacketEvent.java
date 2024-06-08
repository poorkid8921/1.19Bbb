package main.utils.modules.optimizer;

import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import io.netty.buffer.ByteBuf;
import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.modules.anticheat.AutoTotem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static main.utils.Initializer.playerData;

public class LastPacketEvent extends SimplePacketListenerAbstract {
    public LastPacketEvent() {
        super(PacketListenerPriority.MONITOR);
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        final Object uncastedPlayer = event.getPlayer();
        if (uncastedPlayer == null)
            return;
        final Player player = (Player) uncastedPlayer;
        final String name = player.getName();
        // using_autototem; payload packet
        if (event.getPacketId() == 0x0C) {
            Bukkit.getLogger().warning(((ByteBuf) event.getByteBuf()).toString());
            return;
        }
        final CustomPlayerDataHolder user = playerData.get(name);
        final PacketType.Play.Client type = event.getPacketType();
        final int animPacket = getAnimPacket(event, type);
        switch (type) {
            case HELD_ITEM_CHANGE, PLAYER_DIGGING, PICK_ITEM -> {
                final int oldLastPacket = user.getLastItemPacket();
                final int preLastPacket = user.getPreLastPacket();
                if (((preLastPacket == 5 && oldLastPacket == 6 && animPacket == 5 &&
                        AutoTotem.tryBanningSync(player, name, user, true)) ||
                        (preLastPacket == 4 && oldLastPacket == 5 && animPacket == 4) //&&
                        /*AutoTotem.tryBanningSync(player, name, user, false)*/)) {
                    Bukkit.getLogger().warning(name + " | " + preLastPacket + " | " + oldLastPacket + " | " + animPacket);
                    return;
                }
                user.setPreLastPacket(oldLastPacket);
                user.setLastItemPacket(animPacket);
            }
        }
        if (!user.isFastCrystals())
            return;
        user.setIgnoreAnim(animPacket == 3 && user.getLastPacket() == 0);
        user.setLastPacket(animPacket);
    }

    private int getAnimPacket(PacketPlayReceiveEvent event, PacketType.Play.Client packetType) {
        switch (packetType) {
            case PLAYER_DIGGING -> {
                final DiggingAction action = new WrapperPlayClientPlayerDigging(event).getAction();
                return action == DiggingAction.START_DIGGING ? 1 : action == DiggingAction.SWAP_ITEM_WITH_OFFHAND ? 5 : 0;
            }
            case CLICK_WINDOW -> {
                final WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);
                return (wrapper.getWindowClickType() == WrapperPlayClientClickWindow.WindowClickType.THROW ||
                        (wrapper.getWindowClickType() == WrapperPlayClientClickWindow.WindowClickType.PICKUP
                                && wrapper.getSlot() == -999)) ? 3 : 0;
            }
            case INTERACT_ENTITY -> {
                return new WrapperPlayClientInteractEntity(event).getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK ? 2 : 0;
            }
            case HELD_ITEM_CHANGE -> {
                return 4;
            }
            case PICK_ITEM -> {
                return 6;
            }
            default -> {
                return 0;
            }
        }
    }
}

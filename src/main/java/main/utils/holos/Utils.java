package main.utils.holos;

import lombok.Getter;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;

import java.util.concurrent.atomic.AtomicInteger;

import static main.Practice.d;

public class Utils {
    public static LoopableHologramHolder[] holos = new LoopableHologramHolder[2];
    public static LoopableHologramHolder[] tickableHolos = new LoopableHologramHolder[8];

    public static void init() {
        AtomicInteger i = new AtomicInteger(-1);
        //AtomicInteger iTickable = new AtomicInteger(7);
        for (HologramHolder k : new HologramHolder[]{new HologramHolder(new String[]{"first2", "2nd2"}, -6.5D, -3.5D, -6.5D)}) {
            double x = k.getX();
            double z = k.getZ();
            double y = k.getY();
            for (String c : k.lines) {
                ArmorStand holo = new ArmorStand(((CraftWorld) d).getHandle(), x, y, z);
                holo.setInvisible(true);
                holo.setCustomNameVisible(true);
                holo.setMarker(true);
                holo.setCustomName(CraftChatMessage.fromString(c, false, false)[0]);
                holos[i.incrementAndGet()] = new LoopableHologramHolder(holo, new ClientboundAddEntityPacket(holo), new ClientboundSetEntityDataPacket(holo.getId(), holo.getEntityData().getNonDefaultValues()));
                y -= 0.3D;
            }
        }
        /*HologramHolder[] tickables = new HologramHolder[]{
                new HologramHolder(new String[]{"first", "2nd"}, -3.5D, -3.5D, -3.5D)};
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Initializer.p, () -> {
            for (HologramHolder k : tickables) {
                double x = k.getX();
                double z = k.getZ();
                double y = k.getY();
                for (String c : k.lines) {
                    ArmorStand holo = new ArmorStand(((CraftWorld) d).getHandle(), x, y, z);
                    holo.setInvisible(true);
                    holo.setCustomNameVisible(true);
                    holo.setMarker(true);
                    holo.setCustomName(CraftChatMessage.fromString(c)[0]);
                    tickableHolos[iTickable.incrementAndGet()] = new LoopableHologramHolder(holo, new ClientboundAddEntityPacket(holo), new ClientboundSetEntityDataPacket(holo.getId(), holo.getEntityData().getNonDefaultValues()));
                    y -= 0.3D;
                }
            }
        }, 0L, 6000L);*/
    }

    public static void showForPlayer(ServerGamePacketListenerImpl connection) {
        for (LoopableHologramHolder k : holos) {
            connection.send(k.ADD_ENTITY);
            connection.send(k.METADATA);
        }
    }

    public static void showForPlayerTickable(ServerGamePacketListenerImpl connection) {
        for (LoopableHologramHolder k : tickableHolos) {
            connection.send(k.ADD_ENTITY);
            connection.send(k.METADATA);
        }
    }

    public record LoopableHologramHolder(ArmorStand Hologram, ClientboundAddEntityPacket ADD_ENTITY,
                                         ClientboundSetEntityDataPacket METADATA) {

    }

    @Getter
    static class HologramHolder {
        String[] lines;
        double x, y, z;

        public HologramHolder(String[] lines, double x, double y, double z) {
            this.lines = lines;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}

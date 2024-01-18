package ac.checks.impl.movement;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import ac.checks.Check;
import ac.checks.type.PacketCheck;
import ac.player.GrimPlayer;

public class SetbackBlocker extends Check implements PacketCheck {
    public SetbackBlocker(GrimPlayer playerData) {
        super(playerData);
    }

    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            if (player.getSetbackTeleportUtil().cheatVehicleInterpolationDelay > 0) {
                event.setCancelled(true); // Player is in the vehicle
            }
        }

        // Don't block teleport packets
        if (player.packetStateData.lastPacketWasTeleport) return;

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            // The player must obey setbacks
            if (player.getSetbackTeleportUtil().shouldBlockMovement()) {
                event.setCancelled(true);
            }

            // Look is the only valid packet to send while in a vehicle
            if (player.compensatedEntities.getSelf().inVehicle() && event.getPacketType() != PacketType.Play.Client.PLAYER_ROTATION && !player.packetStateData.lastPacketWasTeleport) {
                event.setCancelled(true);
            }

            // The player is sleeping, should be safe to block position packets
            if (player.isInBed && new Vector3d(player.x, player.y, player.z).distanceSquared(player.bedPosition) > 1) {
                event.setCancelled(true);
            }

            // Player is dead
            if (player.compensatedEntities.getSelf().isDead) {
                event.setCancelled(true);
            }
        }

        if (event.getPacketType() == PacketType.Play.Client.VEHICLE_MOVE) {
            if (player.getSetbackTeleportUtil().shouldBlockMovement()) {
                event.setCancelled(true);
            }

            // Don't let a player move a vehicle when not in a vehicle
            if (!player.compensatedEntities.getSelf().inVehicle()) {
                event.setCancelled(true);
            }

            // A player is sleeping while in a vehicle
            if (player.isInBed) {
                event.setCancelled(true);
            }

            // Player is dead
            if (player.compensatedEntities.getSelf().isDead) {
                event.setCancelled(true);
            }
        }
    }
}

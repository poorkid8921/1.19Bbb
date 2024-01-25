package expansions.optimizer;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import main.utils.Constants;
import main.utils.Instances.CustomPlayerDataHolder;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEnderCrystal;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import static main.utils.Constants.playerData;

public class AnimationEvent extends SimplePacketListenerAbstract {
    Holder<DamageType> cachedHolder = Holder.a(new DamageType("player", 0.1f));

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.ANIMATION) return;

        Player player = (Player) event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) return;
        if (player.hasPotionEffect(PotionEffectType.WEAKNESS)) return; // ignore weakness hits, tool hits are slow anyway

        CustomPlayerDataHolder user = playerData.get(player.getName());
        AnimPackets lastPacket = user.getLastPacket();
        Bukkit.getScheduler().runTask(Constants.p, () -> {
            if (lastPacket == AnimPackets.IGNORE) return; // animation is for hotbar drop item/placement/use item
            if (user.isIgnoreAnim()) return;; // animation is for inventory drop item

            Location eyeLoc = player.getEyeLocation();
            RayTraceResult result = player.getWorld().rayTraceEntities(
                    eyeLoc,
                    player.getLocation().getDirection(),
                    3.0,
                    0.0,
                    entity -> {
                        if (entity.getType() != EntityType.PLAYER) return true;

                        Player p = (Player) entity;
                        if (p.getGameMode() == GameMode.SPECTATOR) return false;

                        return !player.getUniqueId().equals(p.getUniqueId()) && player.canSee(p);
                    }
            );
            if (result == null) return;

            Entity entity = result.getHitEntity();
            if (entity == null || entity.getType() != EntityType.ENDER_CRYSTAL) return;

            // Ignore if the entity was spawned in the same tick
            // This is to avoid "double popping" situations; see https://github.com/mcpvp-club/FasterCrystals/issues/3
            if (entity.getTicksLived() == 0) return;

            // Raytrace entity obtains position on the other side of the bounding box when the player eye location is
            //     within the bounding box. This causes the distance check to false positive.
            // Instead, ignore block raytrace checks if the crystal bounding box contains the eye vector.
            if (!entity.getBoundingBox().contains(eyeLoc.toVector())) {
                RayTraceResult bResult = player.rayTraceBlocks(player.getGameMode() == GameMode.CREATIVE ? 5.0 : 4.5);
                if (bResult != null) {
                    Block block = bResult.getHitBlock();
                    Vector eyeLocV = eyeLoc.toVector();
                    if (block != null) {
                        // Check if a block was in front of the end crystal
                        if (eyeLocV.distanceSquared(bResult.getHitPosition()) <= eyeLocV.distanceSquared(result.getHitPosition())) {
                            return;
                        }

                        // If true, it is in the middle of breaking a block
                        // We only want the beginning of left click inputs (begin mining or attack)
                        if (lastPacket != AnimPackets.START_DIGGING && lastPacket != AnimPackets.ATTACK) {
                            return;
                        }
                    }
                }
            }

            ((CraftEnderCrystal) entity)
                    .getHandle()
                    .a(new DamageSource(cachedHolder, ((CraftPlayer) player).getHandle()), 1);
        });
    }
}
package main.utils.optimizer;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import main.utils.Initializer;
import main.utils.instances.CustomPlayerDataHolder;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.bukkit.Bukkit;
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

import static main.utils.Initializer.playerData;

public class AnimationEvent extends SimplePacketListenerAbstract {
    Holder<DamageType> cachedHolder = Holder.a(new DamageType("player", 0.1f));

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.ANIMATION) return;

        Player player = (Player) event.getPlayer();
        if (player.hasPotionEffect(PotionEffectType.WEAKNESS)) return;
        CustomPlayerDataHolder user = playerData.get(player.getName());
        int lastPacket = user.getLastPacket();
        if (lastPacket == 4) return;
        if (user.isIgnoreAnim()) return;
        Bukkit.getScheduler().runTask(Initializer.p, () -> {
            Location eyeLoc = player.getEyeLocation();
            RayTraceResult result = player.getWorld().rayTraceEntities(
                    eyeLoc,
                    player.getLocation().getDirection(),
                    3.0,
                    0.0,
                    entity -> {
                        if (entity.getType() != EntityType.PLAYER) return true;
                        return player.getUniqueId() != entity.getUniqueId();
                    }
            );
            if (result == null) return;

            Entity entity = result.getHitEntity();
            if (entity == null || entity.getType() != EntityType.ENDER_CRYSTAL) return;
            if (entity.getTicksLived() == 0) return;
            if (!entity.getBoundingBox().contains(eyeLoc.toVector())) {
                RayTraceResult bResult = player.rayTraceBlocks(4.5);
                if (bResult != null) {
                    Block block = bResult.getHitBlock();
                    Vector eyeLocV = eyeLoc.toVector();
                    if (block != null && (lastPacket != 1 && lastPacket != 2 ||
                            eyeLocV.distanceSquared(bResult.getHitPosition()) <=
                                    eyeLocV.distanceSquared(result.getHitPosition())))
                        return;
                }
            }

            ((CraftEnderCrystal) entity)
                    .getHandle()
                    .a(new DamageSource(cachedHolder, ((CraftPlayer) player).getHandle()), 1);
        });
    }
}
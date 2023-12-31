package main.expansions.optimizer;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import main.utils.Constants;
import main.utils.Instances.CustomPlayerDataHolder;
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

import static main.utils.Constants.playerData;

public class AnimationEvent extends SimplePacketListenerAbstract {
    Holder<DamageType> cachedHolder = Holder.a(new DamageType("player", 0.1f));

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.ANIMATION) return;

        Player player = (Player) event.getPlayer();
        if (player.getPing() < 50) {
            playerData.get(player.getName()).incrementCPS(System.currentTimeMillis());
            return;
        }

        if (player.hasPotionEffect(PotionEffectType.WEAKNESS)) return;
        String pn = player.getName();
        CustomPlayerDataHolder user = playerData.get(pn);
        user.incrementCPS(System.currentTimeMillis());
        Bukkit.getScheduler().runTask(Constants.p, () -> {
            if (user.getLastPacket() == AnimPackets.IGNORE) return;
            if (user.isIgnoreAnim()) return;

            Location eyeLoc = player.getEyeLocation();
            RayTraceResult result = player.getWorld().rayTraceEntities(eyeLoc, player.getLocation().getDirection(), 3.0, 0.0, entity -> {
                if (entity.getType() != EntityType.PLAYER) return true;
                Player p = (Player) entity;
                return !player.getUniqueId().equals(p.getUniqueId()) && player.canSee(p);
            });
            if (result == null) return;

            Entity entity = result.getHitEntity();
            if (entity == null ||
                    entity.getType() != EntityType.ENDER_CRYSTAL) return;
            if (entity.getTicksLived() == 0) return;

            if (!entity.getBoundingBox().contains(eyeLoc.toVector())) {
                RayTraceResult bResult = player.rayTraceBlocks(4.5);
                if (bResult != null) {
                    Block block = bResult.getHitBlock();
                    Vector eyeLocV = eyeLoc.toVector();
                    if (block != null && (eyeLocV.distanceSquared(bResult.getHitPosition()) <=
                            eyeLocV.distanceSquared(result.getHitPosition()) ||
                            user.getLastPacket() != AnimPackets.START_DIGGING && user.getLastPacket() != AnimPackets.ATTACK))
                        return;
                }
            }

            ((CraftEnderCrystal) entity)
                    .getHandle()
                    .a(new DamageSource(cachedHolder, ((CraftPlayer) player).getHandle()), 1);
        });
    }
}
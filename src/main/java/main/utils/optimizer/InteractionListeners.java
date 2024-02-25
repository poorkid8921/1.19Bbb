package main.utils.optimizer;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import main.utils.Instances.CustomPlayerDataHolder;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEnderCrystal;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import static main.utils.Initializer.*;

public class InteractionListeners extends SimplePacketListenerAbstract {
    Holder<DamageType> cachedHolder = Holder.a(new DamageType("player", 0.1f));

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        switch (event.getPacketType()) {
            case ANIMATION -> {
                Player player = (Player) event.getPlayer();
                if (player.hasPotionEffect(PotionEffectType.WEAKNESS))
                    return;

                CustomPlayerDataHolder user = playerData.get(player.getName());
                int lastPacket = user.getLastPacket();
                Bukkit.getScheduler().runTask(p, () -> {
                    if (lastPacket == 3) return;
                    if (user.isIgnoreAnim()) return;
                    Location eyeLoc = player.getEyeLocation();
                    RayTraceResult result = player.getWorld().rayTraceEntities(
                            eyeLoc,
                            player.getLocation().getDirection(),
                            3.0,
                            0.0,
                            entity -> {
                                if (entity.getType() != EntityType.PLAYER) return true;

                                Player p = (Player) entity;
                                return !player.getUniqueId().equals(p.getUniqueId()) && player.canSee(p);
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
                            if (block != null) {
                                if (eyeLocV.distanceSquared(bResult.getHitPosition()) <=
                                        eyeLocV.distanceSquared(result.getHitPosition()) ||
                                        (lastPacket != 1 && lastPacket != 2))
                                    return;
                            }
                        }
                    }

                    ((CraftEnderCrystal) entity)
                            .getHandle()
                            .a(new DamageSource(cachedHolder, ((CraftPlayer) player).getHandle()), 1);
                });

            }
            case INTERACT_ENTITY -> {
                WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
                if (wrapper.getAction() != WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT) return;
                Player player = (Player) event.getPlayer();
                ItemStack item = wrapper.getHand() == InteractionHand.MAIN_HAND ?
                        player.getInventory().getItemInMainHand() :
                        player.getInventory().getItemInOffHand();
                if (item.getType() != Material.END_CRYSTAL) return;
                int entityId = wrapper.getEntityId();
                Location entity = crystalsToBeOptimized.get(entityId);
                if (entity == null) return;
                Location blockLoc = entity.clone().subtract(0.5, 1.0, 0.5);
                RayTraceResult result = player.rayTraceBlocks(4.5,
                        FluidCollisionMode.NEVER);
                if (result == null || result.getHitBlock().getType() != Material.OBSIDIAN) return;
                if (!result.getHitBlock().getLocation().equals(blockLoc)) return;
                Bukkit.getScheduler().runTask(p, () -> {
                    Location clonedLoc = entity.clone().subtract(0.5, 0.0, 0.5);
                    if (clonedLoc.getBlock().getType() != Material.AIR) return;

                    clonedLoc.add(0.5, 1.0, 0.5);
                    if (clonedLoc.getWorld().getNearbyEntities(clonedLoc, 0.5, 1, 0.5).isEmpty()) {
                        entity.getWorld().spawn(clonedLoc.subtract(0.0, 1.0, 0.0), EnderCrystal.class, c -> c.setShowingBottom(false));
                        item.setAmount(item.getAmount() - 1);
                    }
                });
            }
        }
    }
}

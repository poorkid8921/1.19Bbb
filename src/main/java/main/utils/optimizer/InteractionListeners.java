package main.utils.optimizer;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import main.utils.Instances.CustomPlayerDataHolder;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEnderCrystal;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.event.CraftEventFactory;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import static main.utils.Initializer.*;

public class InteractionListeners extends SimplePacketListenerAbstract {
    Holder<DamageType> cachedHolder = Holder.direct(new DamageType("player", 0.1f));

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        switch (event.getPacketType()) {
            case ANIMATION -> {
                Player player = (Player) event.getPlayer();
                if (player.hasPotionEffect(PotionEffectType.WEAKNESS))
                    return;
                CustomPlayerDataHolder user = playerData.get(player.getName());
                if (!user.isFastCrystals())
                    return;
                int lastPacket = user.getLastPacket();
                if (lastPacket == 3) return;
                if (user.isIgnoreAnim()) return;
                Bukkit.getScheduler().runTask(p, () -> {
                    Location eyeLoc = player.getEyeLocation();
                    RayTraceResult result = player.getWorld().rayTraceEntities(eyeLoc, player.getLocation().getDirection(), 3.0, 0.0,
                            entity -> {
                                if (entity.getType() != EntityType.PLAYER)
                                    return true;
                                Player p = (Player) entity;
                                return !player.getUniqueId().equals(p.getUniqueId()) && player.canSee(p);
                            });
                    if (result == null) return;
                    Entity entity = result.getHitEntity();
                    if (entity == null || entity.getType() != EntityType.ENDER_CRYSTAL) return;
                    if (entity.getTicksLived() == 0) return;
                    if (!entity.getBoundingBox().contains(eyeLoc.toVector())) {
                        RayTraceResult bResult = player.rayTraceBlocks(4.5D);
                        if (bResult != null && bResult.getHitBlock() != null) {
                            Vector eyeLocV = eyeLoc.toVector();
                            if (eyeLocV.distanceSquared(bResult.getHitPosition()) <=
                                    eyeLocV.distanceSquared(result.getHitPosition()) ||
                                    (lastPacket != 1 && lastPacket != 2))
                                return;
                        }
                    }
                    event.getUser().sendPacket(new WrapperPlayServerDestroyEntities(entity.getEntityId()));
                    EndCrystal endCrystal = ((CraftEnderCrystal) entity).getHandle();
                    if (!endCrystal.isRemoved()) {
                        DamageSource damageSource = new DamageSource(cachedHolder, ((CraftPlayer) player).getHandle());
                        if (CraftEventFactory.handleNonLivingEntityDamageEvent(endCrystal, damageSource, 1D, false))
                            return;
                        endCrystal.remove(net.minecraft.world.entity.Entity.RemovalReason.KILLED);
                        DamageSource damagesource1 = endCrystal.damageSources().explosion(endCrystal, damageSource.getEntity());
                        ExplosionPrimeEvent event1 = new ExplosionPrimeEvent(endCrystal.getBukkitEntity(), 6.0F, false);
                        endCrystal.level.getCraftServer().getPluginManager().callEvent(event1);
                        if (!event1.isCancelled()) {
                            endCrystal.level.explode(endCrystal, damagesource1, null, endCrystal.getX(), endCrystal.getY(), endCrystal.getZ(), 6.0F, false, Level.ExplosionInteraction.BLOCK);
                        } else
                            endCrystal.unsetRemoved();
                    }
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
                Location blockLoc = entity.clone().subtract(0.5D, 1.0D, 0.5D);
                RayTraceResult result = player.rayTraceBlocks(4.5D, FluidCollisionMode.NEVER);
                if (result == null || result.getHitBlock().getType() != Material.OBSIDIAN) return;
                if (!result.getHitBlock().getLocation().equals(blockLoc)) return;
                Bukkit.getScheduler().runTask(p, () -> {
                    Location clonedLoc = entity.clone().subtract(0.5D, 0.0D, 0.5D);
                    if (clonedLoc.getBlock().getType() != Material.AIR) return;

                    clonedLoc.add(0.5D, 1.0D, 0.5D);
                    if (clonedLoc.getWorld().getNearbyEntities(clonedLoc, 0.5D, 1D, 0.5D).isEmpty()) {
                        entity.getWorld().spawn(clonedLoc.subtract(0.0D, 1.0D, 0.0D), EnderCrystal.class, c -> c.setShowingBottom(false));
                        item.setAmount(item.getAmount() - 1);
                    }
                });
            }
        }
    }
}
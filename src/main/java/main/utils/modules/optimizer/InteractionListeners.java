package main.utils.modules.optimizer;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import main.managers.instances.PlayerDataHolder;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import org.bukkit.*;
import org.bukkit.block.Block;
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

import static main.utils.Initializer.crystalsToBeOptimized;
import static main.utils.Initializer.playerData;

public class InteractionListeners extends SimplePacketListenerAbstract {
    private static final Holder<DamageType> cachedHolder = Holder.direct(new DamageType("player", 0.1f));

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        switch (event.getPacketType()) {
            case ANIMATION -> {
                final Player player = (Player) event.getPlayer();
                if (player.hasPotionEffect(PotionEffectType.WEAKNESS))
                    return;
                final String name = player.getName();
                final PlayerDataHolder user = playerData.get(name);
                if (!user.isFastCrystals() || player.getPing() < 50)
                    return;
                final int lastPacket = user.getLastPacket();
                if (lastPacket == 3 || user.isIgnoreAnim()) return;
                final Location eyeLoc = player.getEyeLocation();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    final RayTraceResult result = eyeLoc.getWorld().rayTraceEntities(eyeLoc, player.getLocation().getDirection(), 3.0, 0.0,
                            entity -> {
                                if (entity.getType() != EntityType.PLAYER)
                                    return true;
                                Player p = (Player) entity;
                                return !name.equals(p.getName()) && player.canSee(p);
                            });
                    if (result == null) return;
                    final Entity entity = result.getHitEntity();
                    if (entity == null || entity.getType() != EntityType.ENDER_CRYSTAL) return;
                    if (entity.getTicksLived() == 0) return;
                    if (!entity.getBoundingBox().contains(eyeLoc.toVector())) {
                        final RayTraceResult bResult = player.rayTraceBlocks(4.5D);
                        if (bResult != null && bResult.getHitBlock() != null) {
                            final Vector eyeLocV = eyeLoc.toVector();
                            if (eyeLocV.distanceSquared(bResult.getHitPosition()) <=
                                    eyeLocV.distanceSquared(result.getHitPosition()) ||
                                    (lastPacket != 1 && lastPacket != 2))
                                return;
                        }
                    }
                    final EndCrystal endCrystal = ((CraftEnderCrystal) entity).getHandle();
                    if (!endCrystal.isRemoved()) {
                        final DamageSource damageSource = new DamageSource(cachedHolder, ((CraftPlayer) player).getHandle());
                        if (CraftEventFactory.handleNonLivingEntityDamageEvent(endCrystal, damageSource, 1D, false))
                            return;
                        endCrystal.remove(net.minecraft.world.entity.Entity.RemovalReason.KILLED);
                        final DamageSource damagesource1 = endCrystal.damageSources().explosion(endCrystal, damageSource.getEntity());
                        final ExplosionPrimeEvent event1 = new ExplosionPrimeEvent(endCrystal.getBukkitEntity(), 6.0F, false);
                        endCrystal.level.getCraftServer().getPluginManager().callEvent(event1);
                        if (!event1.isCancelled()) {
                            endCrystal.level.explode(endCrystal, damagesource1, null, endCrystal.getX(), endCrystal.getY(), endCrystal.getZ(), 6.0F, false, Level.ExplosionInteraction.BLOCK);
                        } else
                            endCrystal.unsetRemoved();
                    }
                });
            }
            case INTERACT_ENTITY -> {
                final WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
                if (wrapper.getAction() != WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT) return;
                final Player player = (Player) event.getPlayer();
                final ItemStack item = wrapper.getHand() == InteractionHand.MAIN_HAND ?
                        player.getInventory().getItemInMainHand() :
                        player.getInventory().getItemInOffHand();
                if (item.getType() != Material.END_CRYSTAL) return;
                final int entityId = wrapper.getEntityId();
                final Location entity = crystalsToBeOptimized.get(entityId);
                if (entity == null) return;
                final Location blockLoc = entity.subtract(0.5D, 1.0D, 0.5D);
                final RayTraceResult result = player.rayTraceBlocks(4.5D, FluidCollisionMode.NEVER);
                if (result == null) return;
                final Block hitBlock = result.getHitBlock();
                final Material material = hitBlock.getType();
                if (material != Material.OBSIDIAN &&
                        material != Material.BEDROCK)
                    return;
                if (!hitBlock.getLocation().equals(blockLoc)) return;
                final Location clonedLoc = entity.add(0D, 1.0D, 0D);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (clonedLoc.getBlock().getType() != Material.AIR) return;
                    clonedLoc.add(0.5D, 1.0D, 0.5D);
                    final World world = clonedLoc.getWorld();
                    if (world.getNearbyEntities(clonedLoc, 0.5D, 1D, 0.5D).isEmpty()) {
                        world.spawn(clonedLoc.subtract(0.0D, 1.0D, 0.0D), EnderCrystal.class, c -> c.setShowingBottom(false));
                        item.setAmount(item.getAmount() - 1);
                    }
                });
            }
        }
    }
}
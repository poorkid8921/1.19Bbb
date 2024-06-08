package main.utils;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import main.utils.Instances.AbstractRegionHolder;
import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

import static main.Practice.d;
import static main.utils.Initializer.*;

public class ProtectionEvents implements Listener {
    private final ObjectOpenHashSet<Block> inRegion = ObjectOpenHashSet.of();

    private void handleBlockPlace(BlockPlaceEvent e) {
        final Location location = e.getBlock().getLocation();
        if (location.getWorld() != d)
            return;
        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();
        final Player player = e.getPlayer();
        for (final AbstractRegionHolder region : regions) {
            if (!region.testY(x, y, z)) continue;
            if (player.isOp()) return;
            player.sendMessage(EXCEPTION_INTERACTION);
            e.setCancelled(true);
            return;
        }
    }

    private ObjectOpenHashSet<Block> handleExplosion(List<Block> blockList) {
        int x, y, z;
        for (final Block block : blockList) {
            x = block.getX();
            y = block.getY();
            z = block.getZ();
            for (final AbstractRegionHolder region : regions) {
                if (!region.testY(x, y, z)) continue;
                inRegion.add(block);
                break;
            }
        }
        return inRegion;
    }

    @EventHandler
    private void onBlockExplosion(BlockExplodeEvent e) {
        e.blockList().removeAll(handleExplosion(e.blockList()));
    }

    @EventHandler
    private void onEntityExplosion(EntityExplodeEvent e) {
        e.blockList().removeAll(handleExplosion(e.blockList()));
    }

    @EventHandler
    private void onPlayerBoostElytra(PlayerElytraBoostEvent e) {
        final Player player = e.getPlayer();
        if (inFlat.contains(player.getName())) {
            player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerShootArrow(EntityShootBowEvent e) {
        final Location location = e.getEntity().getLocation();
        if (!spawnRegionHolder.test(location.getBlockX(), location.getBlockZ())) return;
        e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent e) {
        final Block block = e.getClickedBlock();
        if (block == null) return;
        if (block.getType() == Material.SPRUCE_TRAPDOOR) {
            final Player player = e.getPlayer();
            if (player.isOp()) return;
            player.sendMessage(EXCEPTION_INTERACTION);
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageByEntityEvent e) {
        final Entity attacker = e.getDamager();
        final boolean playerAttacker = attacker instanceof Player;
        if (!(e.getEntity() instanceof Player damaged) || (!playerAttacker && !(attacker instanceof Arrow) && !(attacker instanceof ThrownPotion)))
            return;
        final Location location = damaged.getLocation();
        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();
        if (playerAttacker) {
            if (location.getWorld() == d)
                for (AbstractRegionHolder region : regions) {
                    if (!region.testY(x, y, z)) continue;
                    attacker.sendMessage(EXCEPTION_PVP);
                    e.setCancelled(true);
                    return;
                }
            final Player damager = (Player) attacker;
            final String damagerName = damager.getName();
            final String damagedName = damaged.getName();
            final CustomPlayerDataHolder D0 = playerData.get(damagedName);
            final CustomPlayerDataHolder D1 = playerData.get(damagerName);
            // check
            //if (!ModerationAssist.checkFlat(damager, damagerName, D0, D1))
            //return;
            D1.setLastTaggedBy(damagedName);
            D1.setLastTagged(System.currentTimeMillis());
            if (D1.isTagged()) D1.setTagTime(damager);
            else D1.setupCombatRunnable(damager);

            D0.setLastTaggedBy(damagerName);
            D0.setLastTagged(System.currentTimeMillis());
            if (D0.isTagged()) D0.setTagTime(damaged);
            else D0.setupCombatRunnable(damaged);
        } else {
            if (location.getWorld() == d) for (final AbstractRegionHolder region : regions) {
                if (!region.testY(x, y, z)) continue;
                e.setCancelled(true);
                return;
            }
            final CustomPlayerDataHolder D0 = playerData.get(damaged.getName());
            D0.setLastTagged(System.currentTimeMillis());
            if (D0.isTagged()) D0.setTagTime(damaged);
            else D0.setupCombatRunnable(damaged);
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        final Player player = e.getPlayer();
        final Location location = e.getBlock().getLocation();
        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();
        for (final AbstractRegionHolder region : regions) {
            if (!region.testY(x, y, z)) continue;
            if (player.isOp()) return;
            player.sendMessage(EXCEPTION_INTERACTION);
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent e) {
        handleBlockPlace(e);
    }

    @EventHandler
    private void onBlockPlace(BlockMultiPlaceEvent e) {
        handleBlockPlace(e);
    }

    @EventHandler
    private void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        final Player player = e.getPlayer();
        final String name = player.getName();
        if (inFlat.contains(name) || atSpawn.contains(name) || inFFA.contains(player))
            e.setCancelled(true);
    }

    @EventHandler
    private void onBlockBurn(BlockBurnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    private void onFireSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
    }
}

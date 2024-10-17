package main.utils;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import main.managers.instances.AbstractRegionHolder;
import main.managers.instances.PlayerDataHolder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

import static main.Economy.overworld;
import static main.Economy.spawnDistance;
import static main.managers.MessageManager.EXCEPTION_PVP;
import static main.utils.Initializer.*;

public class ProtectionEvents implements Listener {
    private final ObjectOpenHashSet<Block> inRegion = ObjectOpenHashSet.of();

    private void handleBlockPlace(BlockPlaceEvent e) {
        final Location location = e.getBlock().getLocation();
        if (location.getWorld() != overworld) return;

        final int x = location.getBlockX();
        final int z = location.getBlockZ();
        final int y = location.getBlockY();
        for (final AbstractRegionHolder region : regions) {
            if (!region.testY(x, y, z)) continue;
            final Player player = e.getPlayer();
            if (player.isOp()) return;
            player.sendMessage(EXCEPTION_INTERACTION);
            e.setCancelled(true);
            return;
        }
    }

    private ObjectOpenHashSet<Block> handleExplosion(List<Block> blockList) {
        int x, y, z;
        for (Block block : blockList) {
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
    private void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND && playerData.get(e.getPlayer().getName()).isTagged())
            e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerGlide(EntityToggleGlideEvent e) {
        final Player player = (Player) e.getEntity();
        if (playerData.get(player.getName()).isTagged()) {
            player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerShootArrow(EntityShootBowEvent e) {
        final Location location = e.getEntity().getLocation();
        if (location.getWorld() != overworld)
            return;

        if (!spawnRegionHolder.test(location.getBlockX(), location.getBlockZ()))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerPearlCollide(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            final Location location = e.getTo();
            if (location.getWorld() != overworld) return;

            if (!spawnRegionHolder.test(location.getBlockX(), location.getBlockZ())) return;

            final Player player = e.getPlayer();
            if ((System.currentTimeMillis() - playerData.get(player.getName()).getLastTagged()) > 30000L) return;

            player.sendMessage(EXCEPTION_INTERACTION);
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent e) {
        final Block block = e.getClickedBlock();
        if (block == null) return;

        if (block.getType() == Material.SPRUCE_TRAPDOOR) {
            final Player player = e.getPlayer();
            final Location location = player.getLocation();

            int x = location.getBlockX();
            int z = location.getBlockZ();

            for (AbstractRegionHolder region : regions) {
                if (!region.test(x, z)) continue;
                if (player.isOp()) return;
                player.sendMessage(EXCEPTION_INTERACTION);
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    private void onBlockExplosion(BlockExplodeEvent e) {
        if (e.getBlock().getWorld() != overworld)
            return;

        e.blockList().removeAll(handleExplosion(e.blockList()));
    }

    @EventHandler
    private void onEntityExplosion(EntityExplodeEvent e) {
        if (e.getEntity().getWorld() != overworld)
            return;

        e.blockList().removeAll(handleExplosion(e.blockList()));
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageByEntityEvent e) {
        final Entity damager = e.getDamager();
        final Entity entity = e.getEntity();
        if (damager instanceof EnderCrystal && entity instanceof Player damaged) {
            if (damaged.getWorld() != overworld) return;

            final Location location = damaged.getLocation();
            if (spawnRegionHolder.test(location.getBlockX(), location.getBlockZ())) e.setCancelled(true);
            return;
        }
        final boolean playerAttacker = damager instanceof Player;
        if (!(entity instanceof Player damaged) || (!playerAttacker && !(damager instanceof Arrow) && !(damager instanceof ThrownPotion)))
            return;
        final Location location = damaged.getLocation();
        final int x = location.getBlockX();
        final int z = location.getBlockZ();
        if (playerAttacker) {
            if (location.getWorld() == overworld && spawnRegionHolder.test(x, z)) {
                damager.sendMessage(EXCEPTION_PVP);
                e.setCancelled(true);
                return;
            }

            final PlayerDataHolder D0 = playerData.get(damaged.getName());
            if (D0.isTagged()) D0.setTagTime(damaged);
            else D0.setupCombatRunnable(damaged);
            D0.setLastTagged(System.currentTimeMillis());

            final PlayerDataHolder D1 = playerData.get(damager.getName());
            final Player damagePlayer = (Player) damager;
            if (D1.isTagged()) D1.setTagTime(damagePlayer);
            else D1.setupCombatRunnable(damagePlayer);
            D1.setLastTagged(System.currentTimeMillis());
        } else {
            if (location.getWorld() == overworld && spawnRegionHolder.test(x, z)) {
                e.setCancelled(true);
                return;
            }

            final PlayerDataHolder D0 = playerData.get(damaged.getName());
            if (D0.isTagged()) D0.setTagTime(damaged);
            else D0.setupCombatRunnable(damaged);
            D0.setLastTagged(System.currentTimeMillis());
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        final Location location = e.getBlock().getLocation();
        if (location.getWorld() != overworld) return;

        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();

        for (AbstractRegionHolder region : regions) {
            if (!region.testY(x, y, z)) continue;

            final Player player = e.getPlayer();
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
    private void onBucketEmpty(PlayerBucketEmptyEvent e) {
        final Location location = e.getBlock().getLocation();
        if (location.getWorld() != overworld) return;

        if (spawnDistance.distance(location.getBlockX(), location.getBlockZ()) > 128)
            return;

        e.setCancelled(true);
    }
}

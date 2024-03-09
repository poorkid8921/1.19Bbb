package main.utils;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import main.utils.instances.CustomPlayerDataHolder;
import main.utils.instances.RegionHolder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

import static main.Economy.d;
import static main.utils.Initializer.*;

public class ProtectionEvents implements Listener {
    ObjectOpenHashSet<Block> inRegion = ObjectOpenHashSet.of();

    private void handleBlockPlace(BlockPlaceEvent e) {
        Location loc = e.getBlock().getLocation();
        if (loc.getWorld() != d)
            return;
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for (RegionHolder r : regions) {
            if (!r.check(x, y, z))
                continue;
            Player p = e.getPlayer();
            if (p.isOp())
                return;
            p.sendMessage(EXCEPTION_BLOCK_PLACE);
            e.setCancelled(true);
            return;
        }
    }

    private ObjectOpenHashSet<Block> handleExplosion(List<Block> blockList) {
        for (Block b : blockList) {
            int x = b.getX();
            int y = b.getY();
            int z = b.getZ();
            for (RegionHolder r : regions) {
                if (!r.check(x, y, z))
                    continue;
                inRegion.add(b);
                break;
            }
        }
        return inRegion;
    }

    @EventHandler
    private void onPlayerGlide(EntityToggleGlideEvent e) {
        Player p = (Player) e.getEntity();
        if (playerData.get(p.getName()).isTagged()) {
            p.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerShootArrow(EntityShootBowEvent e) {
        Location loc = e.getEntity().getLocation();
        if (loc.getWorld() != d)
            return;
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for (RegionHolder r : regions) {
            if (!r.check(x, y, z))
                continue;
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    private void onPlayerPearlCollide(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Location loc = e.getTo();
            if (loc.getWorld() != d)
                return;
            if (!spawnRegionHolder.check(loc.getBlockX(), loc.getBlockZ()))
                return;
            Player p = e.getPlayer();
            if (playerData.get(p.getName()).isTagged()) {
                p.sendMessage("§7You can't pearl here!");
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent e) {
        Block b = e.getClickedBlock();
        if (b == null)
            return;
        if (b.getType() == Material.SPRUCE_TRAPDOOR) {
            Player p = e.getPlayer();
            Location loc = p.getLocation();
            int x = loc.getBlockX();
            int z = loc.getBlockZ();
            for (RegionHolder r : regions) {
                if (!r.check(x, z))
                    continue;
                if (p.isOp())
                    return;
                p.sendMessage(EXCEPTION_INTERACTION);
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    private void onBlockExplosion(BlockExplodeEvent e) {
        if (e.getBlock().getWorld() == d)
            e.blockList().removeAll(handleExplosion(e.blockList()));
    }

    @EventHandler
    private void onEntityExplosion(EntityExplodeEvent e) {
        if (e.getEntity().getWorld() == d)
            e.blockList().removeAll(handleExplosion(e.blockList()));
    }

    @EventHandler
    private void onAnvilDamage(BlockDamageEvent e) {
        Block b = e.getBlock();
        if (b.getType() != Material.DAMAGED_ANVIL)
            return;
        if (spawnRegionHolder.check(b.getX(), b.getZ()))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled())
            return;
        Entity attacker = e.getDamager();
        EntityType entType = attacker.getType();
        boolean playerAttacker = entType == EntityType.PLAYER;
        Entity ent = e.getEntity();
        if (ent.getType() != EntityType.PLAYER ||
                (!playerAttacker && entType != EntityType.SPLASH_POTION) && entType != EntityType.ARROW)
            return;
        Player p = (Player) ent;
        Location loc = p.getLocation();
        if (loc.getWorld() != d)
            return;
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        if (playerAttacker) {
            if (spawnRegionHolder.check(x, z)) {
                attacker.sendMessage("§7You can't combat here!");
                e.setCancelled(true);
                return;
            }
            CustomPlayerDataHolder D0 = playerData.get(p.getName());
            if (D0.isTagged())
                D0.setTagTime(p);
            else
                D0.setupCombatRunnable(p);
            Player damagePlayer = (Player) attacker;
            CustomPlayerDataHolder D1 = playerData.get(attacker.getName());
            if (D1.isTagged())
                D1.setTagTime(damagePlayer);
            else
                D1.setupCombatRunnable(damagePlayer);
        } else {
            if (spawnRegionHolder.check(x, z)) {
                e.setCancelled(true);
                return;
            }
            CustomPlayerDataHolder D0 = playerData.get(p.getName());
            if (D0.isTagged())
                D0.setTagTime(p);
            else
                D0.setupCombatRunnable(p);
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        Location loc = e.getBlock().getLocation();
        if (loc.getWorld() != d)
            return;
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for (RegionHolder r : regions) {
            if (!r.check(x, y, z))
                continue;
            Player p = e.getPlayer();
            if (p.isOp())
                return;
            p.sendMessage(EXCEPTION_BLOCK_BREAK);
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
        Location loc = e.getBlock().getLocation();
        if (spawnRegionHolder.check(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
            Player p = e.getPlayer();
            if (p.isOp())
                return;
            e.setCancelled(true);
        }
    }
}

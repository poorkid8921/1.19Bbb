package main.utils;

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import main.utils.instances.AbstractRegionHolder;
import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

import static main.Economy.d;
import static main.Economy.spawnDistance;
import static main.utils.Initializer.*;

public class ProtectionEvents implements Listener {
    private final ObjectOpenHashSet<Block> inRegion = ObjectOpenHashSet.of();

    private void handleBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        if (block.getWorld() != d) return;
        Location loc = block.getLocation();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        if (flatRegionHolder.test(x, z) && block.getType() != Material.OBSIDIAN) {
            if (!e.getPlayer().isOp()) e.setCancelled(true);
            return;
        }
        int y = loc.getBlockY();
        for (AbstractRegionHolder r : regions) {
            if (!r.testY(x, y, z)) continue;
            Player p = e.getPlayer();
            if (p.isOp()) return;
            p.sendMessage(EXCEPTION_INTERACTION);
            e.setCancelled(true);
            return;
        }
    }

    private ObjectOpenHashSet<Block> handleExplosion(List<Block> blockList) {
        for (Block b : blockList) {
            int x = b.getX();
            int y = b.getY();
            int z = b.getZ();
            for (AbstractRegionHolder r : regions) {
                if (!r.testY(x, y, z)) continue;
                inRegion.add(b);
                break;
            }
        }
        return inRegion;
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND &&
                playerData.get(e.getPlayer().getName()).isTagged())
            e.setCancelled(true);
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
        if (loc.getWorld() == d && spawnRegionHolder.test(loc.getBlockX(), loc.getBlockZ()))
            e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerPearlCollide(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Location loc = e.getTo();
            if (loc.getWorld() != d) return;
            if (!spawnRegionHolder.test(loc.getBlockX(), loc.getBlockZ())) return;
            Player p = e.getPlayer();
            CustomPlayerDataHolder D0 = playerData.get(p.getName());
            if ((System.currentTimeMillis() - D0.getLastTagged()) > 30000L)
                return;
            p.sendMessage(EXCEPTION_INTERACTION);
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent e) {
        Block b = e.getClickedBlock();
        if (b == null) return;
        if (b.getType() == Material.SPRUCE_TRAPDOOR) {
            Player p = e.getPlayer();
            Location loc = p.getLocation();
            int x = loc.getBlockX();
            int z = loc.getBlockZ();
            for (AbstractRegionHolder r : regions) {
                if (!r.test(x, z)) continue;
                if (p.isOp()) return;
                p.sendMessage(EXCEPTION_INTERACTION);
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    private void onBlockExplosion(BlockExplodeEvent e) {
        if (e.getBlock().getWorld() == d) e.blockList().removeAll(handleExplosion(e.blockList()));
    }

    @EventHandler
    private void onEntityExplosion(EntityExplodeEvent e) {
        if (e.getEntity().getWorld() == d) e.blockList().removeAll(handleExplosion(e.blockList()));
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;
        Entity attacker = e.getDamager();
        Entity ent = e.getEntity();
        if (attacker instanceof EnderCrystal && ent instanceof Player p) {
            if (p.getWorld() != d) return;
            Location loc = p.getLocation();
            if (spawnRegionHolder.test(loc.getBlockX(), loc.getBlockZ()))
                e.setCancelled(true);
            return;
        }
        boolean playerAttacker = attacker instanceof Player;
        if (!(ent instanceof Player p) ||
                (!playerAttacker &&
                        !(attacker instanceof Arrow) &&
                        !(attacker instanceof ThrownPotion)))
            return;
        Location loc = p.getLocation();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        if (playerAttacker) {
            if (p.getWorld() == d && spawnRegionHolder.test(x, z)) {
                attacker.sendMessage(EXCEPTION_PVP);
                e.setCancelled(true);
                return;
            }
            CustomPlayerDataHolder D0 = playerData.get(p.getName());
            if (D0.isTagged()) D0.setTagTime(p);
            else D0.setupCombatRunnable(p);
            D0.setLastTagged(System.currentTimeMillis());
            Player damagePlayer = (Player) attacker;
            CustomPlayerDataHolder D1 = playerData.get(attacker.getName());
            if (D1.isTagged()) D1.setTagTime(damagePlayer);
            else D1.setupCombatRunnable(damagePlayer);
            D1.setLastTagged(System.currentTimeMillis());
        } else {
            if (p.getWorld() == d && spawnRegionHolder.test(x, z)) {
                e.setCancelled(true);
                return;
            }
            CustomPlayerDataHolder D0 = playerData.get(p.getName());
            if (D0.isTagged()) D0.setTagTime(p);
            else D0.setupCombatRunnable(p);
            D0.setLastTagged(System.currentTimeMillis());
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        Location loc = e.getBlock().getLocation();
        if (loc.getWorld() != d) return;
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for (AbstractRegionHolder r : regions) {
            if (!r.testY(x, y, z)) continue;
            Player p = e.getPlayer();
            if (p.isOp()) return;
            p.sendMessage(EXCEPTION_INTERACTION);
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
        Block block = e.getBlock();
        if (block.getWorld() != d) return;
        Location loc = block.getLocation();
        if (spawnRegionHolder.test(loc.getBlockX(), loc.getBlockZ()))
            e.setCancelled(true);
    }
}

package main.utils;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import main.utils.Instances.AbstractRegionHolder;
import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

import static main.Practice.d;
import static main.utils.Initializer.*;

public class ProtectionEvents implements Listener {
    ObjectOpenHashSet<Block> inRegion = ObjectOpenHashSet.of();

    private void handleBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (inNethpot.contains(p.getName())) {
            if (!p.isOp())
                e.setCancelled(true);
            return;
        }
        Location loc = e.getBlock().getLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for (AbstractRegionHolder r : regions) {
            if (!r.testY(x, y, z))
                continue;
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
            for (AbstractRegionHolder r : regions) {
                if (!r.testY(x, y, z))
                    continue;
                inRegion.add(b);
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
    private void onPlayerShootArrow(EntityShootBowEvent e) {
        Location loc = e.getEntity().getLocation();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        if (!spawnRegionHolder.test(x, z) && !nethPotRegionHolder.test(x, z))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent e) {
        Block b = e.getClickedBlock();
        if (b == null)
            return;
        if (b.getType() == Material.SPRUCE_TRAPDOOR) {
            Player p = e.getPlayer();
            if (p.isOp())
                return;
            p.sendMessage(EXCEPTION_INTERACTION);
            e.setCancelled(true);
        }
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
        if (inNethpot.contains(p.getName())) {
            Location loc = p.getLocation();
            if (nethPotRegionHolder.test(loc.getBlockX(), loc.getBlockZ()))
                e.setCancelled(true);
            return;
        }
        Location loc = p.getLocation();
        if (loc.getWorld() != d)
            return;
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        if (playerAttacker) {
            for (AbstractRegionHolder r : regions) {
                if (!r.testY(x, y, z))
                    continue;
                e.setCancelled(true);
                return;
            }
            Player damagePlayer = (Player) attacker;
            String name = damagePlayer.getName();
            CustomPlayerDataHolder D1 = playerData.get(name);
            if (D1.isTagged())
                D1.setTagTime(damagePlayer);
            else
                D1.setupCombatRunnable(damagePlayer);
            String pn = p.getName();
            CustomPlayerDataHolder D0 = playerData.get(pn);
            if (D0.isTagged())
                D0.setTagTime(p);
            else {
                D0.setLastTaggedBy(name);
                D0.setupCombatRunnable(p);

                D1.setLastTaggedBy(pn);
            }
        } else {
            for (AbstractRegionHolder r : regions) {
                if (!r.testY(x, y, z))
                    continue;
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
        Player p = e.getPlayer();
        if (inNethpot.contains(p.getName())) {
            if (p.isOp())
                return;
            p.sendMessage(EXCEPTION_BLOCK_BREAK);
            e.setCancelled(true);
            return;
        }
        Location loc = e.getBlock().getLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for (AbstractRegionHolder r : regions) {
            if (!r.testY(x, y, z))
                continue;
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

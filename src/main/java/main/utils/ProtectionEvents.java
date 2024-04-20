package main.utils;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import main.utils.Instances.AbstractRegionHolder;
import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

import static main.Practice.d;
import static main.utils.Initializer.*;
import static main.utils.Utils.banEffect;

public class ProtectionEvents implements Listener {
    private final ObjectOpenHashSet<Block> inRegion = ObjectOpenHashSet.of();

    private void handleBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Location loc = e.getBlock().getLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for (AbstractRegionHolder r : regions) {
            if (!r.testY(x, y, z)) continue;
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
        if (!spawnRegionHolder.test(x, z)) return;
        e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent e) {
        Block b = e.getClickedBlock();
        if (b == null) return;
        if (b.getType() == Material.SPRUCE_TRAPDOOR) {
            Player p = e.getPlayer();
            if (p.isOp()) return;
            p.sendMessage(EXCEPTION_INTERACTION);
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;
        Entity attacker = e.getDamager();
        boolean playerAttacker = attacker instanceof Player;
        Entity ent = e.getEntity();
        if (!(ent instanceof Player p) || (!playerAttacker && !(attacker instanceof Arrow) && !(attacker instanceof ThrownPotion)))
            return;
        Location loc = p.getLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        if (playerAttacker) {
            if (p.getWorld() == d) for (AbstractRegionHolder r : regions) {
                if (!r.testY(x, y, z)) continue;
                attacker.sendMessage(EXCEPTION_PVP);
                e.setCancelled(true);
                return;
            }
            Player damagePlayer = (Player) attacker;
            String name = damagePlayer.getName();
            String pn = p.getName();
            CustomPlayerDataHolder D1 = playerData.get(name);
            D1.setLastTaggedBy(pn);
            D1.setLastTagged(System.currentTimeMillis());
            if (D1.isTagged()) D1.setTagTime(damagePlayer);
            else D1.setupCombatRunnable(damagePlayer);
            CustomPlayerDataHolder D0 = playerData.get(pn);
            String lastTaggedBy = D0.getLastTaggedBy();
            if (inFlat.contains(name) &&
                    lastTaggedBy != null &&
                    lastTaggedBy != name &&
                    D1.incrementFlatFlags() == 5) {
                D0.setFlatFlags(0);
                Initializer.bannedFromflat.add(name);
                banEffect(damagePlayer);
                p.teleportAsync(spawn, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
                    damagePlayer.sendMessage("§7You are now banned in flat for " + SECOND_COLOR + "Interrupting");
                    atSpawn.add(name);
                });
            }
            D0.setLastTaggedBy(name);
            D0.setLastTagged(System.currentTimeMillis());
            if (D0.isTagged()) D0.setTagTime(p);
            else {
                D0.setLastTaggedBy(name);
                D0.setupCombatRunnable(p);
            }
        } else {
            if (p.getWorld() == d) for (AbstractRegionHolder r : regions) {
                if (!r.testY(x, y, z)) continue;
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
        Player p = e.getPlayer();
        Location loc = e.getBlock().getLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for (AbstractRegionHolder r : regions) {
            if (!r.testY(x, y, z)) continue;
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

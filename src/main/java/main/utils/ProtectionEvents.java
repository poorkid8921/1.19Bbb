package main.utils;

import main.utils.instances.CustomPlayerDataHolder;
import main.utils.instances.RegionHolder;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import static main.utils.Constants.*;
import static main.utils.Constants.EXCEPTION_BLOCK_PLACE;

public class ProtectionEvents implements Listener {
    @EventHandler
    private void onPlayerShootArrow(EntityShootBowEvent e) {
        Location loc = e.getEntity().getLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for (RegionHolder r : regions) {
            if (r.checkX(x) ||
                    r.checkY(y) ||
                    r.checkZ(z))
                continue;
            e.setCancelled(true);
        }
    }
    @EventHandler
    private void onPlayerPearlCollide(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Location loc = e.getTo();
            int x = loc.getBlockX();
            int z = loc.getBlockZ();
            if (spawnRegionHolder.checkX(x) ||
                    spawnRegionHolder.checkZ(z))
                return;
            e.getPlayer().sendMessage("§7You can't pearl here.");
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onBlockExplosion(BlockExplodeEvent e) {
        for (Block b : e.blockList()) {
            int x = b.getX();
            int y = b.getY();
            int z = b.getZ();
            for (RegionHolder r : regions) {
                if (r.checkX(x) ||
                        r.checkY(y) ||
                        r.checkZ(z))
                    continue;
                e.blockList().remove(b);
            }
            return;
        }
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        if (e.getClickedBlock() instanceof TrapDoor) {
            Player p = e.getPlayer();
            Location loc = p.getLocation();
            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();
            for (RegionHolder r : regions) {
                if (r.checkX(x) ||
                        r.checkY(y) ||
                        r.checkZ(z))
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
    private void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player p) ||
                !(e.getDamager() instanceof Player attacker))
            return;

        Location loc = p.getLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for (RegionHolder r : regions) {
            if (r.checkX(x) ||
                    r.checkY(y) ||
                    r.checkZ(z))
                continue;
            attacker.sendMessage("§7You can't combat here!");
            e.setCancelled(true);
            return;
        }

        CustomPlayerDataHolder D0 = playerData.get(p.getName());
        if (D0.isTagged())
            D0.setTagTime(p);
        else
            D0.setupCombatRunnable(p);

        CustomPlayerDataHolder D1 = playerData.get(attacker.getName());
        if (D1.isTagged())
            D1.setTagTime(attacker);
        else
            D1.setupCombatRunnable(attacker);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        Location loc = e.getBlock().getLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for (RegionHolder r : regions) {
            if (r.checkX(x) ||
                    r.checkY(y) ||
                    r.checkZ(z))
                continue;
            Player p = e.getPlayer();
            if (p.isOp())
                return;
            p.sendMessage(EXCEPTION_BLOCK_BREAK);
            e.setCancelled(true);
            return;
        }
    }

    void handleBlockPlace(BlockPlaceEvent e) {
        Location loc = e.getBlock().getLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for (RegionHolder r : regions) {
            if (r.checkX(x) ||
                    r.checkY(y) ||
                    r.checkZ(z))
                continue;
            Player p = e.getPlayer();
            if (p.isOp())
                return;
            p.sendMessage(EXCEPTION_BLOCK_PLACE);
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
}
